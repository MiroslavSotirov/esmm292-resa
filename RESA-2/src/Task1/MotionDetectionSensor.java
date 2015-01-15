package Task1;

/******************************************************************************************************************
 * File:MotionDetectionSensor.java
 *
 * Description:
 * This class simulates a motion detection sensor. It polls the message manager for simulation trigger messages. If the sensor is armed the
 * current status of the alarm is posted. The sensor can be armed and disarmed by the security console. If the alarm is activated when the
 * sensor is disarmed, the alarm is turned off.
 *
 * Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is on the local
 * machine.
******************************************************************************************************************/

class MotionDetectionSensor extends IntrusionSensor {

    public MotionDetectionSensor(String managerIP) {
        super(managerIP, 8, "Motion Detection Sensor", 0.5f, 0.7f);
    }

    public static void main(String args[]) {
        MotionDetectionSensor sensor;
        if (args.length == 0) {
            sensor = new MotionDetectionSensor(null);
        } else {
            sensor = new MotionDetectionSensor(args[0]);
        }
        sensor.run();
    }

}
