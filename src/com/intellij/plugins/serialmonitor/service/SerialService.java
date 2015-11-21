package com.intellij.plugins.serialmonitor.service;

import com.intellij.openapi.Disposable;
import com.intellij.util.Consumer;

import java.util.List;

/**
 * @author Dmitry_Cherkas
 */
public interface SerialService extends Disposable {

    List<String> getPortNames();

    boolean isConnected();

    void connect(String portName, int baudRate);

    void close();

    void write(byte[] bytes);

    void addDataListener(Consumer<String> listener);

    void removeDataListener(Consumer<String> listener);

    void addPortStateListener(Consumer<Boolean> listener);

    void removePortStateListener(Consumer<Boolean> listener);
}
