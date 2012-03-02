package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

public class LinkMessage extends FillableBuffer {
	
	// the channel to which messages are directed
	private int channelNumber;
	
	private boolean sendNotify = false;
	private boolean doReset = false;
	
	//--------------------------------------------------------------------------------
	// Constructors

	public LinkMessage (int channelnum) {
		super(MAX_PAYLOAD_LEN);
		channelNumber = channelnum;
		sendNotify = false;
	}
	
	public LinkMessage (int channelnum, boolean notify) {
		super(MAX_PAYLOAD_LEN);
		channelNumber = channelnum;
		sendNotify = notify;
	}
	
	public LinkMessage (int channelnum, int capacity) {
		super(capacity);
		channelNumber = channelnum;
		sendNotify = false;
	}
	
	public LinkMessage (int channelnum, int capacity, boolean notify) {
		super(capacity);
		channelNumber = channelnum;
		sendNotify = notify;
	}

	//--------------------------------------------------------------------------------
	// Getter/Setters
	
	public int getChannelNumber() {
		return channelNumber;
	}

	public boolean isSendNotify() {
		return sendNotify;
	}

	public void setSendNotify(boolean doNotify) {
		this.sendNotify = doNotify;
	}
	
	public boolean isDoReset() {
		return doReset;
	}

	public void setDoReset(boolean doReset) {
		this.doReset = doReset;
	}

	//--------------------------------------------------------------------------------

	public synchronized void doWait () throws InterruptedException {
		wait();
	}

	public synchronized void doNotify () {
		notify();
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
