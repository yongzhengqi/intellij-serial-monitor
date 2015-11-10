package com.intellij.plugins.serialmonitor.ui.status;

import javax.swing.*;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorStatusWidgetPanel {
    private JPanel myPanel;
    private JLabel myIcon;
    private JLabel myLabel;

    public SerialMonitorStatusWidgetPanel() {
        myPanel.setVisible(false);
    }

    public JComponent getComponent() {
        return myPanel;
    }

    public void setText(String text, String tooltip) {
        myLabel.setText(text);
        myLabel.setToolTipText(tooltip);
    }

    public void setIcon(Icon icon) {
        myIcon.setIcon(icon);
    }

    public void setVisible(boolean isVisible) {
        myPanel.setVisible(isVisible);
    }
}
