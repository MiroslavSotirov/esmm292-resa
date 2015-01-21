package Task3_2;

/******************************************************************************************************************
 * File:MaintenanceConsole.java
 *
 * Description: This class is the console for the museum maintenance system. This process consists of two
 * threads. The MaintenanceMonitor object is a thread that is started that is responsible for the monitoring of
 * the museum system devices. The main thread provides a text interface that shows the status of all devices in the system.
 ******************************************************************************************************************/

public class MaintenanceConsole {

    private static final int INTERVAL = 1000;
    
    private MaintenanceMonitor Monitor = null;

    public MaintenanceConsole(String managerIP, float winPosX, float winPosY) {
        Monitor = new MaintenanceMonitor(managerIP);
    }

    private void run() {
        Monitor.start();

        while (!Monitor.isStopped()) {
            System.out.println("-----------------------------------------");
            for(String status : Monitor.getStatuses()){
                System.out.println(status);
            }

            // Wait a while before entering the next iteration.
            try {
                Thread.sleep(INTERVAL);
            } catch (Exception e) {
                System.out.println("Sleep error:: " + e);
            }
        }
    }

    public static void main(String args[]) {
        MaintenanceConsole console;
        if (args.length == 0) {
            console = new MaintenanceConsole(null, 0.0f, 0.5f);
        } else {
            console = new MaintenanceConsole(args[0], 0.0f, 0.5f);
        }
        console.run();
    }

}
