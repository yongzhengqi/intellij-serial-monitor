package com.intellij.plugins.serialmonitor.service;

import com.google.common.base.Suppliers;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.intellij.plugins.serialmonitor.service.SerialMonitorSettingsChangeListener.TOPIC;

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
    private static final String SHOW_STATUS_WIDGET = "ShowStatusWidget";
    private static final String LINE_ENDING_SINDEX = "LineEndingsIndex";

    // Memoizing available ports for better performance
    private final Supplier<List<String>> portNamesSupplier;
    private final Project myProject;

    private String myPortName;
    private int myBaudRate = Integer.parseInt(DEFAULT_BAUD_RATE);
    private int myLineEndingsIndex;
    private boolean myShowStatusWidget = true; // defaults to true

    public SerialMonitorSettingsImpl(Project project) {
        myProject = project;
        SerialService mySerialService = ServiceManager.getService(project, SerialService.class);
        portNamesSupplier = Suppliers.memoizeWithExpiration(mySerialService::getPortNames, 1000, TimeUnit.MILLISECONDS);
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
        element.setAttribute(SHOW_STATUS_WIDGET, String.valueOf(isShowStatusWidget()));
        return element;
    }

    @Override
    public void loadState(@NotNull Element element) {
        try {
            setPortName(element.getAttributeValue(PORT_NAME));
            setBaudRate(Integer.parseInt(element.getAttributeValue(BAUD_RATE, DEFAULT_BAUD_RATE)));
            setLineEndingIndex(Integer.parseInt(element.getAttributeValue(LINE_ENDING_SINDEX)));
            setShowStatusWidget(Boolean.parseBoolean(element.getAttributeValue(SHOW_STATUS_WIDGET)));
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

    @Override
    public boolean isShowStatusWidget() {
        return myShowStatusWidget;
    }

    @Override
    public void setShowStatusWidget(boolean showStatusWidget) {
        myShowStatusWidget = showStatusWidget;
        myProject.getMessageBus().syncPublisher(TOPIC).settingsChanged(this);
    }
}
