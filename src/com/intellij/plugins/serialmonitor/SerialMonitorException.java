package com.intellij.plugins.serialmonitor;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorException extends RuntimeException {
    public SerialMonitorException() {
    }

    public SerialMonitorException(String message) {
        super(message);
    }

    public SerialMonitorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerialMonitorException(Throwable cause) {
        super(cause);
    }

    public SerialMonitorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
