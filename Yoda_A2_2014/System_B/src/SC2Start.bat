%ECHO OFF
%ECHO Starting ECS System
PAUSE
%ECHO SCS Monitoring Console
START "MUSEUM SECURITY CONTROL SYSTEM CONSOLE" /NORMAL java Task2.SecurityConsole %1
%ECHO Starting Fire Alarm Controller Console
START "FIRE ALARM CONTROLLER CONSOLE" /MIN /NORMAL java Task2.FireAlarmController %1
%ECHO Starting Sprinkler Controller Console
START "SPRINKLER CONTROLLER CONSOLE" /MIN /NORMAL java Task2.SprinklerController %1
%ECHO Starting Smoke Sensor Console
START "SMOKE SENSOR CONSOLE" /MIN /NORMAL java Task2.SmokeSensor %1
