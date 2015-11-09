package com.intellij.plugins.serialmonitor.ui;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.actions.NextOccurenceToolbarAction;
import com.intellij.ide.actions.PreviousOccurenceToolbarAction;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.util.Consumer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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

        SerialMonitorSettings settings = ServiceManager.getService(project, SerialMonitorSettings.class);
        myLineEndings.setSelectedIndex(settings.getLineEndingsIndex());

        // register listener to update settings, if user preferences were changed
        myLineEndings.addActionListener(new MyPreferencesChangeListener(settings));
    }

    private void initConsoleView(Project project) {
        TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        consoleBuilder.setViewer(true);
        myConsoleView = consoleBuilder.getConsole();
        JComponent consoleComponent = myConsoleView.getComponent(); // if I don't call this, I get NPE later on

        DefaultActionGroup toolbarActions = new DefaultActionGroup();
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, toolbarActions, false);
        toolbarActions.addAll(createConsoleActions());
        toolbar.setTargetComponent(consoleComponent);
        myConsoleHolder.add(toolbar.getComponent(), BorderLayout.WEST);

        myConsoleHolder.add(consoleComponent, BorderLayout.CENTER);
    }

    /**
     * Allows filtering out unappropriate actions from toolbar.
     */
    private List<AnAction> createConsoleActions() {
        List<AnAction> filteredActions = new ArrayList<>();
        AnAction[] consoleActions = myConsoleView.createConsoleActions();
        for (AnAction consoleAction : consoleActions) {
            if (consoleAction instanceof PreviousOccurenceToolbarAction || consoleAction instanceof NextOccurenceToolbarAction) {
                // do not need those
                continue;
            }
            filteredActions.add(consoleAction);
        }
        return filteredActions;
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
