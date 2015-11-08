package com.intellij.plugins.serialmonitor.ui.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.service.SerialService;
import icons.SerialMonitorIcons;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

/**
 * @author Dmitry_Cherkas
 */
public class ConnectDisconnectAction extends DumbAwareAction {

    private final SerialService mySerialService = ServiceManager.getService(SerialService.class);

    public ConnectDisconnectAction() {
        super(message("connect.title"), message("connect.tooltip"), SerialMonitorIcons.Connect);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        SerialMonitorSettings mySettings = ServiceManager.getService(e.getProject(), SerialMonitorSettings.class);

        if (mySerialService.isConnected()) {
            // perform disconnect
            mySerialService.close();
        } else if (mySettings.isValid()) {
            // try connect only when settings are known to be valid
            mySerialService.connect(mySettings.getPortName(), mySettings.getBaudRate());
        }
    }

    @Override
    public void update(AnActionEvent e) {
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
