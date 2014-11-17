package task2;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * ***************************************************************************************************************
 * File:Measurement.java
 * Description:
 * Used to hold measurement tuples.
 * ****************************************************************************************************************
 */

public class Measurement {

    private int id;
    private long measurement;
    /*
     * Formatting styles used in the output, the decimal separator is set in the constructor
     */
    private static final SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");
    private static final DecimalFormat TEMPERATURE_FORMAT = new DecimalFormat("000.00000");
    private static final DecimalFormat ALTITUDE_FORMAT = new DecimalFormat("000000.00000");
    private static final DecimalFormat PRESSURE_FORMAT = new DecimalFormat("00.00000");

    /*
     * The ids used in the stream
     */
    public static final int ID_TIME = 0,
    		ID_VELOCITY = 1,
    		ID_ALTITUDE = 2,
    		ID_PRESSURE = 3,
    		ID_TEMPERATURE = 4,
    		ID_ATTITUDE = 5,
    		ID_WILDPOINT= ID_PRESSURE | (1 << 5);
    
    
    public Measurement(int id, long measurement) {
        super();
        this.id = id;
        this.measurement = measurement;
        DecimalFormatSymbols pointSep = new DecimalFormatSymbols();
        pointSep.setDecimalSeparator('.');
        TEMPERATURE_FORMAT.setDecimalFormatSymbols(pointSep);
        ALTITUDE_FORMAT.setDecimalFormatSymbols(pointSep);
        PRESSURE_FORMAT.setDecimalFormatSymbols(pointSep);
    }

    public Measurement(int id, double measurement) {
		this(id, Double.doubleToLongBits(measurement));
	}

	public byte[] getIdAsByteArray() {
        return ByteBuffer.allocate(4).putInt(this.getId()).array();
    }

    /**
     * Will convert the Measurement to ByteArray.
     * <p/>
     * The conversion will be done depending on the ID according to the specification
     * <p/>
     * ID = 0 will be converted as Timestamp
     * ID = 1 - 5 will be converted as Double
     *
     * @return ByteArray of the Measurement
     */
    public byte[] getMeasurementAsByteArray() {
        if (id == 0) {
            return ByteBuffer.allocate(8).putLong(this.getMeasurement()).array();
        } else {
            return ByteBuffer.allocate(8).putDouble(this.getMeasurementAsDouble()).array();
        }
    }

    public long getMeasurement() {
        return measurement;
    }

    /**
     * This Method returns the Measurement as a String
     *
     * @return String
     */
    public String getMeasurementAsString() {
    	String representation;
    	switch (id){
    	case ID_TIME:
            representation = TimeStampFormat.format(getMeasurementAsCalendar().getTime());
            break;
    	case ID_ALTITUDE:
    		representation = ALTITUDE_FORMAT.format(getMeasurementAsDouble());
    		break;
    	case ID_PRESSURE:
    		representation = PRESSURE_FORMAT.format(getMeasurementAsDouble());
    		break;
    	case ID_WILDPOINT:
    		representation = PRESSURE_FORMAT.format(getMeasurementAsDouble()) + "*";
    		break;
    	case ID_TEMPERATURE:
    		representation = TEMPERATURE_FORMAT.format(getMeasurementAsDouble());
    		break;
    	default:
        	representation = Double.toString(getMeasurementAsDouble());
        	break;
    	}
    	
        return representation;
    }

    /**
     * Returns the Measurement converted to double. This is needed for ID = 1-5
     *
     * @return double
     */
    public double getMeasurementAsDouble() {
        return Double.longBitsToDouble(measurement);
    }

    /**
     * Returns the Measurement converted to a Calendar instance. This is needed for ID = 0
     *
     * @return Calendar
     */
    public Calendar getMeasurementAsCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(measurement);
        return calendar;
    }

    public String toString() {
        return "Measurement with ID: " + id + " and Value: " + getMeasurementAsString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMeasurement(long measurement) {
        this.measurement = measurement;
    }

}
