package com.builditboys.robots.communication;

import com.builditboys.robots.infrastructure.AbstractNotification;

public abstract class AbstractProtocol {
	
	public enum ProtocolRoleEnum {
		MASTER, SLAVE;
	};

	protected ProtocolRoleEnum role;
	
	protected AbstractChannel channel;
	protected int channelNumber;
	
	//--------------------------------------------------------------------------------
	// Constructors
	
	protected AbstractProtocol () {
	}

	protected AbstractProtocol (AbstractChannel chan) {
		channel = chan;
	}
	
	//--------------------------------------------------------------------------------
	// Channel factories - call one or the other, not both
	
	public abstract InputChannel getInputChannel ();
	
	public abstract OutputChannel getOutputChannel ();
	

	//--------------------------------------------------------------------------------

	public abstract AbstractProtocol getIndicator();
	
	public void setChannel (AbstractChannel chanl) {
		channel = chanl;
		channelNumber = channel.getChannelNumber();
	}

	//--------------------------------------------------------------------------------
	
	public void sendMessage (AbstractProtocolMessage mObject, boolean doWait) throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, mObject.getLength(), doWait);
		mObject.deConstruct(message);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	//--------------------------------------------------------------------------------
	// Receive a message, overload this if you wish
	
	//--------------------------------------------------------------------------------
	// Receiving messages
	
	public void receiveMessage (LinkMessage message) throws InterruptedException {
		channel.addMessage(message);
	}
		
	//--------------------------------------------------------------------------------
	// Sub classes should be able to create notifications from messages
	// on the input side
	
//	public abstract AbstractNotification xdeSerialize (LinkMessage message);

	//--------------------------------------------------------------------------------
	// Sub classes should be able to create messages from notifications
	// on the output side
	
//	public abstract LinkMessage xserialize (AbstractNotification notice);


}
