# Serial Monitor plugin for IntelliJ IDEA Platform #
This plugin aims to leverage Arduino development with CLion by providing Serial Monitor Tool Window. It allows you to communicate to your Arduino device without leaving the IDE. Functionality is pretty similar to those, available in Arduino IDE through "Serial Monitor Tool".
**Note:** Only COM port (both hardware and virtual) is currently supported. In order to use virtual COM port please make sure to install all necessary drivers.

## Features ##
- Serial Monitor Tool Window with project-based persistent settings
- Status widget
- Duplex Console View (Regular view + HEX)

## Screenshots ##

### Regular View ###
![SerialMonitorRegular.PNG](https://bitbucket.org/repo/GdXK46/images/1110632912-SerialMonitorRegular.PNG)

### HEX View ###
![SerialMonitorHEX.PNG](https://bitbucket.org/repo/GdXK46/images/3293739962-SerialMonitorHEX.PNG)

### Status Widget ###
![SerialMonitorWidget.PNG](https://bitbucket.org/repo/GdXK46/images/1180778625-SerialMonitorWidget.PNG)

## Change Notes ##
- **0.4.1**
    - added plugin logo
    - SerialService converted to projectComponent in order to allow simultaneous usage in multiple open projects
    - fixed NPE when trying to dispose non-initialized SerialMonitorView
- **0.4**
    - added history support for sent commands + keyboard shortcut to send on Ctrl+Enter
    - implemented exception handling via notification popups
    - fixed https://bitbucket.org/dmitry_cherkas/intellij-serial-monitor/issues/1
- **0.3**
    - implemented duplex console to switch between HEX/regular views
    - changed behaviour of status widget
- **0.2**
    - implemented serial monitor status bar
    - added README
- **0.1.1**
    - fixed "EditorImpl hasn't been released" exception on idea shutdown
    - switched to purejavacomm SerialService implementation
- **0.1**
    - Initial version