To prepare each of the systems, follow these instructions:
1. Open a command line prompt in the directory where you unzipped the archive.
2. Navigate to the src folder (e.g. System_A/src)
3. Compile the first part of the application by entering: javac *.java
4. Compile the second part of the application by entering: javac Task{1,2,3}/*.java
5. Type rmic MessageManager to build the Message Manager interface stubs.
6. Before you start the Message Manager or Museum Environmental Control System, make sure that port 1099 is available on your system.

To run the system, first start the manager by executing EMStart.bat and then execute SC{1,2,3}Start.bat respectively.