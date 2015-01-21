package Task2;

/******************************************************************************************************************
 * File:SprinklerController.java
 *
 * Description: The sprinkler controller listens to the fire alarm controller.
 * If the fire alarm turns on, the sprinkler waits 10 seconds for the confirmation
 * of the monitor. If the monitor does not react within that time, the sprinkler
 * turns on automatically. The monitor then has the option to turn the sprinkler off.   
 ******************************************************************************************************************/

import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

public class SprinklerController {

    public static final int DELAY = 2500;

    public static final int MSG_ID = 13;
    public static final String NAME = "Sprinkler Controller";

    private MessageManagerInterface messageManager = null;
    private MessageWindow messageWindow = null;
    private boolean sprinklerOn = false;
    private boolean sprinklerTriggered = false;
    private long alarmStart = 0;
    private static final long AUTOMATIC_START = 10000; // in ms
    private Indicator sprinklerIndicator;

    public SprinklerController(String managerIP, float winPosX, float winPosY) {

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
            sprinklerIndicator = new Indicator("Sprinkler", messageWindow.GetX(), messageWindow.GetY() + messageWindow.Height(), 0);
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
     * Purpose: This methods implements the behavior of the sprinkler controller. 
     * The sprinkler continuously reads the messages out of the queue and reacts accordingly.
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

                // Listen to the messages of the fire alarm.
                if (msg.GetMessageId() == 12) {
                    boolean alarmOn = Boolean.valueOf(msg.GetMessage());
                    if(alarmOn){
                        sprinklerTriggered = true;
                        alarmStart = System.currentTimeMillis();
                    }
                }

                // Listen to the messages of the monitor.
                if (msg.GetMessageId() == -MSG_ID) {
                    boolean command = Boolean.valueOf(msg.GetMessage());
                    if(command){
                        if(!sprinklerOn){
                            sprinklerOn = true;
                            sprinklerTriggered = false;
                            postStatus();
                            sprinklerIndicator.SetLampColor(2);
                        }
                    } else {
                        if(sprinklerOn){
                            sprinklerOn = false;
                            postStatus();
                            sprinklerIndicator.SetLampColor(0);
                        } else {
                            sprinklerTriggered = false;
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
                    
                    sprinklerIndicator.dispose();
                }

            }

            if(!sprinklerOn && sprinklerTriggered && System.currentTimeMillis() > alarmStart + AUTOMATIC_START){
                sprinklerOn = true;
                sprinklerTriggered = false;
                postStatus();
                sprinklerIndicator.SetLampColor(2);
            }

            // Post the current status to the message manager
            if (sprinklerOn) {
                messageWindow.WriteMessage("Sprinkler is on.");
            } else {
                if(sprinklerTriggered){
                    double secondsToStart = (AUTOMATIC_START + alarmStart - System.currentTimeMillis()) / 1000;
                    messageWindow.WriteMessage("Sprinkler starts automatically in " + secondsToStart + " seconds.");
                } else {
                messageWindow.WriteMessage("Sprinkler is off.");
                }
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
     * Purpose: This method posts the current status of the sprinkler to the message manager.
     *
     * Returns: none
     *
     * Exceptions: None
     *
     ***************************************************************************/
    private void postStatus() {
        try {
            messageManager.SendMessage(new Message(MSG_ID, String.valueOf(sprinklerOn)));
        } catch (Exception e) {
            System.out.println("Error Posting Status:: " + e);
        }
    }

    public static void main(String args[]) {
        SprinklerController controller;
        if (args.length == 0) {
            controller = new SprinklerController(null, 0.5f, 0.6f);
        } else {
            controller = new SprinklerController(args[0], 0.5f, 0.6f);
        }
        controller.run();
    }

}
