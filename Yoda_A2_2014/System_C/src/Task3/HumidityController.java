package Task3;

import InstrumentationPackage.Indicator;
import MessagePackage.Message;

/******************************************************************************************************************
 * File:HumidityController.java Course: 17655 Project: Assignment A3 Copyright: Copyright (c) 2009 Carnegie Mellon University Versions: 1.0
 * March 2009 - Initial rewrite of original assignment 3 (ajl).
 *
 * Description:
 *
 * This class simulates a device that controls a humidifier and dehumidifier. It polls the message manager for message ids = 4 and reacts to
 * them by turning on or off the humidifier/dehumidifier. The following command are valid strings for controlling the humidifier and
 * dehumidifier:
 *
 * H1 = humidifier on H0 = humidifier off D1 = dehumidifier on D0 = dehumidifier off
 *
 * The state (on/off) is graphically displayed on the terminal in the indicator. Command messages are displayed in the message window. Once
 * a valid command is recieved a confirmation message is sent with the id of -5 and the command in the command string.
 *
 * Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is on the local
 * machine.
 ******************************************************************************************************************/

public class HumidityController extends MaintainedDevice {

    public static final int MSG_ID = 4;

    private Indicator humidIndicator;
    private Indicator dehumidIndicator;

    private boolean isHumidifierOn = false;
    private boolean isDehumidifierOn = false;

    public HumidityController(String managerIP) {
        super(managerIP, "Humidity Controller", 0.0f, 0.6f);
    }

    @Override
    public String getIdentifier() {
        return "humidity_controller";
    }

    @Override
    public String getDescription() {
        return "Humidity Controller: Controls the humidifier and dehumidifier.";
    }

    @Override
    public void OnStartup() {
        humidIndicator = new Indicator("Humid OFF", messageWindow.GetX(), messageWindow.GetY() + messageWindow.Height());
        dehumidIndicator = new Indicator("DeHumid OFF", messageWindow.GetX() + (humidIndicator.Width() * 2), messageWindow.GetY() + messageWindow.Height());
    }

    @Override
    public void OnShutdown() {
        humidIndicator.dispose();
        dehumidIndicator.dispose();
    }

    @Override
    public void processMessage(Message msg) {
        if (msg.GetMessageId() == MSG_ID) {
            if (msg.GetMessage().equalsIgnoreCase("H1")) {
                isHumidifierOn = true;
                messageWindow.WriteMessage("Received humidifier on message");
                ConfirmMessage("H1");
            }

            if (msg.GetMessage().equalsIgnoreCase("H0")) {
                isHumidifierOn = false;
                messageWindow.WriteMessage("Received humidifier off message");
                ConfirmMessage("H0");
            }

            if (msg.GetMessage().equalsIgnoreCase("D1")) {
                isDehumidifierOn = true;
                messageWindow.WriteMessage("Received dehumidifier on message");
                ConfirmMessage("D1");
            }

            if (msg.GetMessage().equalsIgnoreCase("D0")) {
                isDehumidifierOn = false;
                messageWindow.WriteMessage("Received dehumidifier off message");
                ConfirmMessage("D0");
            }
        }
    }

    @Override
    public void doStuff() {
        // Update the lamps
        if (isHumidifierOn) {
            // Set to green, humidifier is on
            humidIndicator.SetLampColorAndMessage("HUMID ON", 1);
        } else {
            // Set to black, humidifier is off
            humidIndicator.SetLampColorAndMessage("HUMID OFF", 0);
        }
        if (isDehumidifierOn) {
            // Set to green, dehumidifier is on
            dehumidIndicator.SetLampColorAndMessage("DEHUMID ON", 1);
        } else {
            // Set to black, dehumidifier is off
            dehumidIndicator.SetLampColorAndMessage("DEHUMID OFF", 0);
        }
    }

    private void ConfirmMessage(String msg) {
        try {
            messageManager.SendMessage(new Message(-MSG_ID, msg));
        }
        catch (Exception e) {
            System.out.println("Error Confirming Message:: " + e);
        }
    }

    public static void main(String args[]) {
        HumidityController controller;
        if (args.length == 0) {
            controller = new HumidityController(null);
        } else {
            controller = new HumidityController(args[0]);
        }
        controller.run();
    }

}
