package com.builditboys.robots.communication;

import com.builditboys.robots.infrastructure.AbstractNotification;

public abstract class AbstractProtocol {
	
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
	// Receive a message, overload this if you wish
	
	public void receiveMessage (LinkMessage message) {
		channel.addMessage(message);
	}
		
	//--------------------------------------------------------------------------------
	// Sub classes should be able to create notifications from messages
	// on the input side
	
	public abstract AbstractNotification deSerialize (LinkMessage message);

	//--------------------------------------------------------------------------------
	// Sub classes should be able to create messages from notifications
	// on the output side
	
	public abstract LinkMessage serialize (AbstractNotification notice);


}
