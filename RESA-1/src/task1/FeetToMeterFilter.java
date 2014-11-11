package task1;

/**
 * ***************************************************************************************************************
 * File:AltidueFilter.java
 * <p/>
 * Description:
 * <p/>
 * Converts the Measurement with the given Id from feet to meters.
 * The conversion rate we use is: 3.2808 Feet to 1 Meter
 * <p/>
 * ****************************************************************************************************************
 */

public class FeetToMeterFilter extends MeasurementFilterFramework {
    private final int id;

    /**
     * Instantiates a new FeetToMeterFilter object.
     *
     * @param id to remove
     */
    public FeetToMeterFilter(int id) {
        super();
        this.id = id;
    }

    public void run() {

        while (true) {
            try {
                Measurement measurement = readMeasurementFromInput();

                if (measurement.getId() == this.id) {

                    double feet = measurement.getMeasurementAsDouble();
                    double meters = feet / 3.2808;
                    measurement.setMeasurement(Double.doubleToLongBits(meters));

                    //System.out.println("Converting "+feet+" to "+meters+" and the new Measurement is:"+measurement);
                }

                writeMeasurementToOutput(measurement);
            } catch (EndOfStreamException e) {
                ClosePorts();
                System.out.print("\n" + this.getName() + "::Delete Exiting;");
                break;
            }
        }

    } // run

} // MiddleFilter