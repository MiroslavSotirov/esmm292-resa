package Task3;

import java.util.Random;

import MessagePackage.Message;

/******************************************************************************************************************
 * File:TemperatureSensor.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*   1.0 March 2009 - Initial rewrite of original assignment 3 (ajl).
*
* Description:
*
* This class simulates a temperature sensor. It polls the message manager for messages corresponding to changes in state
* of the heater or chiller and reacts to them by trending the ambient temperature up or down. The current ambient
* room temperature is posted to the message manager.
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
 ******************************************************************************************************************/

public class TemperatureSensor extends MaintainedDevice {

    private static final int MSG_ID = 1;

    private boolean isChillerOn = false;
    private boolean isHeaterOn = false;
    private float currentTemperature;
    private float driftValue;

    public TemperatureSensor(String managerIP) {
        super(managerIP, "Temperature Sensor", 0.5f, 0.3f);
    }

    @Override
    public String getIdentifier() {
        return "temperature_sensor";
    }

    @Override
    public String getDescription() {
        return "Temperature Sensor: Reports the temperature in F.";
    }

    @Override
    public void OnStartup() {
        currentTemperature = getRandomNumber() * 50.0f;
        if (coinToss()) {
            driftValue = getRandomNumber() * -1.0f;
        } else {
            driftValue = getRandomNumber();
        }
        messageWindow.WriteMessage("   Initial Temperature Set:: " + currentTemperature);
        messageWindow.WriteMessage("   Drift Value Set:: " + driftValue);
    }

    @Override
    public void OnShutdown() {
    }

    @Override
    public void processMessage(Message msg) {
        if (msg.GetMessageId() == -TemperatureController.MSG_ID) {
            if (msg.GetMessage().equalsIgnoreCase("C1")) {
                isChillerOn = true;
            }

            if (msg.GetMessage().equalsIgnoreCase("C0")) {
                isChillerOn = false;
            }

            if (msg.GetMessage().equalsIgnoreCase("H1")) {
                isHeaterOn = true;
            }

            if (msg.GetMessage().equalsIgnoreCase("H0")) {
                isHeaterOn = false;
            }
        }
    }

    @Override
    public void doStuff() {
        if (isHeaterOn) {
            currentTemperature += getRandomNumber();
        }
        if (!isHeaterOn && !isChillerOn) {
            currentTemperature += driftValue;
        }
        if (isChillerOn) {
            currentTemperature -= getRandomNumber();
        }
        postTemperature();
        messageWindow.WriteMessage("Current Temperature:: " + currentTemperature + " F");
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

    private void postTemperature() {
        try {
            messageManager.SendMessage(new Message(MSG_ID, String.valueOf(currentTemperature)));
        } catch (Exception e) {
            System.out.println("Error Posting Temperature:: " + e);
        }
    }

    public static void main(String args[]) {
        TemperatureSensor sensor;
        if (args.length == 0) {
            sensor = new TemperatureSensor(null);
        } else {
            sensor = new TemperatureSensor(args[0]);
        }
        sensor.run();
    }

}
