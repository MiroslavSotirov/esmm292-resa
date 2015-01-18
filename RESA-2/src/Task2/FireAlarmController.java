package Task2;

/******************************************************************************************************************
 * File:FireAlarmController.java
 *
 * Description: The fire alarm controller listens to the messages of the smoke sensor.
 * If the smoke sensor changes its state (on or off), the controller turns the alarm
 * on/off accordingly and posts a message.    
 ******************************************************************************************************************/

import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

public class FireAlarmController {

    public static final int DELAY = 2500;

    public static final int MSG_ID = 12;
    public static final String NAME = "File Alarm Controller";

    private MessageManagerInterface messageManager = null;
    private MessageWindow messageWindow = null;
    private boolean alarmOn = false;
    private Indicator alarmIndicator;

    public FireAlarmController(String managerIP, float winPosX, float winPosY) {

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
            alarmIndicator = new Indicator("Fire Alarm", messageWindow.GetX(), messageWindow.GetY() + messageWindow.Height(), 0);
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
     * 
     * Purpose: This methods implements the behavior of the fire alarm. The controller
     * continuously reads the messages out of the queue and reacts accordingly.
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

                // Listen to the messages of the smoke sensor.
                // If the sensor changes its state, turn the alarm on/off and post a message.
                // Also the indicator is updated.
                if (msg.GetMessageId() == 11) {
                    boolean smokeDetected = Boolean.valueOf(msg.GetMessage());
                    if(smokeDetected != alarmOn){
                        alarmOn = !alarmOn;
                        postStatus();
                        if(alarmOn){
                            alarmIndicator.SetLampColor(3);
                        } else {
                            alarmIndicator.SetLampColor(0);
                        }
                    }
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
                    
                    alarmIndicator.dispose();
                }

            }

            // Post the current status to the message manager
            if (alarmOn) {
                messageWindow.WriteMessage("Fire alarm is on.");
            } else {
                messageWindow.WriteMessage("Fire alarm is off.");
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
     * 
     * Purpose: This method posts the current status of the alarm to the message manager.
     *
     * Returns: none
     *
     * Exceptions: None
     *
     ***************************************************************************/
    private void postStatus() {
        try {
            messageManager.SendMessage(new Message(MSG_ID, String.valueOf(alarmOn)));
        } catch (Exception e) {
            System.out.println("Error Posting Status:: " + e);
        }
    }

    public static void main(String args[]) {
        FireAlarmController controller;
        if (args.length == 0) {
            controller = new FireAlarmController(null, 0.5f, 0.3f);
        } else {
            controller = new FireAlarmController(args[0], 0.5f, 0.3f);
        }
        controller.run();
    }

}
