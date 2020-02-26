package com.intellij.plugins.serialmonitor.ui.status;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dmitry Cherkas
 */
public class SerialMonitorStatusBarWidgetProvider implements StatusBarWidgetProvider {
    @Nullable
    @Override
    public StatusBarWidget getWidget(@NotNull Project project) {
        return new SerialMonitorStatusWidget(project);
    }

    @NotNull
    @Override
    public String getAnchor() {
        return StatusBar.Anchors.before(StatusBar.StandardWidgets.ENCODING_PANEL);
    }
}
