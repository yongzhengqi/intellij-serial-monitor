package com.intellij.plugins.serialmonitor;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.ui.SerialMonitorSettingsPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorConfigurable implements SearchableConfigurable {

    private SerialMonitorSettingsPanel mySettingsPanel;
    private final SerialMonitorSettings mySettings;

    public SerialMonitorConfigurable() {
        mySettings = SerialMonitorSettings.getInstance();
    }

    @NotNull
    @Override
    public String getId() {
        return "settings.serialmonitor";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return message("display-name");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return getId();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (mySettingsPanel == null) {
            mySettingsPanel = new SerialMonitorSettingsPanel();
        }
        reset();
        return mySettingsPanel.getComponent();
    }

    @Override
    public boolean isModified() {
        return mySettingsPanel == null
                || !mySettings.getPortName().equals(mySettingsPanel.getSelectedPortName())
                || mySettings.getBaudRate()!= mySettingsPanel.getSelectedBaudRate();
    }

    @Override
    public void apply() throws ConfigurationException {
        if (mySettingsPanel != null) {
            mySettings.setPortName(mySettingsPanel.getSelectedPortName());
            mySettings.setBaudRate(mySettingsPanel.getSelectedBaudRate());
        }
    }

    @Override
    public void reset() {
        if (mySettingsPanel != null) {
            mySettingsPanel.setSelectedPortName(mySettings.getPortName());
            mySettingsPanel.setSelectedBaudRate(mySettings.getBaudRate());
        }
    }

    @Override
    public void disposeUIResources() {
        mySettingsPanel = null;
    }
}
