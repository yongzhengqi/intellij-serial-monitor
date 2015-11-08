package com.intellij.plugins.serialmonitor.ui.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.service.SerialService;
import icons.SerialMonitorIcons;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

/**
 * @author Dmitry_Cherkas
 */
public class ConnectAction extends DumbAwareAction {

    private final SerialService mySerialService = ServiceManager.getService(SerialService.class);
    private SerialMonitorSettings mySettings = SerialMonitorSettings.getInstance();

    public ConnectAction() {
        super(message("connect.title"), message("connect.tooltip"), SerialMonitorIcons.Connect);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        mySerialService.connect(mySettings.getPortName(), mySettings.getBaudRate());
    }
}
