package Task3;

import InstrumentationPackage.Indicator;
import MessagePackage.Message;

/******************************************************************************************************************
 * File:TemperatureController.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*   1.0 March 2009 - Initial rewrite of original assignment 3 (ajl).
*
* Description:
*
* This class simulates a device that controls a heater and chiller. It polls the message manager for message ids = 5
* and reacts to them by turning on or off the heater or chiller. The following command are valid strings for con
* trolling the heater and chiller:
*
*   H1 = heater on
*   H0 = heater off
*   C1 = chillerer on
*   C0 = chiller off
*
* The state (on/off) is graphically displayed on the terminal in the indicator. Command messages are displayed in
* the message window. Once a valid command is recieved a confirmation message is sent with the id of -5 and the command in
* the command string.
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
 ******************************************************************************************************************/

public class TemperatureController extends MaintainedDevice {

    public static final int MSG_ID = 5;

    private Indicator chillerIndicator;
    private Indicator heaterIndicator;

    private boolean isChillerOn = false;
    private boolean isHeaterOn = false;

    public TemperatureController(String managerIP) {
        super(managerIP, "Temperature Controller", 0.0f, 0.3f);
    }

    @Override
    public String getIdentifier() {
        return "temperature_controller";
    }

    @Override
    public String getDescription() {
        return "Temperature Controller: Controls the heater and chiller.";
    }

    @Override
    public void OnStartup() {
        chillerIndicator = new Indicator("Chiller OFF", messageWindow.GetX(), messageWindow.GetY() + messageWindow.Height());
        heaterIndicator = new Indicator("Heater OFF", messageWindow.GetX() + (chillerIndicator.Width() * 2), messageWindow.GetY() + messageWindow.Height());
    }

    @Override
    public void OnShutdown() {
        chillerIndicator.dispose();
        heaterIndicator.dispose();
    }

    @Override
    public void processMessage(Message msg) {
        if (msg.GetMessageId() == MSG_ID) {
            if (msg.GetMessage().equalsIgnoreCase("C1")) {
                isChillerOn = true;
                messageWindow.WriteMessage("Received chiller on message");
                confirmMessage("H1");
            }

            if (msg.GetMessage().equalsIgnoreCase("C0")) {
                isChillerOn = false;
                messageWindow.WriteMessage("Received chiller off message");
                confirmMessage("H0");
            }

            if (msg.GetMessage().equalsIgnoreCase("H1")) {
                isHeaterOn = true;
                messageWindow.WriteMessage("Received heater on message");
                confirmMessage("D1");
            }

            if (msg.GetMessage().equalsIgnoreCase("H0")) {
                isHeaterOn = false;
                messageWindow.WriteMessage("Received heater off message");
                confirmMessage("D0");
            }
        }
    }

    @Override
    public void doStuff() {
        // Update the lamps
        if (isChillerOn) {
            // Set to green, chiller is on
            chillerIndicator.SetLampColorAndMessage("Chiller ON", 1);
        } else {
            // Set to black, chiller is off
            chillerIndicator.SetLampColorAndMessage("Chiller OFF", 0);
        }
        if (isHeaterOn) {
            // Set to green, heater is on
            heaterIndicator.SetLampColorAndMessage("Heater ON", 1);
        } else {
            // Set to black, heater is off
            heaterIndicator.SetLampColorAndMessage("Heater OFF", 0);
        }
    }

    private void confirmMessage(String msg) {
        try {
            messageManager.SendMessage(new Message(-MSG_ID, msg));
        }
        catch (Exception e) {
            System.out.println("Error Confirming Message:: " + e);
        }
    }

    public static void main(String args[]) {
        TemperatureController controller;
        if (args.length == 0) {
            controller = new TemperatureController(null);
        } else {
            controller = new TemperatureController(args[0]);
        }
        controller.run();
    }

}
