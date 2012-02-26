package com.builditboys.robots.communication;

public class InputChannelCollection extends AbstractChannelCollection {

	
	// --------------------------------------------------------------------------------
	// Constructors

	public InputChannelCollection (AbstractLink link) {
		super(link);
	}

	// --------------------------------------------------------------------------------
	// Adding a channel
	
	public synchronized void addChannel (InputChannel channel) {
		super.addChannel(channel);
	}
	
}
