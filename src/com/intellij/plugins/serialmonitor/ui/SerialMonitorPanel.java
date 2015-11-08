package com.intellij.plugins.serialmonitor.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.util.Consumer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JComboBox myLineEndings;

    public SerialMonitorPanel() {
        mySend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(myCommand.getText());
                myCommand.setText("");
            }
        });

        mySerialService.addDataListener(new Consumer<String>() {
            @Override
            public void consume(final String s) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        myReceivedText.append(s);
                        if (myAutoScrollEnabled.isSelected()) {
                            myReceivedText.setCaretPosition(myReceivedText.getDocument().getLength());
                        }
                    }
                });
            }
        });
    }

    private void send(String s) {
        switch (myLineEndings.getSelectedIndex()) {
            case 1:
                s += "\n";
                break;
            case 2:
                s += "\r";
                break;
            case 3:
                s += "\r\n";
                break;
        }
        mySerialService.write(s.getBytes());
    }

    public JComponent getComponent() {
        return myPanel;
    }
}
