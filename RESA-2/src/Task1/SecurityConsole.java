package Task1; /******************************************************************************************************************
* File:SecurityConsole.java
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

import MessagePackage.Message;
import TermioPackage.Termio;

public class SecurityConsole
{
	public static void main(String args[])
	{
    	Termio UserInput = new Termio();	// Termio IO Object
		boolean Done = false;				// Main loop flag
		String Option = null;				// Menu choice from user
		Message Msg = null;					// Message object
		boolean Error = false;				// Error flag
		SecurityMonitor Monitor = null;		// The security monitor
        boolean SystemArmed = true;        // States if the System is armed

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the message manager
		/////////////////////////////////////////////////////////////////////////////////

 		if ( args.length != 0 )
 		{
			// message manager is not on the local system

			Monitor = new SecurityMonitor( args[0] );

		} else {

			Monitor = new SecurityMonitor();

		} // if


		// Here we check to see if registration worked. If ef is null then the
		// message manager interface was not properly created.

		if (Monitor.IsRegistered() )
		{
			Monitor.start(); // Here we start the monitoring and control thread

			while (!Done)
			{
				// Here, the main thread continues and provides the main menu

				System.out.println( "\n\n\n\n" );
				System.out.println( "Security Console: \n" );

				if (args.length != 0)
					System.out.println( "Using message manger at: " + args[0] + "\n" );
				else
					System.out.println( "Using local message manger \n" );

                if (SystemArmed){
                    System.out.println("System is Armed!");
                }else{
                    System.out.println("System is NOT Armed!");
                }
                System.out.println("\n");

				System.out.println( "Select an Option: \n" );
				System.out.println( "1: Arm the system" );
				System.out.println( "2: Unarm the system" );
                System.out.println( "W1: Activate Window Break Alarm" );
                System.out.println( "W0: Deactivate Window Break Alarm" );
                System.out.println( "D1: Activate Door Break Alarm" );
                System.out.println( "D0: Deactivate Door Break Alarm" );
                System.out.println( "M1: Activate Motion Detection Alarm" );
                System.out.println( "M0: Deactivate Motion Detection Alarm" );
				System.out.println( "X: Stop System\n" );
				System.out.print( "\n>>>> " );
				Option = UserInput.KeyboardReadString();

				//////////// option 1 ////////////

                if ( Option.equals( "1" ) )
                {
                    // Here we arm the system
                    Monitor.ArmSystem(true);
                    SystemArmed = true;

                } // if

                //////////// option 2 ////////////

                if ( Option.equals( "2" ) )
                {
                    // Here we unarm the system
                    Monitor.ArmSystem(false);
                    SystemArmed = false;

                } // if

                //////////// option W1 ////////////

                if ( Option.equals( "W1" ) )
                {
                    // Trigger Window Alarm
                    Monitor.simulateWindowBreak(true);

                } // if

                //////////// option W0 ////////////

                if ( Option.equals( "W0" ) )
                {
                    // Untrigger Window Alarm
                    Monitor.simulateWindowBreak(false);

                } // if

                //////////// option D1 ////////////

                if ( Option.equals( "D1" ) )
                {
                    // Trigger Door Alarm
                    Monitor.simulateDoorBreak(true);

                } // if

                //////////// option D0 ////////////

                if ( Option.equals( "D0" ) )
                {
                    // Untrigger Door Alarm
                    Monitor.simulateDoorBreak(false);

                } // if

                //////////// option M1 ////////////

                if ( Option.equals( "M1" ) )
                {
                    // Trigger Motion Alarm
                    Monitor.simulateMotionDetection(true);

                } // if

                //////////// option M0 ////////////

                if ( Option.equals( "M0" ) )
                {
                    // Untrigger Motion Alarm
                    Monitor.simulateMotionDetection(false);

                } // if

				//////////// option X ////////////

				if ( Option.equalsIgnoreCase( "X" ) )
				{
					// Here the user is done, so we set the Done flag and halt
					// the security system. The monitor provides a method
					// to do this. Its important to have processes release their queues
					// with the message manager. If these queues are not released these
					// become dead queues and they collect messages and will eventually
					// cause problems for the message manager.

					Monitor.Halt();
					Done = true;
					System.out.println( "\nConsole Stopped... Exit monitor mindow to return to command prompt." );
					Monitor.Halt();

				} // if

			} // while

		} else {

			System.out.println("\n\nUnable start the monitor.\n\n");

		} // if

  	} // main

} // ECSConsole
