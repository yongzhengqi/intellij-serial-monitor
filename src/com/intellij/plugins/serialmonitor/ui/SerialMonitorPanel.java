package com.intellij.plugins.serialmonitor.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.plugins.serialmonitor.service.SerialService;

import javax.swing.*;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorPanel {

    private final SerialService mySerialService = ServiceManager.getService(SerialService.class);

    private JButton mySend;
    private JTextField myCommand;
    private JTextArea myReceivedText;
    private JPanel myPanel;
    private JCheckBox myAutoScrollEnabled;
    private JComboBox myLineBreak;

    public JComponent getComponent() {
        return myPanel;
    }
}
