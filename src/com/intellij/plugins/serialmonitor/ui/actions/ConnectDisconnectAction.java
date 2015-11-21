package com.intellij.plugins.serialmonitor.ui.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.plugins.serialmonitor.SerialMonitorException;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.plugins.serialmonitor.ui.NotificationsService;
import icons.SerialMonitorIcons;
import org.jetbrains.annotations.NotNull;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

/**
 * @author Dmitry_Cherkas
 */
public class ConnectDisconnectAction extends ToggleAction implements DumbAware {

    private final NotificationsService myNotificationsService = ServiceManager.getService(NotificationsService.class);

    public ConnectDisconnectAction() {
        super(message("connect.title"), message("connect.tooltip"), SerialMonitorIcons.Connect);
    }

    @Override
    public boolean isSelected(AnActionEvent e) {
        SerialService serialService = ServiceManager.getService(e.getProject(), SerialService.class);
        return serialService.isConnected();
    }

    @Override
    public void setSelected(AnActionEvent e, boolean doConnect) {
        Project project = e.getProject();
        SerialService serialService = ServiceManager.getService(project, SerialService.class);
        SerialMonitorSettings settings = ServiceManager.getService(project, SerialMonitorSettings.class);

        try {
            if (doConnect) {
                if (settings.isValid()) {
                    // try connect only when settings are known to be valid
                    serialService.connect(settings.getPortName(), settings.getBaudRate());
                }
            } else {
                // perform disconnect
                serialService.close();
            }
        } catch (SerialMonitorException sme) {
            myNotificationsService.createErrorNotification(sme.getMessage()).notify(project);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        Project project = e.getProject();
        SerialService serialService = ServiceManager.getService(project, SerialService.class);
        SerialMonitorSettings settings = ServiceManager.getService(project, SerialMonitorSettings.class);
        Presentation presentation = e.getPresentation();

        if (serialService.isConnected()) {
            // validate disconnect action
            presentation.setIcon(SerialMonitorIcons.Disconnect);
            presentation.setText(message("disconnect.title"));
        } else {
            // validate Connect action
            if (settings.isValid()) {
                presentation.setIcon(SerialMonitorIcons.Connect);
                presentation.setText(message("connect.title"));
            } else {
                presentation.setIcon(SerialMonitorIcons.InvalidSettingsConnect);
                presentation.setText(message("connect-invalid-settings.title"));
            }
        }
    }
}
