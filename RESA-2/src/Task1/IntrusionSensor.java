package Task1;

/******************************************************************************************************************
 * File:IntrusionSensor.java
 *
 * Description:
 * This class simulates an intrusion sensor. It polls the message manager for simulation trigger messages. If the sensor is armed the
 * current status of the alarm is posted regularly. The sensor can be armed and disarmed by the security console. If the alarm is activated when the
 * sensor is disarmed, the alarm is turned off.
 ******************************************************************************************************************/

import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

public abstract class IntrusionSensor {

    public static final int DELAY = 2500;

    private int msgId;
    private String name;

    private MessageManagerInterface messageManager = null;
    private MessageWindow messageWindow = null;
    private boolean isArmed = true;
    private boolean isActivated = false;

    public IntrusionSensor(String managerIP, int msgId, String name, float winPosX, float winPosY) {
        this.msgId = msgId;
        this.name = name;

        if (managerIP == null) {

            System.out.println("\n\nAttempting to register on the local machine...");
            try {
                // Here we create an message manager interface object. This assumes
                // that the message manager is on the local machine
                messageManager = new MessageManagerInterface();
            } catch (Exception e) {
                System.out.println("Error instantiating message manager interface: " + e);
            }

        } else {

            System.out.println("\n\nAttempting to register on the machine:: " + managerIP);
            try {
                // Here we create an message manager interface object. This assumes
                // that the message manager is NOT on the local machine
                messageManager = new MessageManagerInterface(managerIP);
            } catch (Exception e) {
                System.out.println("Error instantiating message manager interface: " + e);
            }

        }

        if (messageManager == null) {
            System.out.println("Unable to register with the message manager.\n\n");
            throw new RuntimeException();
        } else {

            messageWindow = new MessageWindow(name, winPosX, winPosY);
            messageWindow.WriteMessage("Registered with the message manager.");
            try {
                messageWindow.WriteMessage("   Participant id: " + messageManager.GetMyId());
                messageWindow.WriteMessage("   Registration Time: " + messageManager.GetRegistrationTime());
            } catch (Exception e) {
                messageWindow.WriteMessage("Error:: " + e);
            }
            messageWindow.WriteMessage("\nInitializing " + name + " Simulation::");

        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: run 
     * Purpose: This methods implements the behavior of the sensor.
     * The sensor continuously reads the messages out of the queue and reacts accordingly.
     *
     * Returns: none
     *
     * Exceptions: None
     *
     ***************************************************************************/
    protected void run() {
        MessageQueue queue = null;
        boolean done = false;

        messageWindow.WriteMessage("Beginning Simulation... ");

        while (!done) {
            try {
                queue = messageManager.GetMessageQueue();
            } catch (Exception e) {
                messageWindow.WriteMessage("Error getting message queue::" + e);
            }

            int qlen = queue.GetSize();
            for (int i = 0; i < qlen; i++) {
                Message msg = queue.GetMessage();

                // If the ID of the message is the negation of the ID this sensor used,
                // then the alarm is triggered by the console.
                // This is for testing purposes.
                if (msg.GetMessageId() == -msgId) {
                    if (isArmed) {
                        isActivated = Boolean.valueOf(msg.GetMessage());
                    }
                }

                // Message ID 10 means that the console arms or disarms the system.
                // The value is given as boolean in the message body.
                // As reaction, the sensor arms/disarms itself.
                if (msg.GetMessageId() == 10) {
                    boolean newValue = Boolean.valueOf(msg.GetMessage());
                    if (isArmed && newValue == false && isActivated) {
                        isActivated = false;
                        postStatus();
                    }
                    if (newValue) {
                        messageWindow.WriteMessage("Sensor armed.");
                    } else {
                        messageWindow.WriteMessage("Sensor disarmed.");
                    }
                    isArmed = newValue;
                }

                // If the messageID == 99 then this is a signal that the simulation
                // is to end. At this point, the loop termination flag is set to
                // true and this process unregisters from the message manager.
                if (msg.GetMessageId() == 99) {
                    done = true;

                    try {
                        messageManager.UnRegister();
                    } catch (Exception e) {
                        messageWindow.WriteMessage("Error unregistering: " + e);
                    }
                    messageWindow.WriteMessage("\n\nSimulation Stopped. \n");
                }

            }

            // Post the current status to the message manager, but only if the sensor is armed.
            if (isArmed) {
                postStatus();
                messageWindow.WriteMessage("Current Status:: " + isActivated);
            }

            // Wait a while before entering the next iteration.
            try {
                Thread.sleep(DELAY);
            } catch (Exception e) {
                messageWindow.WriteMessage("Sleep error:: " + e);
            }

        }

    }

    /***************************************************************************
     * CONCRETE METHOD:: postStatus 
     * Purpose: This method posts the current status of the sensor to the specified message manager.
     *
     * Returns: none
     *
     * Exceptions: None
     *
     ***************************************************************************/
    private void postStatus() {
        try {
            messageManager.SendMessage(new Message(msgId, String.valueOf(isActivated)));
        } catch (Exception e) {
            System.out.println("Error Posting Status:: " + e);
        }
    }
}
