package com.intellij.plugins.serialmonitor.ui.status;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.intellij.plugins.serialmonitor.ui.status.SerialMonitorStatusWidget.SERIAL_MONITOR_STATUS_WIDGET_ID;

/**
 * @author Dmitry Cherkas
 */
public class SerialMonitorStatusBarWidgetProvider implements StatusBarWidgetFactory {

    @Override
    public @NonNls @NotNull String getId() {
        return SERIAL_MONITOR_STATUS_WIDGET_ID;
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "Serial Monitor Status";
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        return new SerialMonitorStatusWidget(project);
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        Disposer.dispose(widget);
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;
    }
}
