package com.builditboys.robots.time;

import static com.builditboys.robots.communication.LinkParameters.*;

import com.builditboys.robots.communication.AbstractChannel;
import com.builditboys.robots.communication.AbstractLink;
import com.builditboys.robots.communication.AbstractProtocol;
import com.builditboys.robots.communication.InputChannel;
import com.builditboys.robots.communication.LinkControlProtocol;
import com.builditboys.robots.communication.LinkMessage;
import com.builditboys.robots.communication.OutputChannel;
import com.builditboys.robots.infrastructure.AbstractNotification;

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
	// Sending Messages -- Master to Slave
	
	
	// --------------------------------------------------------------------------------
	// Sending Messages -- Slave to Master
	
	
	//--------------------------------------------------------------------------------
	// Receiving messages
	
	public void receiveMessage (LinkMessage message) {
		// fill this in
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
	
	private class TimeSyncMessage {
		long time1;
		long time2;
		long time3;
		long time4;
		
		TimeSyncMessage (long t1) {
			time1 = t1;
		}
	}

}
