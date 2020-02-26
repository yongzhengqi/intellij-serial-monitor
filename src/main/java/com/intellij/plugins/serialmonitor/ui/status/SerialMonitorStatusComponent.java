package com.intellij.plugins.serialmonitor.ui.status;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorStatusComponent implements ProjectComponent {

    private final Project myProject;
    private SerialMonitorStatusWidget myStatusWidget;

    protected SerialMonitorStatusComponent(Project project) {
        myProject = project;
        myStatusWidget = new SerialMonitorStatusWidget(project);
    }

    @Override
    public void projectOpened() {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(myProject);
        if (statusBar != null) {
            statusBar.addWidget(myStatusWidget, "before Position");//TODO
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
