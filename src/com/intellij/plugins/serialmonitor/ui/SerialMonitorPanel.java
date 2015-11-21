package com.intellij.plugins.serialmonitor.ui;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.plugins.serialmonitor.SerialMonitorException;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.plugins.serialmonitor.ui.console.SerialMonitorConsoleBuilder;
import com.intellij.plugins.serialmonitor.ui.console.SerialMonitorConsoleBuilderFactory;
import com.intellij.util.Consumer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorPanel implements Disposable {

    private final NotificationsService myNotificationsService = ServiceManager.getService(NotificationsService.class);

    private final SerialService mySerialService;
    private final SerialMonitorSettings mySettings;
    private final Project myProject;

    private final Consumer<Boolean> portStateListener;
    private final Consumer<String> dataListener;

    private JButton mySend;
    private CommandsComboBox myCommand;
    private JPanel myPanel;
    private JComboBox myLineEndings;
    private JPanel myConsoleHolder;
    private ConsoleView myConsoleView;

    public SerialMonitorPanel(final Project project) {
        myProject = project;
        mySerialService = ServiceManager.getService(myProject, SerialService.class);
        mySettings = ServiceManager.getService(myProject, SerialMonitorSettings.class);

        initConsoleView();

        myCommand.setProject(myProject);
        myCommand.setHistorySize(10);
        myCommand.addKeyboardListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // send on CTRL + ENTER
                if (e.isControlDown() && e.getKeyChar() == KeyEvent.VK_ENTER) {
                    myCommand.hidePopup();
                    mySend.doClick();
                }
            }
        });

        mySend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(myCommand.getText());
                myCommand.addCurrentTextToHistory();
                myCommand.setText("");
            }
        });

        dataListener = new Consumer<String>() {
            @Override
            public void consume(final String s) {
                myConsoleView.print(s, ConsoleViewContentType.NORMAL_OUTPUT);
            }
        };
        mySerialService.addDataListener(dataListener);

        portStateListener = new Consumer<Boolean>() {
            @Override
            public void consume(Boolean isConnected) {
                myCommand.setEnabled(isConnected);
                myLineEndings.setEnabled(isConnected);
                mySend.setEnabled(isConnected);
            }
        };
        mySerialService.addPortStateListener(portStateListener);

        myLineEndings.setSelectedIndex(mySettings.getLineEndingsIndex());
        // register listener to update settings, if user preferences were changed
        myLineEndings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (myLineEndings.getSelectedIndex() != mySettings.getLineEndingsIndex()) {
                    mySettings.setLineEndingIndex(myLineEndings.getSelectedIndex());
                }
            }
        });
    }

    private void initConsoleView() {
        SerialMonitorConsoleBuilder consoleBuilder = SerialMonitorConsoleBuilderFactory.getInstance().createBuilder(myProject);
        consoleBuilder.setViewer(true);
        myConsoleView = consoleBuilder.getConsole();
        JComponent consoleComponent = myConsoleView.getComponent(); // if I don't call this, I get NPE later on

        DefaultActionGroup toolbarActions = new DefaultActionGroup();
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, toolbarActions, false);
        toolbarActions.addAll(myConsoleView.createConsoleActions());
        toolbar.setTargetComponent(consoleComponent);
        myConsoleHolder.add(toolbar.getComponent(), BorderLayout.WEST);

        myConsoleHolder.add(consoleComponent, BorderLayout.CENTER);
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
        try {
            mySerialService.write(s.getBytes());
        } catch (SerialMonitorException sme) {
            myNotificationsService.createErrorNotification(sme.getMessage()).notify(myProject);
        }
    }

    public JComponent getComponent() {
        return myPanel;
    }

    @Override
    public void dispose() {
        if (myConsoleView != null) {
            Disposer.dispose(myConsoleView);
            myConsoleView = null;
        }
        mySerialService.removePortStateListener(portStateListener);
        mySerialService.removeDataListener(dataListener);
    }
}
