package com.builditboys.robots.communication;

import com.builditboys.robots.infrastructure.AbstractNotification;

public class OutputChannel extends AbstractChannel {
	
	//--------------------------------------------------------------------------------
	// Constructors

	public OutputChannel (AbstractProtocol protocol, int channelNum) {
		super(protocol, channelNum);
	}

}
