package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.CommParameters.*;

public abstract class AbstractChannelCollection {
	
	// the link that this is part of
	protected AbstractCommLink link;
	
	// the channel buffers
	protected AbstractChannel channels[];
	
	// the highest channel number that has actually been added
	protected int highestChannelNumber = 0;
	
	//--------------------------------------------------------------------------------
	// Constructors

	public AbstractChannelCollection (AbstractCommLink link) {
		this.link = link;
		channels = new AbstractChannel[CHANNEL_NUMBER_MAX];
		highestChannelNumber = 0;
	}

	//--------------------------------------------------------------------------------
	// Getters and Setters
	
	public AbstractCommLink getLink() {
		return link;
	}

	//--------------------------------------------------------------------------------
	// Adding channels

	protected void addChannel (AbstractChannel channel) {
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
	
	public AbstractChannel getChannelByNumber (int channelNumber) {
		AbstractChannel channel = channels[channelNumber];
		if (channel == null) {
			throw new IllegalArgumentException();
		}
		return channel;
	}
	
	// will get the lowest numbered channel that handles the protocol
	// not too meaningful to have several channels handling the same protocol
	public AbstractChannel getChannelByProtocol (AbstractProtocol protocol) {
		AbstractChannel channel;
		for (int i = 0; i <= highestChannelNumber; i++) {
			channel = channels[i];
//			System.out.println(protocol);
//			System.out.println(channel);
//			System.out.println(channel.getProtocol());
//			System.out.println(channel.getProtocol().getIndicator());			
			if (channel != null) {
				if (channel.getProtocol().getIndicator() == protocol) {
					return channel;
				}
			}
		}
		return null;
	}
	
	public AbstractChannel getChannelWithMessages () {
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
	public synchronized void waitForMessage (long timeout) throws InterruptedException {
		wait(timeout);
	}
	
	// called by a channel when it adds a message
	public synchronized void notifyMessageAdded (AbstractChannel channel) {
		notify();
	}

	
}
