package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

public class LinkMessage extends FillableBuffer {
	
	// the channel to which messages are directed
	private int channelNumber;
	
	//--------------------------------------------------------------------------------
	// Getter/Setters
	
	public int getChannelNumber() {
		return channelNumber;
	}

	//--------------------------------------------------------------------------------
	// Constructors

	public LinkMessage (int channelnum) {
		super(MAX_PAYLOAD_LEN);
		channelNumber = channelnum;
	}
	
	public LinkMessage (int channelnum, int capacity) {
		super(capacity);
		channelNumber = channelnum;
	}
	
	//--------------------------------------------------------------------------------

	static public boolean islegalMessageLength (int length) {
		return length <= MAX_PAYLOAD_LEN;
	}
	
	//--------------------------------------------------------------------------------

	public void print () {
		System.out.print("Channel: ");
		System.out.print(channelNumber);
		printBuffer();
	}
	
}
