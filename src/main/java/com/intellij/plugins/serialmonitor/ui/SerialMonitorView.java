package com.intellij.plugins.serialmonitor.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.plugins.serialmonitor.ui.actions.ConnectDisconnectAction;
import com.intellij.plugins.serialmonitor.ui.actions.EditSettingsAction;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorView implements ProjectComponent {

    private final Project myProject;
    private SerialMonitorPanel mySerialMonitorPanel;

    protected SerialMonitorView(Project project) {
        myProject = project;
    }

    public static SerialMonitorView getInstance(@NotNull Project project) {
        return project.getComponent(SerialMonitorView.class);
    }

    public void initToolWindow(ToolWindow toolWindow) {

        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false);

        mySerialMonitorPanel = new SerialMonitorPanel(myProject);
        panel.setContent(mySerialMonitorPanel.getComponent());

        ActionToolbar toolbar = createToolbar();
        toolbar.setTargetComponent(panel);
        panel.setToolbar(toolbar.getComponent());

        toolWindow.getContentManager().addContent(content);
    }

    private ActionToolbar createToolbar() {
        DefaultActionGroup group = new DefaultActionGroup();

        group.add(new ConnectDisconnectAction());
        group.add(new EditSettingsAction());

        return ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, false);
    }

    @Override
    public void disposeComponent() {
        if (mySerialMonitorPanel != null) {
            mySerialMonitorPanel.dispose();
        }
    }

    @Override
    public void projectClosed() {
        disposeComponent();
    }
}
