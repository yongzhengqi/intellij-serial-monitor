package com.intellij.plugins.serialmonitor.ui;

import com.intellij.plugins.serialmonitor.SerialMonitorException;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorSettingsValidationException extends SerialMonitorException {
    private final String title;

    public SerialMonitorSettingsValidationException(String title, String message) {
        super(message);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static SerialMonitorSettingsValidationException error(String msgKey) {
        return new SerialMonitorSettingsValidationException(message("validation-error.title"), message(msgKey));
    }
}
