package task1;
/******************************************************************************************************************
 * File:SinkFilter.java
 * Course: 17655
 * Project: Assignment 1
 * Copyright: Copyright (c) 2003 Carnegie Mellon University
 * Versions:
 *	1.0 November 2008 - Sample Pipe and Filter code (ajl).
 *
 * Description:
 *
 * This class serves as an example for using the SinkFilterTemplate for creating a sink filter. This particular
 * filter reads some input from the filter's input port and does the following:
 *
 *	1) It parses the input stream and "decommutates" the measurement ID
 *	2) It parses the input steam for measurments and "decommutates" measurements, storing the bits in a long word.
 *
 * This filter illustrates how to convert the byte stream data from the upstream filterinto useable data found in
 * the stream: namely time (long type) and measurements (double type).
 *
 *
 * Parameters: 	None
 *
 * Internal Methods: None
 *
 ******************************************************************************************************************/

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SinkFilter extends MeasurementFilterFramework {

    private int numberOfOutputColumns;

    /**
     * Set the number of Columns to print for File
     *
     * @param numberOfOutputColumns
     */
    public SinkFilter(int numberOfOutputColumns) {
        super();
        this.numberOfOutputColumns = numberOfOutputColumns;
    }

    public void run() {

        /**
         * Initialize the ArrayList for a DataFrame
         */
        ArrayList<Measurement> outputList = new ArrayList<Measurement>();

        try {

            while (true) {

                outputList.add(readMeasurementFromInput());

                if (outputList.size() == numberOfOutputColumns) {
                    for (Measurement m : outputList) {
                        System.out.print(m.getMeasurementAsString());
                        System.out.print(" ");
                    }
                    System.out.println();
                    outputList.clear();
                }

            }
        } catch (EndOfStreamException e) {
            ClosePorts();
            System.out.println(this.getName() + "::Sink Exiting;");
        }
    }
}