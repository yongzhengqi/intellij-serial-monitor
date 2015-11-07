package com.intellij.plugins.serialmonitor.ui.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import icons.SerialMonitorIcons;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

/**
 * @author Dmitry_Cherkas
 */
public class ConnectAction extends DumbAwareAction {
    public ConnectAction() {
        super(message("connect.title"), message("connect.tooltip"), SerialMonitorIcons.Connect);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {

    }
}
