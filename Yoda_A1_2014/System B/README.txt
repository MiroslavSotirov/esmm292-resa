To run System B use
java -jar SystemB.jar

System B needs an input file called "FlightData.dat" in the working directory.
The input data must contain the measurements for Time, temperature, pressure and altitude (Ids 0, 2, 3 and 4).
The input may contain measurements for velocity and attitude (Ids 1 and 5) but they will be ignored.
The input must not contain any other measurements.

System B will produce a csv output in an output file called "OutputB.dat" with 
time, temperature (°C), altitude (m), pressure (psi)
extrapolated pressure measurements are denoted with an * and the original value and the corresponding timestamp are retained in "Wildpoints.dat" with
time, pressure (psi)