package Task3_2;

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

public abstract class MaintainedDevice {

    public static final int DELAY = 2500;
    public static final int HEARTBEAT_ID = 98;
    public static final String STARTUP_MESSAGE = "startup";
    public static final String HEARTBEAT_MESSAGE = "heartbeat";
    public static final String SHUTDOWN_MESSAGE = "shutdown";

    protected MessageManagerInterface messageManager = null;
    protected MessageWindow messageWindow = null;

    public MaintainedDevice(String managerIP, String name, float winPosX, float winPosY) {
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
            OnStartup();
            messageWindow.WriteMessage("Registered with the message manager.");
            try {
                messageWindow.WriteMessage("   Participant id: " + messageManager.GetMyId());
                messageWindow.WriteMessage("   Registration Time: " + messageManager.GetRegistrationTime());
                messageManager.SendMessage(new Message(HEARTBEAT_ID, STARTUP_MESSAGE + ":" + getIdentifier() + ":" + getDescription()));
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
                
                processMessage(msg);

                // If the messageID == 99 then this is a signal that the simulation
                // is to end. At this point, the loop termination flag is set to
                // true and this process unregisters from the message manager.
                if (msg.GetMessageId() == 99) {
                    done = true;

                    try {
                        messageManager.SendMessage(new Message(HEARTBEAT_ID, SHUTDOWN_MESSAGE + ":" + getIdentifier()));
                        messageManager.UnRegister();
                    } catch (Exception e) {
                        messageWindow.WriteMessage("Error unregistering: " + e);
                    }
                    messageWindow.WriteMessage("\n\nSimulation Stopped. \n");
                    
                    OnShutdown();
                }

            }

            // Give the device the option to do stuff, like posting its status
            doStuff();
            
            // Send a heartbeat
            try {
                messageManager.SendMessage(new Message(HEARTBEAT_ID, HEARTBEAT_MESSAGE + ":" + getIdentifier()));
            } catch (Exception e) {
                messageWindow.WriteMessage("Error when sending heartbeat:: " + e);
            }
            
            // Wait a while before entering the next iteration.
            try {
                Thread.sleep(DELAY);
            } catch (Exception e) {
                messageWindow.WriteMessage("Sleep error:: " + e);
            }

        }

    }
    
    public abstract String getIdentifier();
    
    public abstract String getDescription();
    
    public abstract void OnStartup();

    public abstract void OnShutdown();
    
    public abstract void processMessage(Message msg);
    
    public abstract void doStuff();
    
}
