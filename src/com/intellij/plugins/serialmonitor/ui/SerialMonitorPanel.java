package com.intellij.plugins.serialmonitor.ui;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.uiDesigner.core.GridConstraints;
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
    private JPanel myPanel;
    private JCheckBox myAutoScrollEnabled; // TODO: currently unused
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

        SerialMonitorSettings settings = ServiceManager.getService(project, SerialMonitorSettings.class);
        myAutoScrollEnabled.setSelected(settings.isAutoScrollEnabled());
        myLineEndings.setSelectedIndex(settings.getLineEndingsIndex());

        // register listener to update settings, if user preferences were changed
        ActionListener saveSettingsListener = new MyPreferencesChangeListener(settings);
        myAutoScrollEnabled.addActionListener(saveSettingsListener);
        myLineEndings.addActionListener(saveSettingsListener);
    }

    private void initConsoleView(Project project) {
        TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        consoleBuilder.setViewer(true);
        myConsoleView = consoleBuilder.getConsole();

        //Content content = ContentFactory.SERVICE.getInstance().createContent(null, "", false);

        JComponent consoleComponent = myConsoleView.getComponent();
        //content.setActions(createConsoleActionGroup(), ActionPlaces.UNKNOWN, consoleComponent);
        //content.setComponent(consoleComponent);

        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setFill(GridConstraints.FILL_BOTH);
        myConsoleHolder.add(consoleComponent, gridConstraints);
    }

/*
    TODO: figure out, how to add common console actions
    private DefaultActionGroup createConsoleActionGroup() {
        DefaultActionGroup group = new DefaultActionGroup();

        final AnAction[] actions = myConsoleView.createConsoleActions();
        for (AnAction action : actions) {
            group.add(action);
        }
        return group;
    }*/

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

    private class MyPreferencesChangeListener implements ActionListener {
        private final SerialMonitorSettings mySettings;

        public MyPreferencesChangeListener(SerialMonitorSettings settings) {
            this.mySettings = settings;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == myAutoScrollEnabled && myAutoScrollEnabled.isSelected() != mySettings.isAutoScrollEnabled()) {
                // auto scroll option changed
                mySettings.setAutoScrollEnabled(myAutoScrollEnabled.isSelected());
            } else if (source == myLineEndings && myLineEndings.getSelectedIndex() != mySettings.getLineEndingsIndex()) {
                // line endings option changed
                mySettings.setLineEndingIndex(myLineEndings.getSelectedIndex());
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}
