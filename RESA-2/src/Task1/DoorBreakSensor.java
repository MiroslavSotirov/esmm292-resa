package Task1;

/******************************************************************************************************************
 * File:DoorBreakSensor.java
 *
 * Description:
 * This class simulates a door break sensor. It polls the message manager for simulation trigger messages. If the sensor is armed the
 * current status of the alarm is posted. The sensor can be armed and disarmed by the security console. If the alarm is activated when the
 * sensor is disarmed, the alarm is turned off.
 *
 * Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is on the local
 * machine.
******************************************************************************************************************/

class DoorBreakSensor extends IntrusionSensor {

    public DoorBreakSensor(String managerIP) {
        super(managerIP, 7, "Door Break Sensor", 0.5f, 0.5f);
    }

    public static void main(String args[]) {
        DoorBreakSensor sensor;
        if (args.length == 0) {
            sensor = new DoorBreakSensor(null);
        } else {
            sensor = new DoorBreakSensor(args[0]);
        }
        sensor.run();
    }

}
