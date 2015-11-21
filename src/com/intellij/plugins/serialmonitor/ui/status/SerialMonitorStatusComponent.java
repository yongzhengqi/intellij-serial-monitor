package com.intellij.plugins.serialmonitor.ui.status;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorStatusComponent extends AbstractProjectComponent {

    private SerialMonitorStatusWidget myStatusWidget;

    protected SerialMonitorStatusComponent(Project project) {
        super(project);
        myStatusWidget = new SerialMonitorStatusWidget(project);
    }

    @Override
    public void projectOpened() {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);
        if (statusBar != null) {
            statusBar.addWidget(myStatusWidget, "before Position");
        }
    }

    @Override
    public void projectClosed() {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);
        if ( statusBar != null ) {
            statusBar.removeWidget(myStatusWidget.ID());
        }
    }
}
