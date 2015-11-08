package com.intellij.plugins.serialmonitor.service;

import com.intellij.plugins.serialmonitor.SerialMonitorException;
import com.intellij.util.Consumer;
import purejavacomm.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Dmitry_Cherkas
 */
class RxTxSerialService implements SerialService {

    private static final int OPEN_TIMEOUT_MILLIS = 2000;
    public static final int READ_TIMEOUT_MILLIS = 100;

    private int dataBits = SerialPort.DATABITS_8;
    private int stopBits = SerialPort.STOPBITS_1;
    private int parity = SerialPort.PARITY_NONE;

    private SerialPort port;
    private final Set<Consumer<String>> dataListeners = Collections.synchronizedSet(new HashSet<Consumer<String>>());

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

    public boolean isConnected() {
        return port != null;
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
            throw new SerialMonitorException("Port is currently in use.");
        } else {
            CommPort commPort;
            try {
                commPort = portIdentifier.open(this.getClass().getName(), OPEN_TIMEOUT_MILLIS);
            } catch (PortInUseException e) {
                throw new SerialMonitorException(e.getMessage(), e);
            }

            if (commPort instanceof SerialPort) {
                port = (SerialPort) commPort;

                try {
                    port.enableReceiveTimeout(READ_TIMEOUT_MILLIS);
                    port.setSerialPortParams(baudRate, dataBits, stopBits, parity);
                    port.notifyOnDataAvailable(true);

                    // start listening for incoming data
                    port.addEventListener(new SerialPortEventListener() {
                        @Override
                        public void serialEvent(SerialPortEvent event) {
                            if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                                if (dataListeners.isEmpty()) {
                                    return;
                                }

                                String data = read();
                                for (Consumer<String> dataListener : dataListeners) {
                                    dataListener.consume(data);
                                }
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
        if (port != null) {
            port.close();
            port = null;
        }
    }

    @Override
    public String read() {
        try {
            return new String(read(port.getInputStream()));
        } catch (IOException e) {
            throw new SerialMonitorException(e.getMessage(), e);
        }
    }

    @Override
    public void write(byte[] bytes) {
        if (!isConnected()) {
            throw new IllegalStateException("Port is not opened!");
        }
        try {
            port.getOutputStream().write(bytes);
        } catch (IOException e) {
            throw new SerialMonitorException(e.getMessage(), e);
        }
    }

    @Override
    public void addDataListener(Consumer<String> listener) {
        if (listener == null || dataListeners.contains(listener)) {
            throw new IllegalArgumentException();
        }
        dataListeners.add(listener);
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
