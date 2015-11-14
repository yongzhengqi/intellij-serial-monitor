package com.intellij.plugins.serialmonitor.ui.status;

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

    private SerialMonitorStatusWidgetPanel myPanel;

    private final SerialMonitorSettings mySettings;

    public SerialMonitorStatusWidget(SerialService serialService, SerialMonitorSettings settings) {
        myPanel = new SerialMonitorStatusWidgetPanel();
        mySettings = settings;

        serialService.addPortStateListener(new Consumer<Boolean>() {
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
        });
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
        myPanel = null;
    }
}
