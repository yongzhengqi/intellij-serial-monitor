package com.intellij.plugins.serialmonitor.ui.status;

import javax.swing.*;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorStatusWidgetPanel {
    private JPanel myPanel;
    private JLabel myStatus;

    public JComponent getComponent() {
        return myPanel;
    }

    public void setText(String text, String tooltip) {
        myStatus.setText(text);
        myStatus.setToolTipText(tooltip);
    }
}
