package com.builditboys.robots.time;

import static com.builditboys.robots.communication.LinkParameters.TIME_SYNC_CHANNEL_NUMBER;

import com.builditboys.robots.communication.AbstractLink;
import com.builditboys.robots.communication.AbstractProtocol;
import com.builditboys.robots.communication.InputChannel;
import com.builditboys.robots.communication.LinkMessage;
import com.builditboys.robots.communication.OutputChannel;
import com.builditboys.robots.infrastructure.AbstractNotification;
import com.builditboys.robots.utilities.FillableBuffer;

public class TimeSyncProtocol extends AbstractProtocol {

	public enum TimeSyncRoleEnum {
		MASTER, SLAVE;
	};

	private TimeSyncRoleEnum role;

	public static AbstractProtocol indicator = new TimeSyncProtocol();

	// --------------------------------------------------------------------------------
	// Constructors

	private TimeSyncProtocol() {
	}

	public TimeSyncProtocol(TimeSyncRoleEnum rol) {
		role = rol;
	}

	//--------------------------------------------------------------------------------
	// Channel factories
	
	public InputChannel getInputChannel () {
		channel = new InputChannel(this, TIME_SYNC_CHANNEL_NUMBER);
		return (InputChannel) channel;
	}
	
	public OutputChannel getOutputChannel () {
		channel = new OutputChannel(this, TIME_SYNC_CHANNEL_NUMBER);
		return (OutputChannel) channel;
	}
	
	// --------------------------------------------------------------------------------

	public AbstractProtocol getIndicator() {
		return indicator;
	}

	// --------------------------------------------------------------------------------

	public static void addProtocolToLink (AbstractLink link, TimeSyncRoleEnum rol) {
		TimeSyncProtocol iproto = new TimeSyncProtocol(rol);
		TimeSyncProtocol oproto = new TimeSyncProtocol(rol);
		link.addProtocol(iproto, oproto);
	}
	
	// --------------------------------------------------------------------------------
	// Time Sync Messages - keep in sync with the PSoC

	public static final int TIME_SYNC_MESSAGE_LENGTH = 1 + 4*4;
	
	public static final int MS_SET_CLOCK        = 0; // master forcing a slave clock value
	public static final int MS_CORRESPOND_CLOCK = 1; // make a correspondence between the masters local time and the slaves internal time
	
	public static final int SM_START_SYNC       = 2; // slave initiating a clock sync transaction
	public static final int MS_REPLY_SYNC       = 3; // master replying to a slave sync request
	
	// --------------------------------------------------------------------------------(non-Javadoc)
 	// Sending Messages -- Master to Slave
	
	public void sendSetClock (boolean doWait) throws InterruptedException {
		if (role != TimeSyncRoleEnum.MASTER) {
			throw new IllegalStateException();
		}
		LinkMessage message = new LinkMessage(channelNumber, TIME_SYNC_MESSAGE_LENGTH, doWait);
		TimeSyncMessage mObject = new TimeSyncMessage(MS_SET_CLOCK,
													  Time.getLocalTime());
		mObject.serialize(message);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}
	
	public void sendCorrespondClock (boolean doWait) throws InterruptedException {
		if (role != TimeSyncRoleEnum.MASTER) {
			throw new IllegalStateException();
		}
		LinkMessage message = new LinkMessage(channelNumber, TIME_SYNC_MESSAGE_LENGTH, doWait);
		TimeSyncMessage mObject = new TimeSyncMessage(MS_CORRESPOND_CLOCK,
													  Time.getLocalTime());
		mObject.serialize(message);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	public void sendReplySync (TimeSyncMessage receivedMessage, boolean doWait) throws InterruptedException {
		if (role != TimeSyncRoleEnum.MASTER) {
			throw new IllegalStateException();
		}
		LinkMessage message = new LinkMessage(channelNumber, TIME_SYNC_MESSAGE_LENGTH, doWait);
		TimeSyncMessage mObject = new TimeSyncMessage(MS_REPLY_SYNC,
													  receivedMessage.time1,
													  receivedMessage.time2,
													  Time.getLocalTime(),
													  0);
		mObject.serialize(message);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	// --------------------------------------------------------------------------------
	// Sending Messages -- Slave to Master
	
	public void sendStartSync (boolean doWait) throws InterruptedException {
		if (role != TimeSyncRoleEnum.SLAVE) {
			throw new IllegalStateException();
		}
		LinkMessage message = new LinkMessage(channelNumber, TIME_SYNC_MESSAGE_LENGTH, doWait);
		TimeSyncMessage mObject = new TimeSyncMessage(SM_START_SYNC,
													  Time.getLocalTime());
		mObject.serialize(message);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}
	
	//--------------------------------------------------------------------------------
	// Receiving messages

	public void receiveMessage (LinkMessage message) throws InterruptedException {
		TimeSyncMessage mObject = new TimeSyncMessage();
		mObject.deSerialize(message);
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
			messageObject.time2 = Time.getLocalTime();
			sendReplySync(messageObject, false);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown clock sync message: " + messageObject.indicator);
		}
	}
	
	//--------------------------------------------------------------------------------
	// Receiving messages - Slave

	public void receiveSlaveMessage (TimeSyncMessage messageObject) {
		switch (messageObject.indicator){ 
		
		// in Java, no need to set the clock since it is a long and has plenty of room
		case MS_SET_CLOCK:
			break;
		
		// setup up the correspondence between the masters local time and our local time
		case MS_CORRESPOND_CLOCK:
			Time.correspondLocalInternalTimes(messageObject.time1);
			
		// slave side is not implemented in Java yet, only the PSOC acts as a slave	
		case MS_REPLY_SYNC:	
			throw new IllegalArgumentException("Unsupported clock sync message: " + messageObject.indicator);
			
		default:
			throw new IllegalArgumentException("Unknown clock sync message: " + messageObject.indicator);
		}
	}
		
	// --------------------------------------------------------------------------------

	private class TimeSyncMessage {
		int indicator = 0;
		long time1 = 0;
		long time2 = 0;
		long time3 = 0;
		long time4 = 0;
		
		TimeSyncMessage () {
		}

		TimeSyncMessage (int ind, long t1) {
			indicator = ind;
			time1 = t1;
		}
		
		TimeSyncMessage (int ind, long t1, long t2, long t3, long t4) {
			indicator = ind;
			time1 = t1;
			time2 = t2;
			time3 = t3;
			time4 = t4;
		}

		
		void serialize (FillableBuffer buff) {
			// make sure the time values are not going to be truncated
			if (((time1 & 0xFFFF000L) | (time2 & 0xFFFF000L) | (time3 & 0xFFFF000L) | (time4 & 0xFFFF000L)) != 0L) {
				throw new IllegalArgumentException();
			}
			buff.serializeBytes1(indicator);
			// times are truncated to 32 bits
			buff.serializeBytes4((int) time1);
			buff.serializeBytes4((int) time2);
			buff.serializeBytes4((int) time3);
			buff.serializeBytes4((int) time4);			
		}
		
		void deSerialize (FillableBuffer buff) {
			indicator = buff.deSerializeBytes1();
			time1 = buff.deSerializeBytes4();
			time2 = buff.deSerializeBytes4();
			time3 = buff.deSerializeBytes4();
			time4 = buff.deSerializeBytes4();		
		}
	}
	
	// --------------------------------------------------------------------------------

	public AbstractNotification deSerialize(LinkMessage message) {
		// TODO Auto-generated method stub
		return null;
	}

	public LinkMessage serialize(AbstractNotification notice) {
		// TODO Auto-generated method stub
		return null;
	}



}
