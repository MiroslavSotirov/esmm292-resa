package Task3;

import java.util.Random;

import MessagePackage.Message;

/******************************************************************************************************************
 * File:HumiditySensor.java Course: 17655 Project: Assignment A3 Copyright: Copyright (c) 2009 Carnegie Mellon University Versions: 1.0
 * March 2009 - Initial rewrite of original assignment 3 (ajl).
 *
 * Description:
 *
 * This class simulates a humidity sensor. It polls the message manager for messages corresponding to changes in state of the humidifier or
 * dehumidifier and reacts to them by trending the relative humidity up or down. The current relative humidity is posted to the message
 * manager.
 *
 * Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is on the local
 * machine.
 ******************************************************************************************************************/

public class HumiditySensor extends MaintainedDevice {

    private static final int MSG_ID = 2;

    private boolean isHumidifierOn = false;
    private boolean isDehumidifierOn = false;
    private float relativeHumidity;
    private float driftValue;

    public HumiditySensor(String managerIP) {
        super(managerIP, "Humidity Sensor", 0.5f, 0.6f);
    }

    @Override
    public String getIdentifier() {
        return "humiditiy_sensor";
    }

    @Override
    public String getDescription() {
        return "Humidity Sensor: Reports the relative humidity in %.";
    }

    @Override
    public void OnStartup() {
        relativeHumidity = getRandomNumber() * 100.0f;
        if (coinToss()) {
            driftValue = getRandomNumber() * -1.0f;
        } else {
            driftValue = getRandomNumber();
        }
        messageWindow.WriteMessage("   Initial Humidity Set:: " + relativeHumidity);
        messageWindow.WriteMessage("   Drift Value Set:: " + driftValue);
    }

    @Override
    public void OnShutdown() {
    }

    @Override
    public void processMessage(Message msg) {
        if (msg.GetMessageId() == -HumidityController.MSG_ID) {
            if (msg.GetMessage().equalsIgnoreCase("H1")) {
                isHumidifierOn = true;
            }

            if (msg.GetMessage().equalsIgnoreCase("H0")) {
                isHumidifierOn = false;
            }

            if (msg.GetMessage().equalsIgnoreCase("D1")) {
                isDehumidifierOn = true;
            }

            if (msg.GetMessage().equalsIgnoreCase("D0")) {
                isDehumidifierOn = false;
            }
        }
    }

    @Override
    public void doStuff() {
        if (isHumidifierOn) {
            relativeHumidity += getRandomNumber();
        }
        if (!isHumidifierOn && !isDehumidifierOn) {
            relativeHumidity += driftValue;
        }
        if (isDehumidifierOn) {
            relativeHumidity -= getRandomNumber();
        }
        postHumidity();
        messageWindow.WriteMessage("Current Relative Humidity:: " + relativeHumidity + "%");
    }

    private float getRandomNumber() {
        Random random = new Random();
        float value = -1.0f;
        while (value < 0.1) {
            value = random.nextFloat();
        }
        return value;
    }

    private boolean coinToss() {
        Random random = new Random();
        return random.nextBoolean();
    }

    private void postHumidity() {
        try {
            messageManager.SendMessage(new Message(MSG_ID, String.valueOf(relativeHumidity)));
        } catch (Exception e) {
            System.out.println("Error Posting Relative Humidity:: " + e);
        }
    }

    public static void main(String args[]) {
        HumiditySensor sensor;
        if (args.length == 0) {
            sensor = new HumiditySensor(null);
        } else {
            sensor = new HumiditySensor(args[0]);
        }
        sensor.run();
    }

}
