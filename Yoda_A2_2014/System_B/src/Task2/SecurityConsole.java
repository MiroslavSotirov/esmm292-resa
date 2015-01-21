package Task2;

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

            if(Monitor.isSprinklerTriggered()){
                showSprinklerConfirmation();
            } else {
                done = showMenu();
            }
        }
    }
    
    private boolean showMenu(){
        boolean isSprinklerOn = Monitor.isSprinklerOn();
        System.out.println("Select an Option: \n");
        System.out.println("1: Activate smoke");
        System.out.println("2: Deactivate smoke");
        if(isSprinklerOn){
            System.out.println("3: Deactivate sprinkler");
        }
        System.out.println("X: Stop System\n");
        System.out.print("\n>>>> ");
        
        while(!Monitor.isSprinklerTriggered()){
            if(UserInput.KeyboardLineReady()){
                String option = UserInput.KeyboardReadString();

                if (option.equals("1")) {
                    // Turn smoke simulation on
                    Monitor.simulateSmoke(true);
                    System.out.println("Smoke simulation activated.");
                    break;
                } else if (option.equals("2")) {
                    // Turn smoke simulation off
                    Monitor.simulateSmoke(false);
                    System.out.println("Smoke simulation deactivated.");
                    break;
                } else if (isSprinklerOn && option.equals("3")) {
                    // Turn sprinkler off
                    Monitor.startSprinkler(false);
                    System.out.println("Sprinkler deactivated.");
                    break;
                } else if (option.equalsIgnoreCase("X")) {
                    // Here the user is done, so we set the Done flag and halt
                    // the security system. The monitor provides a method
                    // to do this. Its important to have processes release their queues
                    // with the message manager. If these queues are not released these
                    // become dead queues and they collect messages and will eventually
                    // cause problems for the message manager.
                    System.out.println("\nConsole Stopped... Exit monitor mindow to return to command prompt.");
                    Monitor.Halt();
                    return true;
                } else {
                    System.out.println("Invalid input: " + option);
                }
            }
        }
        return false;
    }
    
    private void showSprinklerConfirmation(){
        System.out.println("The fire alarm has been activated. Do you");
        System.out.println("want to activate the sprinkler?");
        System.out.println("1: Yes");
        System.out.println("2: No");
        System.out.print("\n>>>> ");
        
        while(Monitor.isSprinklerTriggered()){
            if(UserInput.KeyboardLineReady()){
                String option = UserInput.KeyboardReadString();
                if(option.equals("1")){
                    Monitor.startSprinkler(true);
                    System.out.println("Sprinkler activated.");
                    break;
                } else if(option.equals("2")){
                    Monitor.startSprinkler(false);
                    System.out.println("Sprinkler suppressed.");
                    break;
                } else {
                    System.out.println("Invalid input!");
                }
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
