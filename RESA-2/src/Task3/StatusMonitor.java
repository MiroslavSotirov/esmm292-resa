package Task3;
/******************************************************************************************************************
 * File:StatusMonitor.java
 *
 * Description:
 *
 ******************************************************************************************************************/
import InstrumentationPackage.*;
import MessagePackage.*;

import java.util.*;

class StatusMonitor extends Thread
{
	private MessageManagerInterface em = null;	// Interface object to the message manager
	private String MsgMgrIP = null;				// Message Manager IP address
	boolean Registered = true;					// Signifies that this class is registered with an message manager.
	MessageWindow mw = null;					// This is the message window
	HashMap<Integer, Long> messages = new HashMap<Integer, Long>();
	private static final int TIMEOUT = 4000;	// Timeout at which point a sensor is considered defect

	public List<Integer> getDefectComponents(){
		ArrayList<Integer> defect = new ArrayList<Integer>();
		if (isSensorDefect(Constants.ID_TEMP_SENSOR)){
			defect.add(Constants.ID_TEMP_SENSOR);
		}
		if (isSensorDefect(Constants.ID_HUMIDITY_SENSOR)){
			defect.add(Constants.ID_HUMIDITY_SENSOR);
		}
		if (isSensorDefect(Constants.ID_DOORBREAK_SENSOR)){
			defect.add(Constants.ID_DOORBREAK_SENSOR);
		}
		if (isSensorDefect(Constants.ID_WINDOWBREAK_SENSOR)){
			defect.add(Constants.ID_WINDOWBREAK_SENSOR);
		}
		if (isSensorDefect(Constants.ID_MOTION_SENSOR)){
			defect.add(Constants.ID_MOTION_SENSOR);
		}
		if (isControllerDefect(Constants.ID_HUMIDITY_CONTROLLER)){
			defect.add(Constants.ID_HUMIDITY_CONTROLLER);
		}
		if (isControllerDefect(Constants.ID_TEMP_CONTROLLER)){
			defect.add(Constants.ID_TEMP_CONTROLLER);
		}
		
		// is named as controller, behaves like a sensor
		if (isSensorDefect(Constants.ID_FIRE_ALARM_CONTROLLER)){
			defect.add(Constants.ID_FIRE_ALARM_CONTROLLER);
		}
		if (isSensorDefect(Constants.ID_SMOKE_SENSOR)){
			defect.add(Constants.ID_SMOKE_SENSOR);
		}
		if (isControllerDefect(Constants.ID_SPRINKLER_CONTROLLER)){
			defect.add(Constants.ID_SPRINKLER_CONTROLLER);
		}
		return defect;
	}

	private boolean isSensorDefect(int id){
		Long l = messages.get(id);
		if (l == null)
			return true;
		return (System.currentTimeMillis() - l) > TIMEOUT;
	}
	
	private boolean isControllerDefect(int id){
		Long l = messages.get(id);
		// controllers are silent until used
		if (l == null)
			return false;
		// Answer code is always the negative value
		Long l2 = messages.get(-id);
		if (l2 == null)
			l2 = 0L;
		boolean wasReplied = (l <  l2);
		boolean inTimeout = (System.currentTimeMillis() - l) > TIMEOUT;
		return  !wasReplied && inTimeout;
	}

	public StatusMonitor()
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
			System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
			Registered = false;

		} // catch

	} //Constructor

	public StatusMonitor( String MsgIpAddress )
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
			System.out.println("ECSMonitor::Error instantiating message manager interface: " + e);
			Registered = false;

		} // catch

	} // Constructor

	public void run()
	{
		Message Msg = null;				// Message object
		MessageQueue eq = null;			// Message Queue
		int MsgId = 0;					// User specified message ID
		int	Delay = 100;				// The loop delay (0.1 second) to get a better reading when the message is sent
		boolean Done = false;			// Loop termination flag

		if (em != null)
		{
			// Now we create the ECS status and message panel
			// Note that we set up two indicators that are initially yellow. This is
			// because we do not know if the temperature/humidity is high/low.
			// This panel is placed in the upper left hand corner and the status
			// indicators are placed directly to the right, one on top of the other

			mw = new MessageWindow("ECS Status Console", 0, 0);
			//			ti = new Indicator ("TEMP UNK", mw.GetX()+ mw.Width(), 0);
			//			hi = new Indicator ("HUMI UNK", mw.GetX()+ mw.Width(), (int)(mw.Height()/2), 2 );

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


				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();
					MsgId = Msg.GetMessageId();

					// If the message ID == 99 then this is a signal that the simulation
					// is to end. At this point, the loop termination flag is set to
					// true and this process unregisters from the message manager.
					if ( MsgId == Constants.ID_SHUTDOWN )
					{
						Done = true;

						try
						{
							em.UnRegister();
							Registered = false;
						} // try

						catch (Exception e)
						{
							mw.WriteMessage("Error unregistering: " + e);

						} // catch

						mw.WriteMessage( "\n\nSimulation Stopped. \n");

						// Get rid of the indicators. The message panel is left for the
						// user to exit so they can see the last message posted.

						//						hi.dispose();
						//						ti.dispose();
					} // if
					else
					{
						// sensor value, take the last time
						messages.put(MsgId, System.currentTimeMillis());
						mw.WriteMessage("Received message with id " + MsgId);
					}



				} // for

				// Check temperature and effect control as necessary


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



} // StatusMonitor