package com.intellij.plugins.serialmonitor;

import com.google.common.base.Objects;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
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
public class SerialMonitorConfigurable implements SearchableConfigurable, Configurable.NoScroll, Disposable {

    private SerialMonitorSettingsPanel mySettingsPanel;
    private final SerialMonitorSettings mySettings;

    public SerialMonitorConfigurable(Project project) {
        mySettings = SerialMonitorSettings.getInstance(project);
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
                || !Objects.equal(mySettings.getPortName(), mySettingsPanel.getSelectedPortName())
                || mySettings.getBaudRate() != mySettingsPanel.getSelectedBaudRate();
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
        Disposer.dispose(this);
    }

    @Override
    public void dispose() {
        mySettingsPanel = null;
    }
}
