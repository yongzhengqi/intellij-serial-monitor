package com.intellij.plugins.serialmonitor.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.util.ArrayUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.List;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorSettingsPanel {

    public static final String DEFAULT_BAUD_RATE = "9600";

    private final SerialService mySerialService = ServiceManager.getService(SerialService.class);
    private final SerialMonitorSettings settings = SerialMonitorSettings.getInstance();

    private JComboBox myPortNames;
    private JComboBox myBaudRates;
    private JPanel myPanel;

    @SuppressWarnings("unchecked")
    public SerialMonitorSettingsPanel() {
        List<String> availablePortNames = mySerialService.getPortNames();
        myPortNames.setModel(new DefaultComboBoxModel(ArrayUtil.toObjectArray(availablePortNames)));

        String savedPortName = settings.getPortName();
        if (StringUtils.isNotEmpty(savedPortName) && availablePortNames.contains(savedPortName)) {
            myPortNames.setSelectedItem(savedPortName);
        }

        int savedBaudRate = settings.getBaudRate();
        if (savedBaudRate > 0) {
            myBaudRates.setSelectedItem(String.valueOf(savedBaudRate));
        } else {
            myBaudRates.setSelectedItem(DEFAULT_BAUD_RATE);
        }
    }

    public String getSelectedPortName() {
        return String.valueOf(myPortNames.getSelectedItem());
    }

    public int getSelectedBaudRate() {
        return Integer.parseInt((String) myBaudRates.getSelectedItem());
    }

    public JComponent getComponent() {
        return myPanel;
    }
}
