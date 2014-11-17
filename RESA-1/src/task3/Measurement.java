package task3;

import java.nio.ByteBuffer;
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
    private SimpleDateFormat TimeStampFormat;

    public Measurement(int id, long measurement) {
        super();
        this.id = id;
        this.measurement = measurement;
        TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");
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
    	// TODO format output
        if (id == 0) {
            return TimeStampFormat.format(getMeasurementAsCalendar().getTime());
        } else {
            return Double.toString(getMeasurementAsDouble());
        }
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
