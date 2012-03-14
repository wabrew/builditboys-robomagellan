package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

public abstract class AbstractChannelCollection {
	
	// the link that this is part of
	protected AbstractLink link;
	
	// the channel buffers
	protected AbstractChannel channels[];
	
	// the highest channel number that has actually been added
	protected int highestChannelNumber = 0;
	
	//--------------------------------------------------------------------------------
	// Constructors

	public AbstractChannelCollection (AbstractLink link) {
		this.link = link;
		channels = new AbstractChannel[CHANNEL_NUMBER_MAX];
		highestChannelNumber = 0;
	}

	//--------------------------------------------------------------------------------
	// Getters and Setters
	
	public AbstractLink getLink() {
		return link;
	}

	//--------------------------------------------------------------------------------
	// Adding channels

	protected synchronized void addChannel (AbstractChannel channel) {
		int channelNum = channel.getChannelNumber();
		AbstractChannel existingChannel = channels[channelNum];
		if (existingChannel != null) {
			throw new IllegalArgumentException();
		}
		channels[channelNum] = channel;
		channel.setCollection(this);
		highestChannelNumber = Math.max(highestChannelNumber, channelNum);
	}
	
	//--------------------------------------------------------------------------------
	// Getting Channels
	
	public synchronized AbstractChannel getChannelByNumber (int channelNumber) {
		AbstractChannel channel = channels[channelNumber];
		return channel;
	}
	
	// will get the lowest numbered channel that handles the protocol
	// not too meaningful to have several channels handling the same protocol
	public synchronized AbstractChannel getChannelByProtocol (AbstractProtocol protocol) {
		AbstractChannel channel;
		for (int i = 0; i <= highestChannelNumber; i++) {
			channel = channels[i];
//			System.out.println(protocol);
//			System.out.println(channel);
//			System.out.println(channel.getProtocol());
//			System.out.println(channel.getProtocol().getIndicator());			
			if (channel != null) {
				if (channel.getProtocol().getInstanceIndicator() == protocol) {
					return channel;
				}
			}
		}
		return null;
	}
	
	public synchronized AbstractChannel getChannelWithMessages () {
		AbstractChannel channel;
		for (int i = 0; i <= highestChannelNumber; i++) {
			channel = channels[i];
			if (channel != null) {
				if (channel.hasMessages()) {
					return channel;
				}
			}
		}
		return null;
	}
	
	// --------------------------------------------------------------------------------
	// Coordinating moving messages through
	
	// called by the Sender when it needs a message
	public synchronized void waitForMessage () throws InterruptedException {
		wait();
	}
	
	// called by the Sender when it needs a message
	public synchronized void waitForMessage (long timeout) throws InterruptedException {
		wait(timeout);
	}

	
	// called by a channel when it adds a message
	public synchronized void notifyMessageAdded (AbstractChannel channel) {
		notify();
	}

	
}
