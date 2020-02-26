package com.intellij.plugins.serialmonitor.service;

import com.intellij.util.messages.Topic;

/**
 * @author Dmitry Cherkas
 */
public interface SerialMonitorSettingsChangeListener {
    Topic<SerialMonitorSettingsChangeListener> TOPIC = Topic.create("SerialMonitor settings", SerialMonitorSettingsChangeListener.class);

    void settingsChanged(SerialMonitorSettings newSettings);
}
