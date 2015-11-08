package com.intellij.plugins.serialmonitor.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugins.serialmonitor.ui.SerialMonitorSettingsDialog;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

/**
 * @author Dmitry_Cherkas
 */
public class EditSettingsAction extends DumbAwareAction {

    public EditSettingsAction() {
        super(message("edit-settings.title"), message("edit-settings.tooltip"), AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        SerialMonitorSettingsDialog dialog = new SerialMonitorSettingsDialog(e.getProject());
        dialog.pack();
        dialog.setVisible(true);
    }
}
