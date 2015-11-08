package com.intellij.plugins.serialmonitor.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.util.ArrayUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.List;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorSettingsPanel {

    private JComboBox myPortNames;
    private JComboBox myBaudRates;
    private JPanel myPanel;

    @SuppressWarnings("unchecked")
    public SerialMonitorSettingsPanel() {
        SerialService mySerialService = ServiceManager.getService(SerialService.class);
        List<String> availablePortNames = mySerialService.getPortNames();
        myPortNames.setModel(new DefaultComboBoxModel(ArrayUtil.toObjectArray(availablePortNames)));
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

    public JComponent getComponent() {
        return myPanel;
    }
}
