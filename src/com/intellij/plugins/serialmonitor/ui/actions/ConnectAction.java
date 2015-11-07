package com.intellij.plugins.serialmonitor.ui.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import icons.SerialMonitorIcons;

/**
 * @author Dmitry_Cherkas
 */
public class ConnectAction extends DumbAwareAction {
    public ConnectAction() {
        super("Connect", "Connect to serial port", SerialMonitorIcons.Connect);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {

    }
}
