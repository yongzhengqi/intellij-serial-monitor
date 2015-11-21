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

    private final SerialService mySerialService = ServiceManager.getService(SerialService.class);
    private final NotificationsService myNotificationsService = ServiceManager.getService(NotificationsService.class);

    public ConnectDisconnectAction() {
        super(message("connect.title"), message("connect.tooltip"), SerialMonitorIcons.Connect);
    }

    @Override
    public boolean isSelected(AnActionEvent e) {
        return mySerialService.isConnected();
    }

    @Override
    public void setSelected(AnActionEvent e, boolean doConnect) {
        Project project = e.getProject();
        SerialMonitorSettings mySettings = ServiceManager.getService(project, SerialMonitorSettings.class);

        try {
            if (doConnect) {
                if (mySettings.isValid()) {
                    // try connect only when settings are known to be valid
                    mySerialService.connect(mySettings.getPortName(), mySettings.getBaudRate());
                }
            } else {
                // perform disconnect
                mySerialService.close();
            }
        } catch (SerialMonitorException sme) {
            myNotificationsService.createErrorNotification(sme.getMessage()).notify(project);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        SerialMonitorSettings settings = ServiceManager.getService(e.getProject(), SerialMonitorSettings.class);
        Presentation presentation = e.getPresentation();

        if (mySerialService.isConnected()) {
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
