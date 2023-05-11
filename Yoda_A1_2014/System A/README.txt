To run System A use
java -jar SystemA.jar

System A needs an input file called "FlightData.dat" in the working directory.
The input data must contain the measurements for Time, temperature and altitude (Ids 0, 2 and 4).
The input may contain measurements for velocity, pressure and attitude (Ids 1, 3 and 5) but they will be ignored.
The input must not contain any other measurements.

System A will produce a csv output in an output file called "OutputA.dat" with 
time, temperature (C), altitude (m)