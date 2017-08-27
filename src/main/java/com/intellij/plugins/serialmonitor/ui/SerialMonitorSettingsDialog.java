package com.intellij.plugins.serialmonitor.ui;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class SerialMonitorSettingsDialog extends JDialog {

    private final SerialMonitorSettings mySettings;
    private final Project myProject;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private SerialMonitorSettingsPanel mySettingsPanel;

    public SerialMonitorSettingsDialog(Project project) {
        myProject = project;
        mySettings = ServiceManager.getService(project, SerialMonitorSettings.class);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setTitle(message("settings-dialog.title"));
        setResizable(false);
        setLocationRelativeTo(null); // Center dialog on screen

        mySettingsPanel.setSelectedPortName(mySettings.getPortName());
        mySettingsPanel.setSelectedBaudRate(mySettings.getBaudRate());

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
        String selectedPortName = mySettingsPanel.getSelectedPortName();
        int selectedBaudRate = mySettingsPanel.getSelectedBaudRate();

        if (selectedPortName != null) {
            mySettings.setPortName(selectedPortName);
        }
        if (selectedBaudRate > 0) {
            mySettings.setBaudRate(selectedBaudRate);
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        mySettingsPanel = new SerialMonitorSettingsPanel(myProject);
    }
}
