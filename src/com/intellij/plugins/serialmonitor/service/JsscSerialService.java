package com.intellij.plugins.serialmonitor.service;

import com.intellij.plugins.serialmonitor.SerialMonitorException;
import com.intellij.util.Consumer;
import com.intellij.util.containers.HashSet;
import jssc.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Dmitry_Cherkas
 */
class JsscSerialService implements SerialService {

    private SerialPort port;
    private final Set<Consumer<String>> dataListeners = Collections.synchronizedSet(new HashSet<Consumer<String>>());
    private final Set<Consumer<Boolean>> portStateListeners = Collections.synchronizedSet(new HashSet<Consumer<Boolean>>());

    @Override
    public List<String> getPortNames() {
        String[] portNames;
        if(SerialNativeInterface.getOsType() == SerialNativeInterface.OS_MAC_OS_X) {
            portNames = SerialPortList.getPortNames(Pattern.compile("(tty.(serial|usbserial|usbmodem)|cu).*"));
        } else {
            portNames = SerialPortList.getPortNames();
        }
        return Arrays.asList(portNames);
    }

    public boolean isConnected() {
        return port != null;
    }

    @Override
    public void connect(String portName, int baudRate) {
        int dataBits = SerialPort.DATABITS_8;
        int stopBits = SerialPort.STOPBITS_1;
        int parity = SerialPort.PARITY_NONE;

        try {
            port = new SerialPort(portName);
            port.openPort();
            boolean res = port.setParams(baudRate, dataBits, stopBits, parity, true, true);
            if (!res) {
                throw new SerialMonitorException("Failed to set SerialPort parameters");
            }
            port.addEventListener(new MySerialPortEventListener());
        } catch (SerialPortException e) {
            if (e.getPortName().startsWith("/dev") && SerialPortException.TYPE_PERMISSION_DENIED.equals(e.getExceptionType())) {
                throw new SerialMonitorException(String.format("Error opening serial port \"%s\".", portName));
            }
            port = null;
            throw new SerialMonitorException(String.format("Error opening serial port \"%s\".", portName), e);
        }

        if (port == null) {
            throw new SerialMonitorException(String.format("Serial port \"%s\" not found.", portName));
        }
        notifyStateListeners(true); // notify successful connect
    }

    @Override
    public void close() {
        if (port != null) {
            try {
                if (port.isOpened()) {
                    port.removeEventListener();
                    port.closePort();  // close the port
                }
            } catch (SerialPortException e) {
                throw new SerialMonitorException(e.getMessage(), e);
            } finally {
                port = null;
                notifyStateListeners(false); // notify disconnect
            }
        }
    }

    @Override
    public void write(byte[] bytes) {
        if (!isConnected()) {
            throw new IllegalStateException("Port is not opened!");
        }
        try {
            port.writeBytes(bytes);
        } catch (SerialPortException e) {
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

    @Override
    public void removeDataListener(Consumer<String> listener) {
        dataListeners.remove(listener);
    }

    @Override
    public void addPortStateListener(Consumer<Boolean> listener) {
        portStateListeners.add(listener);
    }

    @Override
    public void removePortStateListener(Consumer<Boolean> listener) {
        portStateListeners.remove(listener);
    }

    private void notifyStateListeners(boolean isConnected) {
        for (Consumer<Boolean> listener : portStateListeners) {
            listener.consume(isConnected);
        }
    }

    @Override
    public void dispose() {
        dataListeners.clear();
        portStateListeners.clear();
        close();
    }

    private class MySerialPortEventListener implements SerialPortEventListener {
        @Override
        public synchronized void serialEvent(SerialPortEvent serialEvent) {
            if (serialEvent.isRXCHAR()) {
                if (dataListeners.isEmpty()) {
                    return;
                }
                try {
                    byte[] buf = port.readBytes(serialEvent.getEventValue());
                    if (buf.length > 0) {
                        // quick and dirty fix for https://bitbucket.org/dmitry_cherkas/intellij-serial-monitor/issues/1
                        String msg = new String(buf).replaceAll("\r", "");
                        for (Consumer<String> dataListener : dataListeners) {
                            dataListener.consume(msg);
                        }
                    }
                } catch (SerialPortException e) {
                    throw new SerialMonitorException(e.getMessage(), e);
                }
            }
        }
    }
}
