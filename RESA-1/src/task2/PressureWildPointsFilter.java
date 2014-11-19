package task2;

import java.util.ArrayList;
import java.util.List;

/**
 * ***************************************************************************************************************
 * File:PressureWildPointsFilter.java
 * <p/>
 * Description:
 * <p/>
 * This Filter filters pressure wildpoints in the data. The wildpoints are replaced by interpolated values.
 * The original value of the wildpoint is send to a separate pipe together with the timestamp.
 * <p/>
 * A wild point is any pressure data that varies more than 10PSI between samples
 * and/or is negative.For wild points encountered in the stream, extrapolate a replacement value
 * by using the last known valid measurement and the next valid measurement in the stream.
 * Extrapolate the replacement value by computing the average of the last valid measurement and
 * the next valid measurement in the stream. If a wild point occurs at the beginning of the stream,
 * replace it with the first valid value; if a wild point occurs at the end of the stream, replace it with
 * the last valid value.
 * <p/>
 * ****************************************************************************************************************
 */

public class PressureWildPointsFilter extends MeasurementFilterFramework {
    private final int id;
    private final int interpolateId;
    private final double deviation;

    /**
     * The cache of the filter, it's used to store the data with the exception of
     * the valid pressure measurements. Any pressure measurement in this list will be interpolated.
     */
    private List<Measurement> cache = new ArrayList<>();

    /**
     * Instantiates a new PressureWildPointsFilter object.
     *
     * @param id        The id of the pressure data
     * @param deviation The maximum deviation for valid measurements
     */
    public PressureWildPointsFilter(int id, double deviation) {
        super(1, 2);
        this.id = id;
        interpolateId = id | (1 << 5);
        this.deviation = deviation;
    }

    /**
     * A pressure measurement is invalid if it either is negative
     * or the deviation to the last valid value is more then specified.
     *
     * @param measurement The measurement that shall be checked
     * @return True if the measurement is valid, false otherwise
     */
    private boolean isValid(Measurement lastValidPoint, Measurement measurement) {
        if (measurement.getMeasurementAsDouble() < 0) {
            return false;
        } // if
        if (lastValidPoint != null) {
            return Math.abs(measurement.getMeasurementAsDouble() - lastValidPoint.getMeasurementAsDouble()) <= deviation;
        } // if
        return true;
    } // isValid

    /**
     * Interpolates between the last and next valid measurement.
     * If either of the measurements is non-existent, the other is choosen.
     * At least one of the measurements must be existent.
     *
     * @param lastValid the last valid measurement
     * @param nextValid the next valid measurement
     * @return The interpolated measurement
     */
    private Measurement interpolate(Measurement lastValid, Measurement nextValid) {
        if (lastValid == null && nextValid == null) {
            throw new NullPointerException("lastValid and nextValid are null");
        } else if (lastValid == null) {
            return new Measurement(interpolateId, nextValid.getMeasurementAsDouble());
        } else if (nextValid == null) {
            return new Measurement(interpolateId, lastValid.getMeasurementAsDouble());
        } else {
            return new Measurement(interpolateId, (lastValid.getMeasurementAsDouble() + nextValid.getMeasurementAsDouble()) / 2);
        } // if
    } // interpolate
    
    /**
     * Processes the cache, interpolating all pressure points with the {@link #lastValidPoint} and
     * given next valid point. <br><br>
     * <em>Clears the cache after forwarding it!</em>
     * @param validMeasurement next valid measurement
     */
    private void processCache(Measurement lastValidPoint, Measurement validMeasurement){
    	for (Measurement m : cache){
    		if (m.getId() == Measurement.ID_TIME){
    			writeMeasurementToOutput(m, 0);
    			writeMeasurementToOutput(m, 1);
    		} else if (m.getId() == this.id){
    			Measurement interpolated = interpolate(lastValidPoint, validMeasurement);
    			writeMeasurementToOutput(interpolated, 0);
    			writeMeasurementToOutput(m, 1);
    		} else {
    			writeMeasurementToOutput(m);
    		}
    	} // for: cache
    	
    	if (validMeasurement != null){
    		writeMeasurementToOutput(validMeasurement);
    	} // if
    	
    	cache.clear();
    } // processCache
    
    public void run() {

    	/*
    	 * Iterates over the input steam and reads non-pressure measurements into a cache to retain 
    	 * them if the pressure value is invalid.
    	 * If a non-valid pressure is found it's also added to the cache.
    	 * If a valid measurement is found the current cache is forwarded to the output streams
    	 * though the processCache procedure. 
    	 * 
    	 * All non-valid pressures must be added to the cache.
    	 * Valid pressures must not be added to the cache.
    	 */
    	Measurement lastValidPoint = null;
        while (true) {
            try {
                Measurement measurement = readMeasurementFromInput();

                if (measurement.getId() == this.id){
                	if (isValid(lastValidPoint, measurement)){
                		// flushing the cache with the current valid point
                		processCache(lastValidPoint, measurement);
                		lastValidPoint = measurement;
                	} else {
                		// cache the wild point
                    	cache.add(measurement);
                	} // if
                } else {
                	// cache the remaining data
                	cache.add(measurement);
                } // if


            } catch (EndOfStreamException e) {
            	/*
            	 * Flush the remaining cache
            	 */
            	processCache(lastValidPoint, null);
                ClosePorts();
                System.out.print("\n" + this.getName() + "::WildPoints Exiting;");
                break;
            } // try
        }

    } // run

} // PressureWildPointsFilter