package com.intellij.plugins.serialmonitor.service;

import com.intellij.openapi.ui.Messages;
import com.intellij.plugins.serialmonitor.SerialMonitorException;
import purejavacomm.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * @author Dmitry_Cherkas
 */
public class RxTxSerialService implements SerialService {

    private static final int OPEN_TIMEOUT_MILLIS = 2000;
    public static final int READ_TIMEOUT_MILLIS = 100;

    private int dataBits = SerialPort.DATABITS_8;
    private int stopBits = SerialPort.STOPBITS_1;
    private int parity = SerialPort.PARITY_NONE;

    private SerialPort serialPort;

    @Override
    public List<String> getPortNames() {
        List<String> portNames = new ArrayList<String>();

        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        while (portIdentifiers.hasMoreElements()) {
            CommPortIdentifier portIdentifier = (CommPortIdentifier) portIdentifiers.nextElement();
            portNames.add(portIdentifier.getName());
        }
        return portNames;
    }

    @Override
    public void connect(String portName, int baudRate) {
        CommPortIdentifier portIdentifier;
        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        } catch (NoSuchPortException e) {
            throw new SerialMonitorException(e.getMessage(), e);
        }

        if (portIdentifier.isCurrentlyOwned()) {
            Messages.showInfoMessage("Port is currently in use.", "Error");
        } else {
            CommPort commPort;
            try {
                commPort = portIdentifier.open(this.getClass().getName(), OPEN_TIMEOUT_MILLIS);
            } catch (PortInUseException e) {
                throw new SerialMonitorException(e.getMessage(), e);
            }

            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;

                try {
                    serialPort.enableReceiveTimeout(READ_TIMEOUT_MILLIS);
                    serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
                    serialPort.notifyOnDataAvailable(true);

                    // start listening for incoming data
                    serialPort.addEventListener(new SerialPortEventListener() {
                        @Override
                        public void serialEvent(SerialPortEvent event) {
                            if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                                Messages.showInfoMessage(read(), "Info");
                            }
                        }
                    });
                } catch (UnsupportedCommOperationException | TooManyListenersException e) {
                    throw new SerialMonitorException(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void close() {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }

    @Override
    public String read() {
        try {
            return new String(read(serialPort.getInputStream()));
        } catch (IOException e) {
            throw new SerialMonitorException(e.getMessage(), e);
        }
    }

    private byte[] read(InputStream is) throws IOException {
        byte[] buffer = new byte[8192];

        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        // is.read is blocking for READ_TIMEOUT_MILLIS. If nothing was read (bytesRead <= 0), then there is no data available and we should quit.
        while ((bytesRead = is.read(buffer)) > 0) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }
}
