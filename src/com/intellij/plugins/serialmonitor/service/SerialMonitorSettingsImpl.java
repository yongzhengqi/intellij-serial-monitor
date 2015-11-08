package com.intellij.plugins.serialmonitor.service;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.intellij.openapi.components.*;
import com.intellij.openapi.util.text.StringUtil;
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
                @Storage(
                        file = StoragePathMacros.PROJECT_FILE + "/serialmonitor_settings.xml"
                )}
)
class SerialMonitorSettingsImpl implements PersistentStateComponent<Element>, SerialMonitorSettings {

    private static final String DEFAULT_BAUD_RATE = "9600";

    private static final String SETTINGS_TAG = "SerialMonitorSettings";
    private static final String PORT_NAME = "PortName";
    private static final String BAUD_RATE = "BaudRate";

    private SerialService serialService = ServiceManager.getService(SerialService.class);

    // Memoizing available ports for better performance
    private Supplier<List<String>> portNamesSupplier = Suppliers.memoizeWithExpiration(new Supplier<List<String>>() {
        @Override
        public List<String> get() {
            return serialService.getPortNames();
        }
    }, 1000, TimeUnit.MILLISECONDS);

    private String portName;
    private int baudRate;

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

    @Override
    public String getPortName() {
        return portName;
    }

    @Override
    public void setPortName(String portName) {
        this.portName = portName;
    }

    @Override
    public int getBaudRate() {
        return baudRate;
    }

    @Override
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    @Override
    public boolean isValid() {
        List<String> availablePortNames = portNamesSupplier.get();
        return availablePortNames.contains(getPortName());


    }
}
