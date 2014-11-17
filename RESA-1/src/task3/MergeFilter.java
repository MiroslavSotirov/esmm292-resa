package task3;

/**
 * *****************************************************************************
 * File: MergeFilter.java
 * <p/>
 * Description:
 * <p/>
 * Merges two Inputs and sorts them by Date.
 * <p/>
 * *****************************************************************************
 */

public class MergeFilter extends MeasurementFilterFramework {

    /**
     * Instantiates a new MergeFilter object.
     */
    public MergeFilter() {
        super(2, 1);
    }

    /**
     * This method gets the last measurement and writes it to
     *
     * TODO: Write Comment
     *
     * @param measurement
     * @param portID
     * @return
     */
    private Measurement forward(Measurement measurement, int portID) {
        try {
            do {
                writeMeasurementToOutput(measurement);
                measurement = readMeasurementFromInput(portID);
            } while (measurement.getId() != 0);
        } catch (EndOfStreamException e) {
            return null;
        }
        return measurement;
    }

    public void run() {
        // TODO breaks if one source has no measurements
        Measurement measurementA;
        Measurement measurementB;

        try {
            measurementA = readMeasurementFromInput(0);
            measurementB = readMeasurementFromInput(1);

        while (true)   
            
				if (measurementA == null && measurementB == null) {
					break;
				} else if (measurementA == null) {
					measurementB = forward(measurementB, 1);
				} else if (measurementB == null) {
					measurementA = forward(measurementA, 0);
				} else if (measurementA.getMeasurementAsCalendar().compareTo(
						measurementB.getMeasurementAsCalendar()) <= 0) {
					measurementA = forward(measurementA, 0);
				} else {
					measurementB = forward(measurementB, 1);
				}
			}

		} catch (EndOfStreamException e) {
			ClosePorts();
			System.out.print("\n" + this.getName() + "::WildPoints Exiting;");
		}

	} // run

} // MiddleFilter