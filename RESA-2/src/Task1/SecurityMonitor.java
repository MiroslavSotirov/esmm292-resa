package Task1; /******************************************************************************************************************
* File:SecurityMonitor.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2009 Carnegie Mellon University
* Versions:
*	1.0 March 2009 - Initial rewrite of original assignment 3 (ajl).
*
* Description:
*
* This class monitors the environmental control systems that control museum temperature and humidity. In addition to
* monitoring the temperature and humidity, the ECSMonitor also allows a user to set the humidity and temperature
* ranges to be maintained. If temperatures exceed those limits over/under alarm indicators are triggered.
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
*
******************************************************************************************************************/

import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

class SecurityMonitor extends Thread
{
	private MessageManagerInterface em = null;	// Interface object to the message manager
	private String MsgMgrIP = null;				// Message Manager IP address
    boolean SystemArmed = true;                // States if the System is armed
	boolean Registered = true;					// Signifies that this class is registered with an message manager.
	MessageWindow mw = null;					// This is the message window
	Indicator wi;								// WindowBreak indicator
	Indicator di;								// DoorBreak indicator
    Indicator mi;								// MotionDetection indicator

	public SecurityMonitor()
	{
		// message manager is on the local system

		try
		{
			// Here we create an message manager interface object. This assumes
			// that the message manager is on the local machine

			em = new MessageManagerInterface();

		}

		catch (Exception e)
		{
			System.out.println("SecurityMonitor::Error instantiating message manager interface: " + e);
			Registered = false;

		} // catch

	} //Constructor

	public SecurityMonitor(String MsgIpAddress)
	{
		// message manager is not on the local system

		MsgMgrIP = MsgIpAddress;

		try
		{
			// Here we create an message manager interface object. This assumes
			// that the message manager is NOT on the local machine

			em = new MessageManagerInterface( MsgMgrIP );
		}

		catch (Exception e)
		{
			System.out.println("SecurityMonitor::Error instantiating message manager interface: " + e);
			Registered = false;

		} // catch

	} // Constructor

	public void run()
	{
		Message Msg = null;				// Message object
		MessageQueue eq = null;			// Message Queue
		int MsgId = 0;					// User specified message ID
        boolean WindowBreak = false;    // States if there is a broken window
        boolean DoorBreak = false;      // States if there is a broken door
        boolean MotionDetection = false;// States if there is a motion detected
		int	Delay = 1000;				// The loop delay (1 second)
		boolean Done = false;			// Loop termination flag
		boolean ON = true;				// Used to turn on heaters, chillers, humidifiers, and dehumidifiers
		boolean OFF = false;			// Used to turn off heaters, chillers, humidifiers, and dehumidifiers

		if (em != null)
		{
			// Now we create the Security status and message panel
			// Note that we set up two indicators that are initially yellow. This is
			// because we do not know if the temperature/humidity is high/low.
			// This panel is placed in the upper left hand corner and the status
			// indicators are placed directly to the right, one on top of the other

			mw = new MessageWindow("Security Monitoring Console", 0, 0);
			wi = new Indicator ("Window", mw.GetX()+ mw.Width(), 0, 2);
			di = new Indicator ("Door", mw.GetX()+ mw.Width(), (int)(mw.Height()/3), 2 );
            mi = new Indicator ("Motion", mw.GetX()+ mw.Width(), (int)(mw.Height()/3)*2, 2 );

			mw.WriteMessage( "Registered with the message manager." );

	    	try
	    	{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );

			} // try

	    	catch (Exception e)
			{
				System.out.println("Error:: " + e);

			} // catch

			/********************************************************************
			** Here we start the main simulation loop
			*********************************************************************/

			while ( !Done )
			{
				// Here we get our message queue from the message manager

				try
				{
					eq = em.GetMessageQueue();

				} // try

				catch( Exception e )
				{
					mw.WriteMessage("Error getting message queue::" + e );

				} // catch

				// If there are messages in the queue, we read through them.
				// We are looking for MessageIDs = 6,7, or 8.
				// Message IDs of 3 are WindowBreaks
				// Message IDs of 4 are DoorBreaks
                // Message IDs of 5 are MotionDetections
				// Note that we get all the messages at once... there is a 1
				// second delay between samples,.. so the assumption is that there should
				// only be a message at most. If there are more, it is the last message
				// that will effect the status of the temperature and humidity controllers
				// as it would in reality.

				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();

                    if ( Msg.GetMessageId() == 6 ) // WindowBreak
                    {
                        try
                        {
                            WindowBreak = Boolean.valueOf(Msg.GetMessage()).booleanValue();

                        } // try

                        catch( Exception e )
                        {
                            mw.WriteMessage("Error reading WindowBreak: " + e);

                        } // catch

                    } // if

                    if ( Msg.GetMessageId() == 7 ) // DoorBreak
                    {
                        try
                        {
                            DoorBreak = Boolean.valueOf(Msg.GetMessage()).booleanValue();

                        } // try

                        catch( Exception e )
                        {
                            mw.WriteMessage("Error reading DoorBreak: " + e);

                        } // catch

                    } // if

                    if ( Msg.GetMessageId() == 8 ) // MotionDetection
                    {
                        try
                        {
                            MotionDetection = Boolean.valueOf(Msg.GetMessage()).booleanValue();

                        } // try

                        catch( Exception e )
                        {
                            mw.WriteMessage("Error reading WindowBreak: " + e);

                        } // catch

                    } // if


					// If the message ID == 99 then this is a signal that the simulation
					// is to end. At this point, the loop termination flag is set to
					// true and this process unregisters from the message manager.

					if ( Msg.GetMessageId() == 99 )
					{
						Done = true;

						try
						{
							em.UnRegister();

				    	} // try

				    	catch (Exception e)
				    	{
							mw.WriteMessage("Error unregistering: " + e);

				    	} // catch

				    	mw.WriteMessage( "\n\nSimulation Stopped. \n");

						// Get rid of the indicators. The message panel is left for the
						// user to exit so they can see the last message posted.

						wi.dispose();
						di.dispose();
                        mi.dispose();

					} // if

				} // for

                if(SystemArmed){

                    mw.WriteMessage("WindowBreak:: " + WindowBreak + "  DoorBreak:: " + DoorBreak + "  MotionDetection:: " + MotionDetection);

                    if (WindowBreak)
                    {
                        wi.SetLampColorAndMessage("ALARM", 3);
                    } else {
                        wi.SetLampColorAndMessage("---", 1);
                    } // if

                    if (DoorBreak)
                    {
                        di.SetLampColorAndMessage("ALARM", 3);
                    } else {
                        di.SetLampColorAndMessage("---", 1);
                    } // if

                    if (MotionDetection)
                    {
                        mi.SetLampColorAndMessage("ALARM", 3);
                    } else {
                        mi.SetLampColorAndMessage("---", 1);
                    } // if

                }else {
                    wi.SetLampColorAndMessage("NOT ARMED", 0);
                    di.SetLampColorAndMessage("NOT ARMED", 0);
                    mi.SetLampColorAndMessage("NOT ARMED", 0);
                }


				// This delay slows down the sample rate to Delay milliseconds

				try
				{
					Thread.sleep( Delay );
				} // try

				catch( Exception e )
				{
					System.out.println( "Sleep error:: " + e );

				} // catch

			} // while

		} else {

			System.out.println("Unable to register with the message manager.\n\n" );

		} // if

	} // main

	/***************************************************************************
	* CONCRETE METHOD:: IsRegistered
	* Purpose: This method returns the registered status
	*
	* Arguments: none
	*
	* Returns: boolean true if registered, false if not registered
	*
	* Exceptions: None
	*
	***************************************************************************/

	public boolean IsRegistered()
	{
		return( Registered );

	} // SetTemperatureRange

	/***************************************************************************
	* CONCRETE METHOD:: ArmSystem
	* Purpose: This method sets the SystemArmed status
	*
	* Arguments: boolean arm - True for Arming the System, False for Unarming the System
	*
	* Returns: none
	*
	* Exceptions: None
	*
	***************************************************************************/

	public void ArmSystem(boolean arm )
	{
		SystemArmed = arm;
		mw.WriteMessage( "***System Arm Status set to::" + SystemArmed +"***" );

	} // SetTemperatureRange

	/***************************************************************************
	* CONCRETE METHOD:: Halt
	* Purpose: This method posts an message that stops the security
	*		   system.
	*
	* Arguments: none
	*
	* Returns: none
	*
	* Exceptions: Posting to message manager exception
	*
	***************************************************************************/

	public void Halt()
	{
		mw.WriteMessage( "***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***" );

		// Here we create the stop message.

		Message msg;

		msg = new Message( (int) 99, "XXX" );

		// Here we send the message to the message manager.

		try
		{
			em.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println("Error sending halt message:: " + e);

		} // catch

	} // Halt

    public void simulateWindowBreak(boolean alarm){

        mw.WriteMessage( "***Trigger WindowBreak Simulation***" );

        // Here we create the stop message.

        Message msg;

        String cont;
        if(alarm){
            cont = "W1";
        }else{
            cont = "W0";
        }

        msg = new Message( (int) -6, cont );

        // Here we send the message to the message manager.

        try
        {
            em.SendMessage( msg );

        } // try

        catch (Exception e)
        {
            System.out.println("Error sending WindowBreak message:: " + e);

        } // catch


    }

} // ECSMonitor