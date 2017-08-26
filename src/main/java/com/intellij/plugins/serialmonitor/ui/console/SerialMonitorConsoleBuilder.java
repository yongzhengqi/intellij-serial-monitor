package com.intellij.plugins.serialmonitor.ui.console;

import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorConsoleBuilder extends TextConsoleBuilderImpl {

    SerialMonitorConsoleBuilder(Project project) {
        super(project);
    }

    @Override
    protected ConsoleView createConsole() {
        return new SerialMonitorDuplexConsoleView(super.createConsole(), new HexConsoleView(getProject(), isViewer()));
    }
}
