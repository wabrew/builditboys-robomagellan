package com.builditboys.robots.communication;

import java.util.concurrent.ArrayBlockingQueue;
import static com.builditboys.robots.communication.LinkParameters.*;

public abstract class AbstractChannel {
		
	// the channel number that is being buffered
	protected int channelNumber;
	
	protected AbstractProtocol protocol;
	
	protected AbstractChannel oppositeChannel;
	
	protected AbstractChannelCollection collection;
	
	// the message buffer, note blocking queue
	private ArrayBlockingQueue<LinkMessage> messagesQueue;
	
	//--------------------------------------------------------------------------------
	// Constructors

	public AbstractChannel (AbstractProtocol protocl, int channelNum) {
		channelNumber = channelNum;
		protocol = protocl;
		protocol.setChannel(this);
		messagesQueue = new ArrayBlockingQueue<LinkMessage>(DEFAULT_CHANNEL_BUFFER_CAPACITY);
	}
	
	public AbstractChannel (AbstractProtocol protocol, int channelNum, int capacity) {
		channelNumber = channelNum;
		this.protocol = protocol;
		messagesQueue = new ArrayBlockingQueue<LinkMessage>(capacity);
	}

	//--------------------------------------------------------------------------------
	// Getters/setters
	
	public int getChannelNumber () {
		return channelNumber;
	}
	
	public AbstractProtocol getProtocol () {
		return protocol;
	}
	
	public AbstractChannelCollection getCollection () {
		return collection;
	}
	
	public void setCollection (AbstractChannelCollection collec) {
		collection = collec;
	}

	public AbstractChannel getOppositeChannel() {
		return oppositeChannel;
	}

	public void setOppositeChannel(AbstractChannel oppositeChannel) {
		this.oppositeChannel = oppositeChannel;
	}

	//--------------------------------------------------------------------------------

	public static void pairChannels (AbstractChannel chan1, AbstractChannel chan2) {
		chan1.setOppositeChannel(chan2);
		chan2.setOppositeChannel(chan1);
	}
	
	//--------------------------------------------------------------------------------

	public AbstractLink getLink () {
		AbstractChannelCollection channelCollection = getCollection();
		AbstractLink link = channelCollection.getLink();
		return link;
	}

	
	//--------------------------------------------------------------------------------
	// Adding/Getting messages
	

	public void addMessage (LinkMessage message) {
		if (message.getChannelNumber() != channelNumber) {
			throw new IllegalArgumentException();	
		}
		messagesQueue.add(message);
		collection.notifyMessageAdded(this);
	}
	
	public LinkMessage getMessage () {
		return messagesQueue.remove();
	}
	
	//--------------------------------------------------------------------------------
	// Checking for messages
	
	public boolean hasMessages () {
		return !messagesQueue.isEmpty();
	}
	
	public boolean isEmpty () {
		return messagesQueue.isEmpty();
	}
	
	//--------------------------------------------------------------------------------

	static public boolean isLegalChannelNumber (int channelNumber) {
		return ((channelNumber >= CHANNEL_NUMBER_MIN) 
				&& (channelNumber <= CHANNEL_NUMBER_MAX));
	}

}
