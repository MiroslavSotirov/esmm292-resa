package task2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	File file;
	FileWriter fw;
	BufferedWriter bw;

	/**
	 * Set the order of Columns to print to the Console
	 *
	 * @param orderedIds
	 *            The order of the Ids
	 */
	public SinkFilter(int[] orderedIds, String fileName) {
		super(1, 1);
		this.orderedIds = orderedIds;

		file = new File(fileName);

		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void run() {

		/**
		 * Initialize the HashMap for a DataFrame
		 */
		HashMap<Integer, Measurement> outputMap = new HashMap<Integer, Measurement>();
		Measurement m;
		String outputString = "";

		try {

			while (true) {

				Measurement readMeasurement = readMeasurementFromInput();
				outputMap.put(readMeasurement.getId(), readMeasurement);

				// Print the required Measurements in the given order
				if (outputMap.size() == orderedIds.length) {
					for (int i = 0; i < orderedIds.length; i++) {
						int orderedId = orderedIds[i];
						m = outputMap.get(orderedId);
						if (m == null){
							m = outputMap.get(orderedId | (1 << 5));
						}
						outputString+= m.getMeasurementAsString();
						if (i < orderedIds.length -1){
							outputString += ',';
						}
					}
					bw.write(outputString);
					bw.newLine();
					outputString = "";
					outputMap.clear();
				}

			}
		} catch (EndOfStreamException | IOException e) {
			ClosePorts();
			try {
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println(this.getName() + "::Sink Exiting;");
		}
	}
}