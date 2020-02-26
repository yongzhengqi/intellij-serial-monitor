package com.intellij.plugins.serialmonitor;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.plugins.serialmonitor.ui.SerialMonitorPanel;
import com.intellij.plugins.serialmonitor.ui.actions.ConnectDisconnectAction;
import com.intellij.plugins.serialmonitor.ui.actions.EditSettingsAction;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorToolWindowFactory implements ToolWindowFactory, DumbAware, Disposable {

    private SerialMonitorPanel mySerialMonitorPanel;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false);

        mySerialMonitorPanel = new SerialMonitorPanel(project);
        panel.setContent(mySerialMonitorPanel.getComponent());

        ActionToolbar toolbar = createToolbar();
        toolbar.setTargetComponent(panel);
        panel.setToolbar(toolbar.getComponent());

        toolWindow.getContentManager().addContent(content);

        Disposer.register(project, this);
    }

    private ActionToolbar createToolbar() {
        DefaultActionGroup group = new DefaultActionGroup();

        group.add(new ConnectDisconnectAction());
        group.add(new EditSettingsAction());

        return ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, false);
    }

    @Override
    public void dispose() {
        if (mySerialMonitorPanel != null) {
            mySerialMonitorPanel.dispose();
        }
    }
}
