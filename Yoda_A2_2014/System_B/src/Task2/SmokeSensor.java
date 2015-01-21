package Task2;

/******************************************************************************************************************
 * File:SmokeSensor.java
 *
 * Description: The smoke sensor posts on the message manager at a periodic rate
 * if detects smoke or not. For simulation purposes the sensor the smoke can be detection
 * can be triggered with the console by using the negation of the message ID.   
 ******************************************************************************************************************/

import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

public class SmokeSensor {

    public static final int DELAY = 2500;

    public static final int MSG_ID = 11;
    public static final String NAME = "Smoke Sensor";

    private MessageManagerInterface messageManager = null;
    private MessageWindow messageWindow = null;
    private boolean smokeDetected = false;

    public SmokeSensor(String managerIP, float winPosX, float winPosY) {

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

            messageWindow = new MessageWindow(NAME, winPosX, winPosY);
            messageWindow.WriteMessage("Registered with the message manager.");
            try {
                messageWindow.WriteMessage("   Participant id: " + messageManager.GetMyId());
                messageWindow.WriteMessage("   Registration Time: " + messageManager.GetRegistrationTime());
            } catch (Exception e) {
                messageWindow.WriteMessage("Error:: " + e);
            }
            messageWindow.WriteMessage("\nInitializing " + NAME + " Simulation::");

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
                if (msg.GetMessageId() == -MSG_ID) {
                    smokeDetected = Boolean.valueOf(msg.GetMessage());
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

            // Post the current status to the message manager.
            postStatus();
            messageWindow.WriteMessage("Current Status:: " + smokeDetected);

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
     * Purpose: This method posts the current status of the sensor to the message manager.
     *
     * Returns: none
     *
     * Exceptions: None
     *
     ***************************************************************************/
    private void postStatus() {
        try {
            messageManager.SendMessage(new Message(MSG_ID, String.valueOf(smokeDetected)));
        } catch (Exception e) {
            System.out.println("Error Posting Status:: " + e);
        }
    }

    public static void main(String args[]) {
        SmokeSensor sensor;
        if (args.length == 0) {
            sensor = new SmokeSensor(null, 0.5f, 0.0f);
        } else {
            sensor = new SmokeSensor(args[0], 0.5f, 0.0f);
        }
        sensor.run();
    }
    
}
