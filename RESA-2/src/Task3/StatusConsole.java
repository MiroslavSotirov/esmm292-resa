package Task3;
/******************************************************************************************************************
* File:ECSConsole.java
* Course: 17655
* Project: Assignment 3
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*	1.0 February 2009 - Initial rewrite of original assignment 3 (ajl).
*
* Description: This class is the console for the museum environmental control system. This process consists of two
* threads. The ECSMonitor object is a thread that is started that is responsible for the monitoring and control of
* the museum environmental systems. The main thread provides a text interface for the user to change the temperature
* and humidity ranges, as well as shut down the system.
*
* Parameters: None
*
* Internal Methods: None
*
******************************************************************************************************************/
import java.util.Calendar;
import java.util.List;

import TermioPackage.*;

public class StatusConsole
{
	
	private static void printID(int id, List<Integer> defects){
		if (defects.contains(id)){
			System.out.print("Error:\t");
		} else {
			System.out.print("Live:\t");
		}
		switch (id){
		case (Constants.ID_HUMIDITY_SENSOR):
			System.out.println("Humidity Sensor. Reports relative humidity in %");
			break;
		case (Constants.ID_DOORBREAK_SENSOR):
			System.out.println("Doorbreak Sensor. Raises an alarm if the door is broken.");
			break;
		case (Constants.ID_WINDOWBREAK_SENSOR):
			System.out.println("Windowbreak Sensor. Raises an alarm if the window is broken.");
			break;
		case (Constants.ID_MOTION_SENSOR):
			System.out.println("Motion Sensor. Raises an alarm if motion is detected.");
			break;
		case (Constants.ID_HUMIDITY_CONTROLLER):
			System.out.println("Humidity Controller. Controls the de- and humidifier.");
			break;
		case (Constants.ID_TEMP_CONTROLLER):
			System.out.println("Teperature Controller. Controls the heater and chiller.");
			break;
		case (Constants.ID_TEMP_SENSOR):
			System.out.println("Temperature Sensor. Checks teperature in °F.");
			break;
		case (Constants.ID_FIRE_ALARM_CONTROLLER):
			System.out.println("Fire alarm controller. Controls the smoke sensor and sprinkler controller.");
			break;
		case (Constants.ID_SPRINKLER_CONTROLLER):
			System.out.println("Sprinkler controller. Controls sprinkler.");
			break;
		case (Constants.ID_SMOKE_SENSOR):
			System.out.println("Smoke sensor. Checks whether smoke is present.");
			break;
		default: 
			break;
		}
	}
	public static void main(String args[])
	{
    	Termio UserInput = new Termio();	// Termio IO Object
		StatusMonitor Monitor = null;			// The environmental control system monitor

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the message manager
		/////////////////////////////////////////////////////////////////////////////////

 		if ( args.length != 0 )
 		{
			// message manager is not on the local system

			Monitor = new StatusMonitor( args[0] );

		} else {

			Monitor = new StatusMonitor();

		} // if


		// Here we check to see if registration worked. If ef is null then the
		// message manager interface was not properly created.

		if (Monitor.IsRegistered() )
		{
			Monitor.start(); // Here we start the monitoring and control thread

			System.out.println( "\n\n\n\n" );
			System.out.println( "ECS Status Console: \n" );

			if (args.length != 0)
				System.out.println( "Using message manger at: " + args[0] + "\n" );
			else
				System.out.println( "Using local message manger \n" );

			while (Monitor.IsRegistered())
			{
				System.out.println("Status for: " + Calendar.getInstance().getTime() + "-----------------------");
				List<Integer> defects = Monitor.getDefectComponents();
				printID(Constants.ID_HUMIDITY_SENSOR, defects);
				printID(Constants.ID_DOORBREAK_SENSOR, defects);
				printID(Constants.ID_WINDOWBREAK_SENSOR, defects);
				printID(Constants.ID_MOTION_SENSOR, defects);
				printID(Constants.ID_HUMIDITY_CONTROLLER, defects);
				printID(Constants.ID_TEMP_CONTROLLER, defects);
				printID(Constants.ID_TEMP_SENSOR, defects);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} // while

		} else {

			System.out.println("\n\nUnable start the monitor.\n\n" );

		} // if

  	} // main

} // ECSConsole
