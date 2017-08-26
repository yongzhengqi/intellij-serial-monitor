package com.intellij.plugins.serialmonitor.ui.console;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorConsoleBuilderFactory {

    public SerialMonitorConsoleBuilder createBuilder(@NotNull Project project) {
        return new SerialMonitorConsoleBuilder(project);
    }

    public static SerialMonitorConsoleBuilderFactory getInstance() {
        return ServiceManager.getService(SerialMonitorConsoleBuilderFactory.class);
    }
}
