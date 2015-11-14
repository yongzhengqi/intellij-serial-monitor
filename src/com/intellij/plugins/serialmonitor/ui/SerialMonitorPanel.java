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
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.plugins.serialmonitor.ui.console.SerialMonitorConsoleBuilder;
import com.intellij.plugins.serialmonitor.ui.console.SerialMonitorConsoleBuilderFactory;
import com.intellij.util.Consumer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorPanel implements Disposable {

    private final SerialService mySerialService = ServiceManager.getService(SerialService.class);

    private JButton mySend;
    private JTextField myCommand;
    private JPanel myPanel;
    private JComboBox myLineEndings;
    private JPanel myConsoleHolder;
    private ConsoleView myConsoleView;

    public SerialMonitorPanel(final Project project) {
        initConsoleView(project);

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
                        myConsoleView.print(s, ConsoleViewContentType.NORMAL_OUTPUT);
                    }
                });
            }
        });

        mySerialService.addPortStateListener(new Consumer<Boolean>() {
            @Override
            public void consume(Boolean isConnected) {
                myCommand.setEnabled(isConnected);
                myLineEndings.setEnabled(isConnected);
                mySend.setEnabled(isConnected);
            }
        });

        SerialMonitorSettings settings = ServiceManager.getService(project, SerialMonitorSettings.class);
        myLineEndings.setSelectedIndex(settings.getLineEndingsIndex());

        // register listener to update settings, if user preferences were changed
        myLineEndings.addActionListener(new MyPreferencesChangeListener(settings));
    }

    private void initConsoleView(Project project) {
        SerialMonitorConsoleBuilder consoleBuilder = SerialMonitorConsoleBuilderFactory.getInstance().createBuilder(project);
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
        mySerialService.write(s.getBytes());
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
    }

    private class MyPreferencesChangeListener implements ActionListener {
        private final SerialMonitorSettings mySettings;

        public MyPreferencesChangeListener(SerialMonitorSettings settings) {
            this.mySettings = settings;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == myLineEndings && myLineEndings.getSelectedIndex() != mySettings.getLineEndingsIndex()) {
                // line endings option changed
                mySettings.setLineEndingIndex(myLineEndings.getSelectedIndex());
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}
