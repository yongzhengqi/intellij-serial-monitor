package com.intellij.plugins.serialmonitor.ui;

import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.util.ArrayUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorPanel {

    private SerialService mySerialService;

    private JComboBox myPortNames;
    private JButton mySend;
    private JTextField myCommand;
    private JButton myConnect;
    private JTextArea myReceivedText;
    private JPanel myPanel;
    private JComboBox myBaudRates;
    private JCheckBox myAutoScrollEnabled;
    private JComboBox myLineBreak;

    public SerialMonitorPanel(final SerialService mySerialService) {
        this.mySerialService = mySerialService;

        myPortNames.setModel(new DefaultComboBoxModel(ArrayUtil.toObjectArray(mySerialService.getPortNames())));
        myBaudRates.setSelectedItem("9600");
        myConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String portName = (String) myPortNames.getSelectedItem();
                int baudRate = Integer.parseInt((String) myBaudRates.getSelectedItem());
                mySerialService.connect(portName, baudRate);
            }
        });
    }

    public JComponent getComponent() {
        return myPanel;
    }
}
