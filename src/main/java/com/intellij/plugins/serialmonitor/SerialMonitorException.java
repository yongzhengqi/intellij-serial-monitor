package com.intellij.plugins.serialmonitor;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorException extends RuntimeException {

    public SerialMonitorException(String message) {
        super(message);
    }

    public SerialMonitorException(String message, Throwable cause) {
        super(message, cause);
    }
}
