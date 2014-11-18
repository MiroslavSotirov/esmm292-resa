package task1;

/**
 * ***************************************************************************************************************
 * File:DeleteFilter.java
 * <p/>
 * Description:
 * <p/>
 * This class will read each measurement and check it for the id.
 * If the Id equals the id to delete it will not output that measurement.
 * All measurements not equal to the given id, will be outputted.
 * <p/>
 * ****************************************************************************************************************
 */

public class DeleteFilter extends MeasurementFilterFramework {
    private final int id;

    /**
     * Instantiates a new DeleteFilter object,
     * which deletes the part of a frame which contains the measurement with <code>id</code>.
     *
     * @param id of measurement to remove
     */
    public DeleteFilter(int id) {
        super();
        this.id = id;
    }

    /**
     * This method will read each measurement and check it for the id.
     * If the Id equals the id to delete it will not output that measurement.
     * All measurements not equal to the given id, will be outputted.
     */
    @Override
    public void run() {

        while (true) {
            try {
                Measurement measurement = readMeasurementFromInput();

                if (measurement.getId() != this.id) {
                    writeMeasurementToOutput(measurement);
                }
            } catch (EndOfStreamException e) {
                ClosePorts();
                System.out.print("\n" + this.getName() + "::Delete Exiting;");
                break;
            }
        }

    }
}