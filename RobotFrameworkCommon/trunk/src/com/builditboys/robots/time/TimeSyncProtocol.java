package com.builditboys.robots.time;

/*
 Java is the slave, PSoC is the master
 
 Java side does
 
   InitializeLocalTime to get its local time setup.
   
   Sends a set clock to the PSoC (since it has only a 32 bit clock, we want it
   to be reset to correspond to now.
   
   Sends a correspond clock to PSoC to get its local time about the same as the Java
   side.
   
 PSoC side periodically sends clock sync requests to the Java side.  A clock sync
 has 4 local time values
 	time PSoC sent
 	time received on Java side
 	time sent back from the Java side
 	time received back on the PSoC side
 	
 From these numbers, it is possible to compute the skew between the two local times.
 The PSoC compensates for this skew and also tries to adjust its clock rate to keep the
 skew from changing.

*/

import static com.builditboys.robots.communication.LinkParameters.ROBOT_CONTROL_CHANNEL_NUMBER;
import static com.builditboys.robots.communication.LinkParameters.TIME_SYNC_CHANNEL_NUMBER;

import com.builditboys.robots.communication.AbstractLink;
import com.builditboys.robots.communication.AbstractProtocol;
import com.builditboys.robots.communication.AbstractProtocolMessage;
import com.builditboys.robots.communication.InputChannel;
import com.builditboys.robots.communication.LinkMessage;
import com.builditboys.robots.communication.OutputChannel;
import com.builditboys.robots.infrastructure.AbstractNotification;
import com.builditboys.robots.utilities.FillableBuffer;

public class TimeSyncProtocol extends AbstractProtocol {

	public static AbstractProtocol indicator = new TimeSyncProtocol();

	private static int myChannelNumber = TIME_SYNC_CHANNEL_NUMBER;

	// --------------------------------------------------------------------------------
	// Constructors

	private TimeSyncProtocol() {
	}

	public TimeSyncProtocol(ProtocolRoleEnum rol) {
		role = rol;
	}

	//--------------------------------------------------------------------------------
	// Channel factories
	
	public InputChannel getInputChannel () {
		channel = new InputChannel(this, myChannelNumber);
		return (InputChannel) channel;
	}
	
	public OutputChannel getOutputChannel () {
		channel = new OutputChannel(this, myChannelNumber);
		return (OutputChannel) channel;
	}
	
	// --------------------------------------------------------------------------------

	public AbstractProtocol getIndicator() {
		return indicator;
	}

	// --------------------------------------------------------------------------------

	public static void addProtocolToLink (AbstractLink link, ProtocolRoleEnum rol) {
		TimeSyncProtocol iproto = new TimeSyncProtocol(rol);
		TimeSyncProtocol oproto = new TimeSyncProtocol(rol);
		link.addProtocol(iproto, oproto);
	}
	
	// --------------------------------------------------------------------------------
	// Time Sync Messages - keep in sync with the PSoC

	public static final int MS_SET_CLOCK        = 0; // master forcing a slave clock value
	public static final int MS_CORRESPOND_CLOCK = 1; // make a correspondence between the masters local time and the slaves internal time
	
	public static final int SM_START_SYNC       = 2; // slave initiating a clock sync transaction
	public static final int MS_REPLY_SYNC       = 3; // master replying to a slave sync request
	
	// --------------------------------------------------------------------------------(non-Javadoc)
 	// Sending Messages -- Master to Slave
	
	public void sendSetClock (boolean doWait) throws InterruptedException {
		if (role != ProtocolRoleEnum.MASTER) {
			throw new IllegalStateException();
		}
		TimeSyncMessage mObject = new TimeSyncMessage(MS_SET_CLOCK,
													  LocalTimeSystem.currentLocalTime());
		sendMessage(mObject, doWait);
	}
	
	public void sendCorrespondClock (boolean doWait) throws InterruptedException {
		if (role != ProtocolRoleEnum.MASTER) {
			throw new IllegalStateException();
		}
		TimeSyncMessage mObject = new TimeSyncMessage(MS_CORRESPOND_CLOCK,
													  LocalTimeSystem.currentLocalTime());
		sendMessage(mObject, doWait);
	}

	public void sendReplySync (TimeSyncMessage receivedMessage, boolean doWait) throws InterruptedException {
		if (role != ProtocolRoleEnum.MASTER) {
			throw new IllegalStateException();
		}
		TimeSyncMessage mObject = new TimeSyncMessage(MS_REPLY_SYNC,
													  receivedMessage.time1,
													  receivedMessage.time2,
													  LocalTimeSystem.currentLocalTime(),
													  0);
		sendMessage(mObject, doWait);
	}

	// --------------------------------------------------------------------------------
	// Sending Messages -- Slave to Master
	
	public void sendStartSync (boolean doWait) throws InterruptedException {
		if (role != ProtocolRoleEnum.SLAVE) {
			throw new IllegalStateException();
		}
		TimeSyncMessage mObject = new TimeSyncMessage(SM_START_SYNC,
													  LocalTimeSystem.currentLocalTime());
		sendMessage(mObject, doWait);
		LinkMessage message = new LinkMessage(channelNumber, mObject.getLength(), doWait);
	}
	
	//--------------------------------------------------------------------------------
	// Receiving messages

	public void receiveMessage (LinkMessage message) throws InterruptedException {
		TimeSyncMessage mObject = new TimeSyncMessage();
		mObject.reConstruct(message);
		switch (role) {	
		case MASTER:
			receiveMasterMessage(mObject);
			break;		
		case SLAVE:
			receiveSlaveMessage(mObject);
			break;		
		default:
			throw new IllegalStateException();
		}
	}
	
	//--------------------------------------------------------------------------------
	// Receiving messages - Master

	public void receiveMasterMessage (TimeSyncMessage messageObject) throws InterruptedException {
		switch (messageObject.indicator) {
		
		// got a sync request, reply to it
		case SM_START_SYNC:
			messageObject.time2 = LocalTimeSystem.currentLocalTime();
			sendReplySync(messageObject, false);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown clock sync message: " + messageObject.indicator);
		}
	}
	
	//--------------------------------------------------------------------------------
	// Receiving messages - Slave

	public void receiveSlaveMessage (TimeSyncMessage messageObject) {
		switch (messageObject.indicator) { 
		
		// in Java, no need to set the clock since it is a long and has plenty of room
		case MS_SET_CLOCK:
			break;
		
		// setup up the correspondence between the masters local time and our local time
		case MS_CORRESPOND_CLOCK:
			LocalTimeSystem.correspondLocalTime(messageObject.time1);
			
		// slave side is not implemented in Java yet, only the PSOC acts as a slave	
		case MS_REPLY_SYNC:	
			throw new IllegalArgumentException("Unsupported clock sync message: " + messageObject.indicator);
			
		default:
			throw new IllegalArgumentException("Unknown clock sync message: " + messageObject.indicator);
		}
	}
		
	// --------------------------------------------------------------------------------
	// A class the models a time sync message
	
	private class TimeSyncMessage  extends AbstractProtocolMessage {
		int indicator = 0;
		int time1 = 0;
		int time2 = 0;
		int time3 = 0;
		int time4 = 0;
				
		TimeSyncMessage () {
			super(1 + 4*4);
		}

		TimeSyncMessage (int ind, int t1) {
			super(1 + 4*4);
			indicator = ind;
			time1 = t1;
		}
		
		TimeSyncMessage (int ind, int t1, int t2, int t3, int t4) {
			super(1 + 4*4);
			indicator = ind;
			time1 = t1;
			time2 = t2;
			time3 = t3;
			time4 = t4;
		}
		
		protected void deConstruct (FillableBuffer buff) {
			// make sure the time values are not going to be truncated
//			if (((time1 & 0xFFFF000L) | (time2 & 0xFFFF000L) | (time3 & 0xFFFF000L) | (time4 & 0xFFFF000L)) != 0L) {
//				throw new IllegalArgumentException();
//			}
			buff.deConstructBytes1(indicator);
			// times are truncated to 32 bits
			buff.deConstructBytes4((int) time1);
			buff.deConstructBytes4((int) time2);
			buff.deConstructBytes4((int) time3);
			buff.deConstructBytes4((int) time4);			
		}
		
		protected void reConstruct (FillableBuffer buff) {
			indicator = buff.reConstructBytes1();
			time1 = buff.reConstructBytes4();
			time2 = buff.reConstructBytes4();
			time3 = buff.reConstructBytes4();
			time4 = buff.reConstructBytes4();		
		}
	}

}
