package com.intellij.plugins.serialmonitor.ui.status;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorStatusWidget implements CustomStatusBarWidget {
    private final SerialService mySerialService;
    private final SerialMonitorSettings mySettings;

    private final Consumer<Boolean> portStateListener;

    private SerialMonitorStatusWidgetPanel myPanel;

    public SerialMonitorStatusWidget(Project project) {
        mySerialService = ServiceManager.getService(project, SerialService.class);
        mySettings = ServiceManager.getService(project, SerialMonitorSettings.class);
        myPanel = new SerialMonitorStatusWidgetPanel();

        portStateListener = new Consumer<Boolean>() {
            @Override
            public void consume(Boolean isConnected) {
                String text;
                String tooltip = null;
                if (isConnected) {
                    String port = mySettings.getPortName();
                    int baud = mySettings.getBaudRate();

                    text = message("status.connected.text", port, baud);
                    tooltip = message("status.connected.tooltip", port, baud);
                } else {
                    text = message("status.disconnected.text");
                }
                myPanel.setText(text, tooltip);
            }
        };
        mySerialService.addPortStateListener(portStateListener);
    }

    @Override
    public JComponent getComponent() {
        return myPanel.getComponent();
    }

    @NotNull
    @Override
    public String ID() {
        return "SerialMonitorStatusWidget";
    }

    @Nullable
    @Override
    public WidgetPresentation getPresentation(@NotNull PlatformType type) {
        return null;
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
    }

    @Override
    public void dispose() {
        mySerialService.removePortStateListener(portStateListener);
        myPanel = null;
    }
}
