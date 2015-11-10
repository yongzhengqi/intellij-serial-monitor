# Serial Monitor plugin for IntelliJ IDEA Platform #
Serial port monitor plugin for IntelliJ IDEA platform aims to provide functionality, available in Arduino IDE through Serial Monitor Tool. Only COM port (both hardware and virtual) is currently supported. In order to use virtual COM port please make sure to install all necessary drivers.

## Screenshot ##
![serialMonitorToolbar.PNG](https://bitbucket.org/repo/GdXK46/images/2572690769-serialMonitorToolbar.PNG)

## Change Notes ##
- **0.2**
    - implemented serial monitor status bar
    - added README
- **0.1.1**
    - fixed "EditorImpl hasn't been released" exception on idea shutdown
    - switched to purejavacomm SerialService implementation
- **0.1**
    - Initial version