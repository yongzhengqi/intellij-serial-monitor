package com.intellij.plugins.serialmonitor.service;

import com.intellij.util.Consumer;

import java.util.List;

/**
 * @author Dmitry_Cherkas
 */
public interface SerialService {
    List<String> getPortNames();

    boolean isConnected();

    void connect(String portName, int baudRate);

    void close();

    String read();

    void write(byte[] bytes);

    void addDataListener(Consumer<String> listener);

    void addPortStateListener(Consumer<Boolean> listener);
}
