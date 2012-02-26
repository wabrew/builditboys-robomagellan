package com.builditboys.robots.communication;

public class OutputChannelCollection extends AbstractChannelCollection {

	
	// --------------------------------------------------------------------------------
	// Constructors

	public OutputChannelCollection (AbstractLink link) {
		super(link);
	}
	
	// --------------------------------------------------------------------------------
	// Adding a channel

	public synchronized void addChannel (OutputChannel channel) {
		super.addChannel(channel);
	}
	
}
