package Task3;

/******************************************************************************************************************
 * File:MaintenanceMonitor.java
 *
 * Description:
 * This class monitors the museum systems and keeps track of the devices in the system.
 ******************************************************************************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

class MaintenanceMonitor extends Thread {

    public static final int DELAY = 2500;
    public static final String NAME = "Maintenance Monitor";

    private MessageManagerInterface messageManager = null;
    private MessageWindow messageWindow = null;

    private Map<String, Long> lastHeartbeats;
    private Map<String, String> descriptions;
    private boolean isStopped = false;

    public MaintenanceMonitor() {
        this(null);
    }

    public MaintenanceMonitor(String managerIP) {
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

            lastHeartbeats = new HashMap<>();
            descriptions = new HashMap<>();
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

                // Listen to heartbeat messages
                if(msg.GetMessageId() == MaintainedDevice.HEARTBEAT_ID){
                    String[] split = msg.GetMessage().split(":");
                    if(split[0].equals(MaintainedDevice.STARTUP_MESSAGE)){
                        lastHeartbeats.put(split[1], System.currentTimeMillis());
                        descriptions.put(split[1], split[2]);
                    }
                    if(split[0].equals(MaintainedDevice.HEARTBEAT_MESSAGE)){
                        lastHeartbeats.put(split[1], System.currentTimeMillis());
                    }
                    if(split[0].equals(MaintainedDevice.SHUTDOWN_MESSAGE)){
                        lastHeartbeats.remove(split[1]);
                        descriptions.remove(split[1]);
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

                    isStopped = true;
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

    public List<String> getStatuses() {
        List<String> statuses = new ArrayList<>();
        
        long currentTime = System.currentTimeMillis();
        for(Entry<String, Long> entry : lastHeartbeats.entrySet()){
            boolean active = currentTime - entry.getValue() < MaintainedDevice.DELAY;
            if(active){
                statuses.add("Active   - " + descriptions.get(entry.getKey()));
            } else {
                statuses.add("Inactive - " + descriptions.get(entry.getKey()));
            }
        }
        
        return statuses;
    }
    
    public boolean isStopped(){
        return isStopped;
    }

}
