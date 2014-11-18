package task1;

/**
 * ***************************************************************************************************************
 * File:MeasurementFilterFramework.java
 * <p/>
 * Description:
 * <p/>
 * This Class extends the FilterFramework to be able to read and write Measurements to the pipes.
 * <p/>
 * ****************************************************************************************************************
 */
public class MeasurementFilterFramework extends FilterFramework {

    final int MEASUREMENT_LENGTH = 8;    // This is the length of all measurements (including time) in bytes
    final int ID_LENGTH = 4;                // This is the length of IDs in the byte stream

    /**
     * This method read a measurement from the input pipe and returns it
     *
     * @return input from the pipe
     * @throws EndOfStreamException If there is no more data from the pipe
     */
    Measurement readMeasurementFromInput() throws EndOfStreamException {
        byte databyte;                // This is the data byte read from the stream

        long measurement;                // This is the word used to store all measurements - conversions are illustrated.
        int id;                            // This is the measurement id

        try {
            id = 0;

            for (int i = 0; i < ID_LENGTH; i++) {
                databyte = ReadFilterInputPort();    // This is where we read the byte from the stream...

                id = id | (databyte & 0xFF);        // We append the byte on to ID...

                if (i != ID_LENGTH - 1)                // If this is not the last byte, then slide the
                {                                    // previously appended byte to the left by one byte
                    id = id << 8;                    // to make room for the next byte we append to the ID

                } // if

            } // for

            measurement = 0;

            for (int i = 0; i < MEASUREMENT_LENGTH; i++) {
                databyte = ReadFilterInputPort();
                measurement = measurement | (databyte & 0xFF);    // We append the byte on to measurement...

                if (i != MEASUREMENT_LENGTH - 1)                    // If this is not the last byte, then slide the
                {                                                // previously appended byte to the left by one byte
                    measurement = measurement << 8;                // to make room for the next byte we append to the
                    // measurement
                } // if

            } // if

            return new Measurement(id, measurement);
        } catch (Exception e) {
            throw new EndOfStreamException("Stream ended unexpectedly.");
        }

    }

    /**
     * This method writes the measurement to the output pipe
     * @param measurement The measurement to be written
     */
    void writeMeasurementToOutput(Measurement measurement) {
        byte[] bytesID = measurement.getIdAsByteArray();
        byte[] bytesMeasurement = measurement.getMeasurementAsByteArray();

        for (byte b : bytesID) {
            WriteFilterOutputPort(b);
        }

        for (byte b : bytesMeasurement) {
            WriteFilterOutputPort(b);
        }
    }

}
