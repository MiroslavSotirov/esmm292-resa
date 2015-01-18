%ECHO OFF
%ECHO Starting ECS System
PAUSE
%ECHO SCS Monitoring Console
START "MUSEUM SECURITY CONTROL SYSTEM CONSOLE" /NORMAL java Task1.SecurityConsole %1
%ECHO Starting Security Controller Console
START "TEMPERATURE CONTROLLER CONSOLE" /MIN /NORMAL java Task1.SecurityController %1
%ECHO Starting Door Breaking Sensor Console
START "DOOR BREAKING SENSOR CONSOLE" /MIN /NORMAL java Task1.DoorBreakSensor %1
%ECHO Starting Motion Detection Sensor Console
START "MOTION DETECTION SENSOR CONSOLE" /MIN /NORMAL java Task1.MotionDetectionSensor %1
%ECHO Starting Window Breaking Sensor Console
START "WINDOW BREAKING SENSOR CONSOLE" /MIN /NORMAL java Task1.WindowBreakSensor %1
