package com.intellij.plugins.serialmonitor;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.plugins.serialmonitor.service.JsscSerialService;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.plugins.serialmonitor.ui.SerialMonitorPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import icons.SerialMonitorIcons;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorView {

    private final Project myProject;

    public SerialMonitorView(Project project) {
        myProject = project;
    }

    public static SerialMonitorView getInstance(@NotNull Project project) {
        return project.getComponent(SerialMonitorView.class);
    }

    public void initToolWindow(ToolWindow toolWindow) {
        // TODO: service, singleton
        SerialService serialService = new JsscSerialService();

        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);
        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false);

        SerialMonitorPanel serialMonitorPanel = new SerialMonitorPanel(serialService);
        panel.setContent(serialMonitorPanel.getComponent());

        ActionToolbar toolbar = createToolbar();
        toolbar.setTargetComponent(panel);
        panel.setToolbar(toolbar.getComponent());

        toolWindow.getContentManager().addContent(content);
    }

    private ActionToolbar createToolbar() {
        DefaultActionGroup group = new DefaultActionGroup();

        group.add(new DumbAwareAction("Connect", "Connect to serial port", SerialMonitorIcons.Connect) {
            @Override
            public void actionPerformed(AnActionEvent e) {

            }
        });


        group.add(new DumbAwareAction("Edit Settings", "Open Serial monitor settings dialog", AllIcons.General.Settings) {
            @Override
            public void actionPerformed(AnActionEvent e) {

            }
        });

        return ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, false);
    }

}
