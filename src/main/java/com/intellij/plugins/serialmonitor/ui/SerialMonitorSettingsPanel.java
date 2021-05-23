package com.intellij.plugins.serialmonitor.ui;

import static com.intellij.plugins.serialmonitor.ui.SerialMonitorBundle.message;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.util.SimpleTextCellAppearance;
import com.intellij.plugins.serialmonitor.service.SerialMonitorSettings;
import com.intellij.plugins.serialmonitor.service.SerialService;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.NumberDocument;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ArrayUtil;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorSettingsPanel {

    private JComboBox<Port> myPortNames;
    private JComboBox<String> myBaudRates;
    private JPanel myPanel;
    private JPanel myValidationPanel;
    private JLabel myWarningLabel;
    private JSeparator mySeparator;
    private JCheckBox myShowStatusWidget;

    public SerialMonitorSettingsPanel(Project project, boolean showExtendedSettings) {
        // configure Settings Validation
        myWarningLabel.setIcon(AllIcons.General.BalloonError);
        MySettingsPanelChangeListener changeListener = new MySettingsPanelChangeListener();

        myPortNames.addActionListener(changeListener);
        myBaudRates.addActionListener(changeListener);

        class NumberEditor extends BasicComboBoxEditor {
            @Override
            protected JTextField createEditorComponent() {
                JTextField editor = new IntegerField();
                editor.setDocument(new NumberDocument());
                editor.setBorder(null);
                return editor;
            }
        }

        myBaudRates.setEditor(new NumberEditor());
        myBaudRates.addItemListener(e -> myBaudRates.setEditable(e.getItem().equals("custom")));
        myPanel.addComponentListener(changeListener);

        myPortNames.setRenderer(new ColoredListCellRenderer<Port>() {

            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends Port> list, Port value, int index, boolean selected, boolean hasFocus) {
                if (value == null) {
                    SimpleTextCellAppearance.invalid(message("port-not-selected.text"), null).customize(this);
                } else if (value.isAvailable()) {
                    SimpleTextCellAppearance.regular(value.getName(), null).customize(this);
                } else {
                    SimpleTextCellAppearance.invalid(value.getName(), null).customize(this);
                }
            }
        });

        myPortNames.setModel(new DefaultComboBoxModel<>(getPorts(project).toArray(new Port[0])));
        myPortNames.setSelectedIndex(-1);
        myBaudRates.setSelectedIndex(-1);

        myShowStatusWidget.setVisible(showExtendedSettings);
    }

    public String getSelectedPortName() {
        Object selectedItem = myPortNames.getSelectedItem();
        return selectedItem == null ? null : String.valueOf(selectedItem);
    }

    public int getSelectedBaudRate() {
        Object selectedItem = myBaudRates.getSelectedItem();
        return selectedItem == null ? 0 : Integer.parseInt((String) selectedItem);
    }

    public void setSelectedPortName(String portName) {
        if (StringUtils.isNotEmpty(portName)) {
            myPortNames.setSelectedItem(findPort(portName));
        }
    }

    public void setSelectedBaudRate(int baudRate) {
        if (baudRate > 0) {
            myBaudRates.setSelectedItem(String.valueOf(baudRate));
        }
    }

    private Port findPort(String portName) {
        ComboBoxModel<Port> model = myPortNames.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Port port = model.getElementAt(i);
            if (portName.equals(port.getName())) {
                return port;
            }
        }
        return null;
    }

    private Set<Port> getPorts(Project project) {
        List<String> availablePortNames = ServiceManager.getService(project, SerialService.class).getPortNames();
        String selectedPortName = ServiceManager.getService(project, SerialMonitorSettings.class).getPortName();

        Set<Port> ports = new TreeSet<>();
        for (String portName : availablePortNames) {
            ports.add(new Port(portName, true));
        }

        if (selectedPortName != null && !availablePortNames.contains(selectedPortName)) {
            ports.add(new Port(selectedPortName, false));
        }
        return ports;
    }

    private void validateSettings() {
        if (myPortNames.getModel().getSize() == 0) {
            throw SerialMonitorSettingsValidationException.error("no-ports-error");
        }

        Port selectedPort = (Port) myPortNames.getSelectedItem();
        if (selectedPort == null) {
            throw SerialMonitorSettingsValidationException.error("select-port-error");
        } else if (!selectedPort.isAvailable()) {
            throw SerialMonitorSettingsValidationException.error("port-unavailable-error");
        }
    }

    public JComponent getComponent() {
        return myPanel;
    }

    public boolean isShowStatusWidget() {
        return myShowStatusWidget.isSelected();
    }

    public void setShowStatusWidget(boolean showStatusWidget) {
        myShowStatusWidget.setSelected(showStatusWidget);
    }

    private class MySettingsPanelChangeListener implements ActionListener, ComponentListener {

        @Override
        public void componentShown(ComponentEvent e) {
            updateWarning();
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            updateWarning();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            updateWarning();
        }

        @Override
        public void componentResized(ComponentEvent e) {
            updateWarning();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            updateWarning();
        }

        private void updateWarning() {
            try {
                validateSettings();

                // no errors, hide validation panel
                mySeparator.setVisible(false);
                myWarningLabel.setVisible(false);
                myValidationPanel.setVisible(false);
            } catch (SerialMonitorSettingsValidationException ex) {
                mySeparator.setVisible(true);
                myWarningLabel.setVisible(true);
                myWarningLabel.setText(generateWarningLabelText(ex));
                myValidationPanel.setVisible(true);
            }
        }

        private String generateWarningLabelText(final SerialMonitorSettingsValidationException settingsValidationException) {
            return "<html><body><b>" + settingsValidationException.getTitle() + ": </b>" + settingsValidationException.getMessage()
                    + "</body></html>";
        }
    }

    private static class Port implements Comparable<Port> {

        private String name;
        private boolean available;

        public Port(String name, boolean available) {
            this.name = name;
            this.available = available;
        }

        public String getName() {
            return name;
        }

        public boolean isAvailable() {
            return available;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Port port = (Port) o;

            return name.equals(port.name);

        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int compareTo(@NotNull Port p) {
            return name.compareTo(p.name);
        }
    }
}
