package com.intellij.plugins.serialmonitor.ui;

import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;

import javax.swing.*;
import java.awt.event.*;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

public class SerialMonitorSettingsDialog extends JDialog {

    private final SerialMonitorSettings settings = SerialMonitorSettings.getInstance();

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private SerialMonitorSettingsPanel mySettingsPanel;

    public SerialMonitorSettingsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setTitle(message("settings-dialog.title"));
        setResizable(false);
        setLocationRelativeTo(null); // Center dialog on screen

        initListeners();
    }

    private void initListeners() {
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // TODO: add for validation, error tooltips (no ports available, etc)
        settings.setPortName(mySettingsPanel.getSelectedPortName());
        settings.setBaudRate(mySettingsPanel.getSelectedBaudRate());
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
