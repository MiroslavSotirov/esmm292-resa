package Task1;

/******************************************************************************************************************
 * File:SecurityController.java
 *
 * Description:
 * This controller shows the status of the window break, door break and motion detection alarms.
 * This is done by listening to messages with IDs 6, 7 and 8. The body of the messages are a boolean
 * that state whether the alarm is on or off.
 *
 * Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
 * on the local machine.
 ******************************************************************************************************************/

import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

class SecurityController {

    public static final int DELAY = 2500;
    public static final String NAME = "Security Controller";

    private MessageManagerInterface messageManager = null;
    private MessageWindow messageWindow = null;
    private Indicator windowBreakIndicator = null;
    private Indicator doorBreakIndicator = null;
    private Indicator motionDetectionIndicator = null;

    public SecurityController(String managerIP, float winPosX, float winPosY) {
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
            windowBreakIndicator = new Indicator("WindowBreak Alarm", messageWindow.GetX(), messageWindow.GetY() + messageWindow.Height(), 0);
            doorBreakIndicator = new Indicator("DoorBreak Alarm", windowBreakIndicator.GetX() + windowBreakIndicator.Width(), windowBreakIndicator.GetY(), 0);
            motionDetectionIndicator = new Indicator("MotionDetection Alarm", doorBreakIndicator.getX() + doorBreakIndicator.Width(), windowBreakIndicator.GetY(), 0);
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
     * Purpose: This methods implements the behavior of the sensor. The sensor continuously reads the messages out of
     * the queue and reacts accordingly.
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

                // Listen for messages of the intrusion sensors.
                if (msg.GetMessageId() == 6) {
                    updateAlarm("WindowBreak Alarm", windowBreakIndicator, Boolean.valueOf(msg.GetMessage()));
                }
                if (msg.GetMessageId() == 7) {
                    updateAlarm("DoorBreak Alarm", doorBreakIndicator, Boolean.valueOf(msg.GetMessage()));
                }
                if (msg.GetMessageId() == 8) {
                    updateAlarm("MotionDetection Alarm", motionDetectionIndicator, Boolean.valueOf(msg.GetMessage()));
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

                    windowBreakIndicator.dispose();
                    doorBreakIndicator.dispose();
                    motionDetectionIndicator.dispose();
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
     * CONCRETE METHOD:: updateAlarm 
     * 
     * Purpose: This method updates the indicator for the alarm. If the alarm is off, the indicator is black.
     * If the alarm is on, the indicator is red.
     *
     * Returns: none
     *
     * Exceptions: None
     *
     ***************************************************************************/
    private void updateAlarm(String name, Indicator indicator, boolean isActivated) {
        if (isActivated) {
            messageWindow.WriteMessage("Received " + name + " on message");
            indicator.SetLampColor(3);
        } else {
            messageWindow.WriteMessage("Received " + name + " off message");
            indicator.SetLampColor(0);
        }
    }

    public static void main(String args[]) {
        SecurityController controller;
        if (args.length == 0) {
            controller = new SecurityController(null, 0.0f, 0.5f);
        } else {
            controller = new SecurityController(args[0], 0.0f, 0.5f);
        }
        controller.run();
    }

}
