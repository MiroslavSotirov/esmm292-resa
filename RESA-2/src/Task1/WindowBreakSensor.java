package Task1;

/******************************************************************************************************************
 * File:WindowBreakSensor.java
 *
 * Description:
 * This class simulates a window break sensor. It polls the message manager for simulation trigger messages. If the sensor is armed the
 * current status of the alarm is posted. The sensor can be armed and disarmed by the security console. If the alarm is activated when the
 * sensor is disarmed, the alarm is turned off.
 *
 * Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is on the local
 * machine.
******************************************************************************************************************/

class WindowBreakSensor extends IntrusionSensor {

    public WindowBreakSensor(String managerIP) {
        super(managerIP, 6, "Window Break Sensor", 0.5f, 0.3f);
    }

    public static void main(String args[]) {
        WindowBreakSensor sensor;
        if (args.length == 0) {
            sensor = new WindowBreakSensor(null);
        } else {
            sensor = new WindowBreakSensor(args[0]);
        }
        sensor.run();
    }

}
