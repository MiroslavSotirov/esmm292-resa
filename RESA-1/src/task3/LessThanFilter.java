package task3;

import java.util.ArrayList;
import java.util.List;

/**
 * ***************************************************************************************************************
 * File:WildPointsFilter.java
 * <p/>
 * Description:
 * <p/>
 * This filters pressure wildpoints in the data. The wildpoints are replaced by interpolated values.
 * The original value of the wildpoint is send to a separate pipe together with the timestamp.
 * <p/>
 * ****************************************************************************************************************
 */

public class LessThanFilter extends MeasurementFilterFramework {
    private final int id;
    private final double limit;
    
    private boolean lessThanLimit = false;
    private List<Measurement> frame = new ArrayList<Measurement>();

    /**
     * Instantiates a new PressureWildPointsFilter object.
     *
     * @param id The id of the pressure data
     * @param deviation The maximum deviation for valid measurements
     */
    public LessThanFilter(int id, double limit) {
        super(1, 2);
        this.id = id;
        this.limit = limit;
    }
    
    public void run() {

        while (true) {
            try {
                Measurement measurement = readMeasurementFromInput();
                
                if(measurement.getId() == 0){
                	int port = 0;
                	if(lessThanLimit){
                		port = 1;
                	}
                	for(Measurement m : frame){
                		writeMeasurementToOutput(m, port);
                	}
                	frame.clear();
                } else if (measurement.getId() == id){
                	if(measurement.getMeasurementAsDouble() < limit){
                		lessThanLimit = true;
                	} else {
                		lessThanLimit = false;
                	}
                }
                frame.add(measurement);

            } catch (EndOfStreamException e) {
                ClosePorts();
                System.out.print("\n" + this.getName() + "::WildPoints Exiting;");
                break;
            }
        }

    } // run

} // MiddleFilter