package com.intellij.plugins.serialmonitor.service;

import java.util.List;

/**
 * @author Dmitry_Cherkas
 */
public interface SerialService {
    List<String> getPortNames();

    void connect(String portName, int baudRate);

    void close();

    String read();
}
