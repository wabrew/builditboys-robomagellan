package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.CommParameters.*;

public class CommMessage extends FillableBuffer {
	
	// the channel to which messages are directed
	private int channelNumber;
	
	//--------------------------------------------------------------------------------
	// Getter/Setters
	
	public int getChannelNumber() {
		return channelNumber;
	}

	//--------------------------------------------------------------------------------
	// Constructors

	public CommMessage (int channelnum) {
		super(MAX_PAYLOAD_LEN);
		channelNumber = channelnum;
	}
	
	public CommMessage (int channelnum, int capacity) {
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
