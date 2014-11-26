To run System C use
java -jar SystemC.jar

System C needs an input file called "FlightData.dat" in the working directory.
The input data must contain the measurements for Time, velocity, temperature, pressure, altitude and attitude (Ids 0, 1, 2, 3, 4 and 5).
The input must not contain any other measurements.

System C will produce a csv output in an output file called "OutputC.dat" for the data from at least 10'000 feet altitude:
	time, velocity, altitude (feet), pressure (psi), temperature (°F), attitude

extrapolated pressure measurements are denoted with an * and the original value and the corresponding timestamp are retained in "PressureWildpoints.dat" with
	time, pressure (psi)

All measurements from below 10'000 feet are stored in "LessThan10k.dat" with
	time, velocity, altitude (feet), pressure (psi), temperature (°F), attitude
No extrapolation is performed for the data below 10'000 feet.