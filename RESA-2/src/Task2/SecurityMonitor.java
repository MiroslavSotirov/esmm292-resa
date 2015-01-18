package Task2;

/******************************************************************************************************************
 * File:SecurityMonitor.java
 *
 * Description:
 * This class monitors the security control systems.
 ******************************************************************************************************************/

import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

class SecurityMonitor extends Thread {

    public static final int DELAY = 2500;
    public static final String NAME = "Security Monitor";

    private MessageManagerInterface messageManager = null;
    private MessageWindow messageWindow = null;

    private boolean sprinklerTriggered = false;
    private boolean sprinklerOn = false;

    public SecurityMonitor() {
        this(null);
    }

    public SecurityMonitor(String managerIP) {
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

            messageWindow = new MessageWindow(NAME, 0.0f, 0.0f);
            messageWindow.WriteMessage("Registered with the message manager.");
            try {
                messageWindow.WriteMessage("   Participant id: " + messageManager.GetMyId());
                messageWindow.WriteMessage("   Registration Time: " + messageManager.GetRegistrationTime());
            } catch (Exception e) {
                messageWindow.WriteMessage("Error:: " + e);
            }

        }
    }

    /***************************************************************************
     * CONCRETE METHOD:: run
     * 
     * Purpose: This methods implements the behavior of the sensor. The sensor continuously reads the messages out of the queue and reacts
     * accordingly.
     *
     * Returns: none
     *
     * Exceptions: None
     *
     ***************************************************************************/
    public void run() {
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
                    boolean fireAlarmOn = Boolean.valueOf(msg.GetMessage());
                    if(fireAlarmOn && !sprinklerOn){
                        sprinklerTriggered = true;
                    }
                }

                // Listen to the messages of the sprinkler.
                if (msg.GetMessageId() == 13) {
                    sprinklerOn = Boolean.valueOf(msg.GetMessage());
                    sprinklerTriggered = false;
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

            // Wait a while before entering the next iteration.
            try {
                Thread.sleep(DELAY);
            } catch (Exception e) {
                messageWindow.WriteMessage("Sleep error:: " + e);
            }

        }

    }

    public void simulateSmoke(boolean activate) {
        if (activate) {
            messageWindow.WriteMessage("***Trigger Smoke Simulation: On***");
        } else {
            messageWindow.WriteMessage("***Trigger Smoke Simulation: Off***");
        }
        try {
            messageManager.SendMessage(new Message(-11, String.valueOf(activate)));
        } catch (Exception e) {
            System.out.println("Error sending message:: " + e);
        }
    }

    public void startSprinkler(boolean activate) {
        if (activate) {
            messageWindow.WriteMessage("***Activate Sprinkler On***");
        } else {
            messageWindow.WriteMessage("***Activate Sprinkler Off***");
        }
        try {
            messageManager.SendMessage(new Message(-13, String.valueOf(activate)));
            sprinklerOn = activate;
            sprinklerTriggered = false;
        } catch (Exception e) {
            System.out.println("Error sending message:: " + e);
        }
    }
    
    public boolean isSprinklerTriggered(){
        return sprinklerTriggered;
    }
    
    public boolean isSprinklerOn(){
        return sprinklerOn;
    }

    /***************************************************************************
     * CONCRETE METHOD:: Halt
     * 
     * Purpose: This method posts an message that stops the security system.
     *
     * Exceptions: Posting to message manager exception
     *
     ***************************************************************************/
    public void Halt() {
        messageWindow.WriteMessage("***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***");
        try {
            messageManager.SendMessage(new Message((int) 99, "XXX"));
        } catch (Exception e) {
            System.out.println("Error sending halt message:: " + e);
        }
    }

}
