package com.intellij.plugins.serialmonitor.service;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dmitry_Cherkas
 */
@State(
        name = "SerialMonitorSettings",
        storages = {
                @Storage(
                        file = StoragePathMacros.PROJECT_FILE + "/serialmonitor_settings.xml"
                )}
)
public class SerialMonitorSettings implements PersistentStateComponent<Element> {

    private static final String DEFAULT_BAUD_RATE = "9600";

    private static final String SETTINGS_TAG = "SerialMonitorSettings";
    private static final String PORT_NAME = "PortName";
    private static final String BAUD_RATE = "BaudRate";

    private String portName;
    private int baudRate;

    public static SerialMonitorSettings getInstance(Project project) {
        return ServiceManager.getService(project, SerialMonitorSettings.class);
    }

    @Nullable
    @Override
    public Element getState() {
        if (StringUtil.isEmptyOrSpaces(portName) && baudRate == 0) {
            return null;
        }
        final Element element = new Element(SETTINGS_TAG);
        if (getPortName() != null) {
            element.setAttribute(PORT_NAME, getPortName());
        }
        element.setAttribute(BAUD_RATE, String.valueOf(getBaudRate()));
        return element;
    }

    @Override
    public void loadState(Element state) {
        try {
            setPortName(state.getAttributeValue(PORT_NAME));
            setBaudRate(Integer.parseInt(state.getAttributeValue(BAUD_RATE, DEFAULT_BAUD_RATE)));
        } catch (Exception e) {
            // ignore
        }
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }
}
