package Task1;

/******************************************************************************************************************
 * File:SecurityMonitor.java
 *
 * Description:
 * This class monitors the security control systems that control museum intrusion detection. The system
 * has a window break sensor, a door break sensor, a motion detection sensor and a security controller that
 * indicates the alarms.
 ******************************************************************************************************************/

import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

class SecurityMonitor extends Thread {

    public static final int DELAY = 2500;
    public static final String NAME = "Security Monitor";

    private MessageManagerInterface messageManager = null;
    private MessageWindow messageWindow = null;

    private boolean systemArmed = true;
    private Indicator armedIndicator;
    private boolean windowBreakAlarm = false;
    private boolean doorBreakAlarm = false;
    private boolean motionDetectionAlarm = false;

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
            armedIndicator = new Indicator("Armed Indicator", messageWindow.GetX(), messageWindow.GetX() + messageWindow.Height(), 1);
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

                // Listen to the messages of the intrusion sensors and print a message if the status changed.
                if (msg.GetMessageId() == 6) {
                    windowBreakAlarm = recieveAlarmStatus("WindowBreak Alarm", windowBreakAlarm, Boolean.valueOf(msg.GetMessage()));
                }
                if (msg.GetMessageId() == 7) {
                    doorBreakAlarm = recieveAlarmStatus("DoorBreak Alarm", doorBreakAlarm, Boolean.valueOf(msg.GetMessage()));
                }
                if (msg.GetMessageId() == 8) {
                    motionDetectionAlarm = recieveAlarmStatus("MotionDetection Alarm", motionDetectionAlarm, Boolean.valueOf(msg.GetMessage()));
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

                    armedIndicator.dispose();
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

    private boolean recieveAlarmStatus(String alarmName, boolean oldValue, boolean newValue) {
        try {
            if (newValue != oldValue) {
                if (newValue) {
                    messageWindow.WriteMessage(alarmName + " went on.");
                } else {
                    messageWindow.WriteMessage(alarmName + " went off.");
                }
            }
            return newValue;
        } catch (Exception e) {
            messageWindow.WriteMessage("Error reading WindowBreak: " + e);
            return oldValue;
        }
    }

    public void ArmSystem(boolean arm) {
        systemArmed = arm;
        messageWindow.WriteMessage("***System Arm Status set to::" + systemArmed + "***");
        if (arm) {
            armedIndicator.SetLampColorAndMessage("Armed", 1);
        } else {
            armedIndicator.SetLampColorAndMessage("Disarmed", 0);
        }

        try {
            messageManager.SendMessage(new Message(10, String.valueOf(arm)));
        } catch (Exception e) {
            System.out.println("Error sending message:: " + e);
        }

    }

    public void simulateWindowBreak(boolean activate) {
        simulateAlarm("WindowBreak", -6, activate);
    }

    public void simulateDoorBreak(boolean activate) {
        simulateAlarm("DorBreak", -7, activate);
    }

    public void simulateMotionDetection(boolean activate) {
        simulateAlarm("MotionDetection", -8, activate);
    }

    public void simulateAlarm(String alarmName, int msgID, boolean activate) {
        if (activate) {
            messageWindow.WriteMessage("***Trigger MotionDetection Simulation: On***");
        } else {
            messageWindow.WriteMessage("***Trigger MotionDetection Simulation: Off***");
        }
        try {
            messageManager.SendMessage(new Message(msgID, String.valueOf(activate)));
        } catch (Exception e) {
            System.out.println("Error sending message:: " + e);
        }
    }
    
    public boolean isArmed(){
        return systemArmed;
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
