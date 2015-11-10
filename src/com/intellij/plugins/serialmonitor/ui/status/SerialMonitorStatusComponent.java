package com.intellij.plugins.serialmonitor.ui.status;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.service.SerialService;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorStatusComponent extends AbstractProjectComponent {

    private SerialMonitorStatusWidget myStatusWidget;

    protected SerialMonitorStatusComponent(Project project) {
        super(project);

        SerialService serialService = ServiceManager.getService(SerialService.class);
        SerialMonitorSettings settings = ServiceManager.getService(project, SerialMonitorSettings.class);

        myStatusWidget = new SerialMonitorStatusWidget(serialService, settings);
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
