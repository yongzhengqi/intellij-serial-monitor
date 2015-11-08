package com.intellij.plugins.serialmonitor.service;

/**
 * @author Dmitry_Cherkas
 */
public interface SerialMonitorSettings {
    String getPortName();

    void setPortName(String portName);

    int getBaudRate();

    void setBaudRate(int baudRate);

    boolean isValid();
}
