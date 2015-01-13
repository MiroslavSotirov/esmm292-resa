package Task1; /******************************************************************************************************************
* File:SecurityController.java
*
* Description:
*
* This class simulates a device sets the alarms. It polls the message manager for message
* ids = 9 and reacts to them by turning on or off the alarm. The following command are valid
* strings for controlling the humidifier and dehumidifier:
*
*	W1 = window alarm on
*	W0 = window alarm off
*	D1 = door alarm on
*	D0 = door alarm off
*   M1 = motion alarm off
*   M0 = motion alarm off
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
*
* Internal Methods:
*	static private void ConfirmMessage(MessageManagerInterface ei, String m )
*
******************************************************************************************************************/

import InstrumentationPackage.Indicator;
import InstrumentationPackage.MessageWindow;
import MessagePackage.Message;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

class SecurityController
{
	public static void main(String args[])
	{
		String MsgMgrIP;					// Message Manager IP address
		Message Msg = null;					// Message object
		MessageQueue eq = null;				// Message Queue
		int MsgId = 0;						// User specified message ID
		MessageManagerInterface em = null;	// Interface object to the message manager
		boolean DoorState = false;	        // DoorBreak state: false == off, true == on
		boolean WindowState = false;	    // WindowsBreak state: false == off, true == on
        boolean MotionState = false;	    // MotionDetection state: false == off, true == on
		int	Delay = 2500;					// The loop delay (2.5 seconds)
		boolean Done = false;				// Loop termination flag

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the message manager
		/////////////////////////////////////////////////////////////////////////////////

 		if ( args.length == 0 )
 		{
			// message manager is on the local system

			System.out.println("\n\nAttempting to register on the local machine..." );

			try
			{
				// Here we create an message manager interface object. This assumes
				// that the message manager is on the local machine

				em = new MessageManagerInterface();
			}

			catch (Exception e)
			{
				System.out.println("Error instantiating message manager interface: " + e);

			} // catch

		} else {

			// message manager is not on the local system

			MsgMgrIP = args[0];

			System.out.println("\n\nAttempting to register on the machine:: " + MsgMgrIP );

			try
			{
				// Here we create an message manager interface object. This assumes
				// that the message manager is NOT on the local machine

				em = new MessageManagerInterface( MsgMgrIP );
			}

			catch (Exception e)
			{
				System.out.println("Error instantiating message manager interface: " + e);

			} // catch

		} // if

		// Here we check to see if registration worked. If em is null then the
		// message manager interface was not properly created.

		if (em != null)
		{
			System.out.println("Registered with the message manager." );

			/* Now we create the humidity control status and message panel
			** We put this panel about 2/3s the way down the terminal, aligned to the left
			** of the terminal. The status indicators are placed directly under this panel
			*/

			float WinPosX = 0.0f; 	//This is the X position of the message window in terms
									//of a percentage of the screen height
			float WinPosY = 0.60f;	//This is the Y position of the message window in terms
								 	//of a percentage of the screen height

			MessageWindow mw = new MessageWindow("Security Controller Console", WinPosX, WinPosY);

			// Now we put the indicators directly under the humitity status and control panel

			Indicator wi = new Indicator ("Window Alarm OFF", mw.GetX(), mw.GetY()+mw.Height());
			Indicator di = new Indicator ("Door Alarm OFF", mw.GetX()+(wi.Width()*2), mw.GetY()+mw.Height());
            Indicator mi = new Indicator ("Motion Alarm OFF", mw.GetX()+(wi.Width()*3), mw.GetY()+mw.Height());

			mw.WriteMessage("Registered with the message manager." );

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
				try
				{
					eq = em.GetMessageQueue();

				} // try

				catch( Exception e )
				{
					mw.WriteMessage("Error getting message queue::" + e );

				} // catch

				// If there are messages in the queue, we read through them.
				// We are looking for MessageIDs = 4, this is a request to turn the
				// humidifier or dehumidifier on/off. Note that we get all the messages
				// at once... there is a 2.5 second delay between samples,.. so
				// the assumption is that there should only be a message at most.
				// If there are more, it is the last message that will effect the
				// output of the humidity as it would in reality.

				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();

					if ( Msg.GetMessageId() == 9 )
					{
						if (Msg.GetMessage().equalsIgnoreCase("W1"))
						{
							WindowState = true;
							mw.WriteMessage("Received WindowBreak on message" );

							// Confirm that the message was recieved and acted on

							ConfirmMessage( em, "W1" );

						} // if

						if (Msg.GetMessage().equalsIgnoreCase("W0"))
						{
							WindowState = false;
                            mw.WriteMessage("Received WindowBreak off message" );

							// Confirm that the message was recieved and acted on

							ConfirmMessage( em, "W0" );

						} // if

                        if (Msg.GetMessage().equalsIgnoreCase("D1"))
                        {
                            DoorState = true;
                            mw.WriteMessage("Received DoorBreak on message" );

                            // Confirm that the message was recieved and acted on

                            ConfirmMessage( em, "D1" );

                        } // if

                        if (Msg.GetMessage().equalsIgnoreCase("D0"))
                        {
                            DoorState = true;
                            mw.WriteMessage("Received DoorBreak off message" );

                            // Confirm that the message was recieved and acted on

                            ConfirmMessage( em, "D0" );

                        } // if

                        if (Msg.GetMessage().equalsIgnoreCase("M1"))
                        {
                            DoorState = true;
                            mw.WriteMessage("Received Motion Detection on message" );

                            // Confirm that the message was recieved and acted on

                            ConfirmMessage( em, "M1" );

                        } // if

                        if (Msg.GetMessage().equalsIgnoreCase("M0"))
                        {
                            DoorState = true;
                            mw.WriteMessage("Received Motion Detection off message" );

                            // Confirm that the message was recieved and acted on

                            ConfirmMessage( em, "M0" );

                        } // if

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

				// Update the lamp status

				if (WindowState)
				{

					wi.SetLampColorAndMessage("Window Break", 3);

				} else {

					wi.SetLampColorAndMessage("---", 1);

				} // if

                if (DoorState)
                {
                    di.SetLampColorAndMessage("Door Break", 3);

                } else {

                    di.SetLampColorAndMessage("---", 1);

                } // if

                if (MotionState)
                {
                    mi.SetLampColorAndMessage("Motion Detected", 3);

                } else {

                    mi.SetLampColorAndMessage("---", 1);

                } // if

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
	* CONCRETE METHOD:: ConfirmMessage
	* Purpose: This method posts the specified message to the specified message
	* manager. This method assumes an message ID of -9 which indicates a confirma-
	* tion of a command.
	*
	* Arguments: MessageManagerInterface ei - this is the messagemanger interface
	*			 where the message will be posted.
	*
	*			 string m - this is the received command.
	*
	* Returns: none
	*
	* Exceptions: None
	*
	***************************************************************************/

	static private void ConfirmMessage(MessageManagerInterface ei, String m )
	{
		// Here we create the message.

		Message msg = new Message( (int) -9, m );

		// Here we send the message to the message manager.

		try
		{
			ei.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println("Error Confirming Message:: " + e);

		} // catch

	} // PostMessage

} // HumidityControllers