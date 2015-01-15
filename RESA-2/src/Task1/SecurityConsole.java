package Task1;

/******************************************************************************************************************
 * File:SecurityConsole.java
 *
 * Description: This class is the console for the museum security control system. This process consists of two
 * threads. The SecurityMonitor object is a thread that is started that is responsible for the monitoring and control of
 * the museum security systems. The main thread provides a text interface for the user to arm/disarm the security system,
 * trigger the alarms (for testing) as well as shut down the system.
 ******************************************************************************************************************/

import TermioPackage.Termio;

public class SecurityConsole {

    private Termio UserInput = new Termio();
    private SecurityMonitor Monitor = null;

    public SecurityConsole(String managerIP, float winPosX, float winPosY) {
        UserInput = new Termio();
        Monitor = new SecurityMonitor(managerIP);
    }

    private void run() {
        Monitor.start();
        boolean done = false;

        while (!done) {
            System.out.println("\n\n\n\n");
            System.out.println("Security Console: \n");
            if (Monitor.isArmed()) {
                System.out.println("System is armed!");
            } else {
                System.out.println("System is disarmed!");
            }
            System.out.println("\n");

            System.out.println("Select an Option: \n");
            System.out.println("1: Arm the system");
            System.out.println("2: Disarm the system");
            System.out.println("W1: Activate Window Break Alarm");
            System.out.println("W0: Deactivate Window Break Alarm");
            System.out.println("D1: Activate Door Break Alarm");
            System.out.println("D0: Deactivate Door Break Alarm");
            System.out.println("M1: Activate Motion Detection Alarm");
            System.out.println("M0: Deactivate Motion Detection Alarm");
            System.out.println("X: Stop System\n");
            System.out.print("\n>>>> ");
            String Option = UserInput.KeyboardReadString();

            // Arm the system if it is not already armed
            if (Option.equals("1")) {
                if (!Monitor.isArmed()) {
                    Monitor.ArmSystem(true);
                }
            }

            // Disarm the system if it is not already disarmed
            if (Option.equals("2")) {
                if (Monitor.isArmed()) {
                    Monitor.ArmSystem(false);
                }
            }

            // Trigger the window break alarm
            if (Option.equals("W1")) {
                Monitor.simulateWindowBreak(true);
            }

            // End the window break alarm
            if (Option.equals("W0")) {
                Monitor.simulateWindowBreak(false);
            }

            // Trigger the door break alarm
            if (Option.equals("D1")) {
                Monitor.simulateDoorBreak(true);
            }

            // End the door break alarm
            if (Option.equals("D0")) {
                Monitor.simulateDoorBreak(false);
            }

            // Trigger the motion detection alarm
            if (Option.equals("M1")) {
                Monitor.simulateMotionDetection(true);
            }

            // End the motion detection alarm
            if (Option.equals("M0")) {
                Monitor.simulateMotionDetection(false);
            }

            if (Option.equalsIgnoreCase("X")) {
                // Here the user is done, so we set the Done flag and halt
                // the security system. The monitor provides a method
                // to do this. Its important to have processes release their queues
                // with the message manager. If these queues are not released these
                // become dead queues and they collect messages and will eventually
                // cause problems for the message manager.

                done = true;
                System.out.println("\nConsole Stopped... Exit monitor mindow to return to command prompt.");
                Monitor.Halt();
            }
        }
    }

    public static void main(String args[]) {
        SecurityConsole console;
        if (args.length == 0) {
            console = new SecurityConsole(null, 0.0f, 0.5f);
        } else {
            console = new SecurityConsole(args[0], 0.0f, 0.5f);
        }
        console.run();
    }

}
