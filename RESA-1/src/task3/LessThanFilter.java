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
    
    private boolean lookingForValidMeasurement = false;
    private Measurement lastValidPoint;
    private List<Measurement> frame = new ArrayList<Measurement>();
    private List<Measurement> cache = new ArrayList<Measurement>();

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
    
    /**
     * A pressure measurement is invalid if it either is negative
     * or the deviation to the last valid value is more then specified.
     * 
     * @param measurement The measurement that shall be checked
     * @return True if the measurement is valid, false otherwise
     */
    private boolean isValid(Measurement measurement){
    	if(measurement.getMeasurementAsDouble() < 0){
    		return false;
    	}
    	if(lastValidPoint != null){
    		return Math.abs(measurement.getMeasurementAsDouble() - lastValidPoint.getMeasurementAsDouble()) <= limit;
    	}
    	return true;
    }
    
    /**
     * Interpolates between the last and next valid measurement.
     * If either of the measurements is non-existent, the other is choosen.
     * At least one of the measurements must be existent.
     * 
     * @param lastValid The last valid Measurement
     * @param nextValid The next valid Measurement
     * @return The interpolated measurement
     */
    private Measurement interpolate(Measurement lastValid, Measurement nextValid){
    	if(lastValid == null && nextValid == null){
    		throw new NullPointerException("lastValid and nextValid are null");
    	} else if(lastValid == null){
    		return new Measurement(nextValid.getId(), nextValid.getMeasurementAsDouble());
    	} else if(nextValid == null){
    		return new Measurement(lastValid.getId(), lastValid.getMeasurementAsDouble());
    	} else {
    		return new Measurement(lastValid.getId(), (lastValid.getMeasurementAsDouble() + nextValid.getMeasurementAsDouble()) / 2);
    	}
    }

    public void run() {

        while (true) {
            try {
            	// TODO handle case when last pressure point is invalid
                Measurement measurement = readMeasurementFromInput();
                
                /****************************************************************
                 * This code stores the complete frame.
                 * If the pressure measurement is valid, the frame is written
                 * to the first output port.
                 * If the pressure measurement is invalid, the input port is
                 * forwarded into a cache until the next valid pressure data
                 * is read. Then the current frame and the frames in the cache
                 * are processed. The frames are written to the first output
                 * port. The invalid pressure measurements are replaced and
                 * the original values are written (together with the timestamp)
                 * to the second output port.
                 ***************************************************************/
                
                if (lookingForValidMeasurement) {
            		cache.add(measurement);
                	if (measurement.getId() == this.id && isValid(measurement)) {
                		// not looking for valid measurement anymore
                		lookingForValidMeasurement = false;
                		// process cache
                		for (Measurement cached : cache) {
                        	if (cached.getId() == 0) {
                        		// write the complete frame to the first output port
                        		// timestamp and the wildpoint are also written to the second output port
                        		for(Measurement m : frame){
                        			if(m.getId() == 0){
                        				writeMeasurementToOutput(m, 0);
                        				writeMeasurementToOutput(m, 1);
                        			} else if (m.getId() == this.id) {
                        				// TODO add asterisk
                        				writeMeasurementToOutput(interpolate(lastValidPoint, measurement), 0);
                        				writeMeasurementToOutput(m, 1);
                        			} else {
                        				writeMeasurementToOutput(m, 0);
                        			}
                        		}
                        		frame.clear();
                        	}
                    		frame.add(cached);
                		}
                		cache.clear();
                	}
                } else {
                	if (measurement.getId() == 0) {
                		// write frame to first output port
                		for (Measurement m : frame) {
                			writeMeasurementToOutput(m, 0);
                		}
                		frame.clear();
                	} else {
                		if (measurement.getId() == this.id) {
                			if (isValid(measurement)) {
                				lastValidPoint = measurement;
                			} else {
                				lookingForValidMeasurement = true;
                			}
                		}
                	}
            		frame.add(measurement);
                }

            } catch (EndOfStreamException e) {
                ClosePorts();
                System.out.print("\n" + this.getName() + "::WildPoints Exiting;");
                break;
            }
        }

    } // run

} // MiddleFilter