package task1;

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
