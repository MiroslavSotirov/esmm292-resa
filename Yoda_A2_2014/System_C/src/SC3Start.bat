%ECHO OFF
%ECHO Starting ECS System
PAUSE
%ECHO ECS Maintenance Console
START "MUSEUM MAINTENANCE CONSOLE" /NORMAL java Task3.MaintenanceConsole %1
%ECHO Starting Temperature Controller Console
START "TEMPERATURE CONTROLLER CONSOLE" /MIN /NORMAL java Task3.TemperatureController %1
%ECHO Starting Humidity Sensor Console
START "HUMIDITY CONTROLLER CONSOLE" /MIN /NORMAL java Task3.HumidityController %1
START "TEMPERATURE SENSOR CONSOLE" /MIN /NORMAL java Task3.TemperatureSensor %1
%ECHO Starting Humidity Sensor Console
START "HUMIDITY SENSOR CONSOLE" /MIN /NORMAL java Task3.HumiditySensor %1
