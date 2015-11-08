package com.intellij.plugins.serialmonitor.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.util.ArrayUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorSettingsPanel {

    private JComboBox myPortNames;
    private JComboBox myBaudRates;
    private JPanel myPanel;
    private JPanel myValidationPanel;
    private JLabel myWarningLabel;
    private JSeparator mySeparator;

    @SuppressWarnings("unchecked")
    public SerialMonitorSettingsPanel() {
        // populate myPortNames
        List<String> availablePortNames = ServiceManager.getService(SerialService.class).getPortNames();
        myPortNames.setModel(new DefaultComboBoxModel(ArrayUtil.toObjectArray(availablePortNames)));

        // configure Settings Validation
        myWarningLabel.setIcon(AllIcons.RunConfigurations.ConfigurationWarning);
        MySettingsPanelChangeListener changeListener = new MySettingsPanelChangeListener();

        myPortNames.addActionListener(changeListener);
        myBaudRates.addActionListener(changeListener);
        myPanel.addComponentListener(changeListener);
    }

    public String getSelectedPortName() {
        Object selectedItem = myPortNames.getSelectedItem();
        return selectedItem == null ? null : String.valueOf(selectedItem);
    }

    public int getSelectedBaudRate() {
        return Integer.parseInt((String) myBaudRates.getSelectedItem());
    }

    public void setSelectedPortName(String portName) {
        if (StringUtils.isNotEmpty(portName)) {
            myPortNames.setSelectedItem(portName);
        }
    }

    public void setSelectedBaudRate(int baudRate) {
        if (baudRate > 0) {
            myBaudRates.setSelectedItem(String.valueOf(baudRate));
        }
    }

    private void validateSettings() {
        if (myPortNames.getModel().getSize() == 0) {
            throw SerialMonitorSettingsValidationException.error("no-ports-error");
        }
        // TODO add more validation (e.g. that selected port does not exist, or baud rate not specified)
    }

    public JComponent getComponent() {
        return myPanel;
    }

    private class MySettingsPanelChangeListener implements ActionListener, ComponentListener {

        @Override
        public void componentShown(ComponentEvent e) {
            updateWarning();
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            updateWarning();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            updateWarning();
        }

        @Override
        public void componentResized(ComponentEvent e) {
            updateWarning();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            updateWarning();
        }

        private void updateWarning() {
            try {
                validateSettings();

                // no errors, hide validation panel
                mySeparator.setVisible(false);
                myWarningLabel.setVisible(false);
                myValidationPanel.setVisible(false);
            } catch (SerialMonitorSettingsValidationException ex) {
                mySeparator.setVisible(true);
                myWarningLabel.setVisible(true);
                myWarningLabel.setText(generateWarningLabelText(ex));
                myValidationPanel.setVisible(true);
            }
        }

        private String generateWarningLabelText(final SerialMonitorSettingsValidationException settingsValidationException) {
            return "<html><body><b>" + settingsValidationException.getTitle() + ": </b>" + settingsValidationException.getMessage() + "</body></html>";
        }
    }
}
