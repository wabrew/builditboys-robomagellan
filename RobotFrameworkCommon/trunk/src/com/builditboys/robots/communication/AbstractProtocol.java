package com.builditboys.robots.communication;

import com.builditboys.robots.infrastructure.AbstractNotification;

public abstract class AbstractProtocol {
	
	protected AbstractChannel channel;
	protected int channelNumber;
	
	protected AbstractProtocol (AbstractChannel chan) {
		channel = chan;
	}
	
	//--------------------------------------------------------------------------------

	public abstract AbstractProtocol getIndicator();
	
	public void setChannel (AbstractChannel chanl) {
		channel = chanl;
		channelNumber = channel.getChannelNumber();
	}
	
	//--------------------------------------------------------------------------------
	// Receive a message, overload this if you wish
	
	public void receiveMessage (CommMessage message) {
		channel.addMessage(message);
	}
		
	//--------------------------------------------------------------------------------
	// Sub classes should be able to create notifications from messages
	// on the input side
	
	public abstract AbstractNotification deSerialize (CommMessage message);

	//--------------------------------------------------------------------------------
	// Sub classes should be able to create messages from notifications
	// on the output side
	
	public abstract CommMessage serialize (AbstractNotification notice);


}
