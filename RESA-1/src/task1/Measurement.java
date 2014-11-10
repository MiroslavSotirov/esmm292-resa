package task1;

import java.nio.ByteBuffer;

/******************************************************************************************************************
* File:Measurement.java
* Description:
*	Used to hold measurement tuples.
*
******************************************************************************************************************/

public class Measurement {

	private int id;
	private long measurement;
	
	public Measurement(int id, long measurement) {
		super();
		this.id = id;
		this.measurement = measurement;
	}

    public byte[] getIdAsByteArray() {
        return ByteBuffer.allocate(4).putInt(this.getId()).array();
    }

    public byte[] getMeasurementAsByteArray() {
        return ByteBuffer.allocate(8).putLong(this.getMeasurement()).array();
    }

	public String toString(){
		return "Measurement with ID: "+id+" and Value: "+measurement;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getMeasurement() {
		return measurement;
	}
	public void setMeasurement(long measurement) {
		this.measurement = measurement;
	}
	
}
