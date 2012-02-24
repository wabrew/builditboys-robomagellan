package com.builditboys.robots.communication;

public class OutputChannelCollection extends AbstractChannelCollection {

	
	// --------------------------------------------------------------------------------
	// Constructors

	public OutputChannelCollection (AbstractCommLink link) {
		super(link);
	}
	
	// --------------------------------------------------------------------------------
	// Adding a channel

	public synchronized void addChannel (OutputChannel channel) {
		super.addChannel(channel);
	}
	
}
