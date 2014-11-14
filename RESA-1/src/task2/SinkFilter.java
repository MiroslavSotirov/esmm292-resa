package task2;

import java.util.HashMap;

/**
 * ***************************************************************************************************************
 * File:SinkFilter.java
 * <p/>
 * Description:
 * <p/>
 * This class writes the desired Output in the given Order to the Console.
 * <p/>
 * ****************************************************************************************************************
 */


public class SinkFilter extends MeasurementFilterFramework {

    private int[] orderedIds;

    /**
     * Set the order of Columns to print to the Console
     *
     * @param orderedIds The order of the Ids
     */
    public SinkFilter(int[] orderedIds) {
        super(1, 1);
        this.orderedIds = orderedIds;

    }

    public void run() {

        /**
         * Initialize the HashMap for a DataFrame
         */
        HashMap<Integer, Measurement> outputMap = new HashMap<Integer, Measurement>();
        Measurement m;

        try {

            while (true) {

                Measurement readMeasurement = readMeasurementFromInput();
                outputMap.put(readMeasurement.getId(), readMeasurement);

                // Print the required Measurements in the given order
                if (outputMap.size() == orderedIds.length) {
                    for (int orderedId : orderedIds) {
                        m = outputMap.get(orderedId);
                        System.out.print(m.getMeasurementAsString());
                        System.out.print(",");
                    }
                    System.out.println();
                    outputMap.clear();
                }

            }
        } catch (EndOfStreamException e) {
            ClosePorts();
            System.out.println(this.getName() + "::Sink Exiting;");
        }
    }
}