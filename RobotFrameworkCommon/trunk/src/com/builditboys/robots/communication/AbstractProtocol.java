package com.builditboys.robots.communication;

public abstract class AbstractProtocol {
	
	public enum ProtocolRoleEnum {
		MASTER, SLAVE;
	};

	protected ProtocolRoleEnum protocolRole;
	
	protected AbstractChannel channel;
	protected int channelNumber;
	
	//--------------------------------------------------------------------------------
	// Constructors
	
	protected AbstractProtocol () {
	}

	protected AbstractProtocol (AbstractChannel chan) {
		channel = chan;
	}
	
	//--------------------------------------------------------------------------------
	// Channel factories - call one or the other, not both
	
	protected abstract InputChannel getInputChannel ();
	
	protected abstract OutputChannel getOutputChannel ();
	

	//--------------------------------------------------------------------------------

	protected abstract AbstractProtocol getIndicator();
	
	protected void setChannel (AbstractChannel chanl) {
		channel = chanl;
		channelNumber = channel.getChannelNumber();
	}

	//--------------------------------------------------------------------------------
	
	protected void sendRoleMessage (ProtocolRoleEnum role,
			  					  AbstractProtocolMessage messageObject,
			  					  boolean doWait) throws InterruptedException {
		if (role != protocolRole) {
			throw new IllegalStateException();
		}	
		sendMessage(messageObject, doWait);
	}
	
	protected void sendMessage (AbstractProtocolMessage mObject, boolean doWait) throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, mObject.getLength(), doWait);
		mObject.deConstruct(message);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	//--------------------------------------------------------------------------------
	// Receive a message, overload this if you wish
	
	//--------------------------------------------------------------------------------
	// Receiving messages
	
	protected void receiveMessage (LinkMessage message) throws InterruptedException {
		channel.addMessage(message);
	}
		
	//--------------------------------------------------------------------------------
	// Sub classes should be able to create notifications from messages
	// on the input side
	
//	public abstract AbstractNotification xdeSerialize (LinkMessage message);

	//--------------------------------------------------------------------------------
	// Sub classes should be able to create messages from notifications
	// on the output side
	
//	public abstract LinkMessage xserialize (AbstractNotification notice);


}
