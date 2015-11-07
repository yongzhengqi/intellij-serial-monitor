package com.intellij.plugins.serialmonitor.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugins.serialmonitor.ui.SerialMonitorSettingsDialog;

/**
 * @author Dmitry_Cherkas
 */
public class EditSettingsAction extends DumbAwareAction {

    public EditSettingsAction() {
        super("Edit Settings", "Open Serial monitor settings dialog", AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        SerialMonitorSettingsDialog dialog = new SerialMonitorSettingsDialog();
        dialog.pack();
        dialog.setVisible(true);
    }
}
