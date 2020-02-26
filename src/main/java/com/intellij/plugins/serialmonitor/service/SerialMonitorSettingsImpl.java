package com.intellij.plugins.serialmonitor.service;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Dmitry_Cherkas
 */
@State(
        name = "SerialMonitorSettings",
        storages = {
                @Storage("serialmonitor_settings.xml")
        }
)
class SerialMonitorSettingsImpl implements PersistentStateComponent<Element>, SerialMonitorSettings {
    private static final Logger LOG = Logger.getInstance(SerialMonitorSettingsImpl.class.getName());

    private static final String DEFAULT_BAUD_RATE = "9600";

    private static final String SETTINGS_TAG = "SerialMonitorSettings";
    private static final String PORT_NAME = "PortName";
    private static final String BAUD_RATE = "BaudRate";
    private static final String LINE_ENDING_SINDEX = "LineEndingsIndex";

    private final SerialService mySerialService;

    // Memoizing available ports for better performance
    private final Supplier<List<String>> portNamesSupplier;

    private String myPortName;
    private int myBaudRate = Integer.valueOf(DEFAULT_BAUD_RATE);
    private int myLineEndingsIndex;

    public SerialMonitorSettingsImpl(Project project) {
        mySerialService = ServiceManager.getService(project, SerialService.class);
        portNamesSupplier = Suppliers.memoizeWithExpiration(new Supplier<List<String>>() {
            @Override
            public List<String> get() {
                return mySerialService.getPortNames();
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }

    @Nullable
    @Override
    public Element getState() {
        final Element element = new Element(SETTINGS_TAG);
        if (getPortName() != null) {
            element.setAttribute(PORT_NAME, getPortName());
        }
        element.setAttribute(BAUD_RATE, String.valueOf(getBaudRate()));
        element.setAttribute(LINE_ENDING_SINDEX, String.valueOf(getLineEndingsIndex()));
        return element;
    }

    @Override
    public void loadState(Element element) {
        try {
            setPortName(element.getAttributeValue(PORT_NAME));
            setBaudRate(Integer.parseInt(element.getAttributeValue(BAUD_RATE, DEFAULT_BAUD_RATE)));
            setLineEndingIndex(Integer.parseInt(element.getAttributeValue(LINE_ENDING_SINDEX)));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public String getPortName() {
        return myPortName;
    }

    @Override
    public void setPortName(String portName) {
        this.myPortName = portName;
    }

    @Override
    public int getBaudRate() {
        return myBaudRate;
    }

    @Override
    public void setBaudRate(int baudRate) {
        this.myBaudRate = baudRate;
    }

    @Override
    public boolean isValid() {
        List<String> availablePortNames = portNamesSupplier.get();
        return availablePortNames.contains(getPortName());
    }

    @Override
    public int getLineEndingsIndex() {
        return myLineEndingsIndex;
    }

    @Override
    public void setLineEndingIndex(int lineEndingIndex) {
        myLineEndingsIndex = lineEndingIndex;
    }
}
