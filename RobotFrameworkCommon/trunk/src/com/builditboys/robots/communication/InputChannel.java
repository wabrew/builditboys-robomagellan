package com.builditboys.robots.communication;

import com.builditboys.robots.infrastructure.AbstractNotification;

public class InputChannel extends AbstractChannel {

	//--------------------------------------------------------------------------------
	// Constructors

	public InputChannel (AbstractProtocol protocol, int channelNumber) {
		super(protocol, channelNumber);
	}
		
}
