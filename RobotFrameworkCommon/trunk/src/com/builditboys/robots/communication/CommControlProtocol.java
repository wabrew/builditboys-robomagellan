package com.builditboys.robots.communication;

import com.builditboys.robots.infrastructure.AbstractNotification;

public class CommControlProtocol extends AbstractProtocol {

	public enum CommControlRoleEnum { MASTER, SLAVE; };
	
	private CommControlRoleEnum role;

	public static AbstractProtocol indicator = new CommControlProtocol();
	
	// --------------------------------------------------------------------------------
	// Constructors
	
	private CommControlProtocol () {
		super(null);
	}
	
	public CommControlProtocol (AbstractChannel channel, CommControlRoleEnum role) {
		super(channel);
	}
	
	// --------------------------------------------------------------------------------

	public AbstractProtocol getIndicator() {
		return indicator;
	}

	//--------------------------------------------------------------------------------
	// Comm Control Messages - keep in sync with the PSoC
	
	public static final int MS_DO_PREPARE = 0;
	public static final int MS_DO_PROCEED = 1;

	public static final int SM_NEED_DO_PREPARE = 0;
	public static final int SM_DID_PREPARE = 0;
	public static final int SM_DID_PROCEED = 0;

	public static final int IM_ALIVE = 0;
	
	//--------------------------------------------------------------------------------
	// Master to Slave messages
	
	public void sendDoPrepare () {
		CommMessage message = new CommMessage(channelNumber);
		message.addByte((byte) MS_DO_PREPARE);
		channel.addMessage(message);
	}
	
	public void sendDoProceed () {
		CommMessage message = new CommMessage(channelNumber);
		message.addByte((byte) MS_DO_PROCEED);
		channel.addMessage(message);		
	}

	//--------------------------------------------------------------------------------
	// Master to Slave messages

	public void sendNeedDoPrepare () {
		CommMessage message = new CommMessage(channelNumber);
		message.addByte((byte) SM_NEED_DO_PREPARE);
		channel.addMessage(message);		
	}

	public void sendDidPrepare () {
		CommMessage message = new CommMessage(channelNumber);
		message.addByte((byte) SM_DID_PREPARE);
		channel.addMessage(message);		
	}

	public void sendDidProceed () {
		CommMessage message = new CommMessage(channelNumber);
		message.addByte((byte) SM_DID_PROCEED);
		channel.addMessage(message);		
	}

	//--------------------------------------------------------------------------------
	// Shared messages

	public void sendKeepAlive () {
		CommMessage message = new CommMessage(channelNumber);
		message.addByte((byte) IM_ALIVE);
		channel.addMessage(message);				
	}
	
	// --------------------------------------------------------------------------------
	
	public void receiveMessage (CommMessage message) {
		System.out.println("Dispatching comm control message");
		AbstractCommLink link = channel.getLink();
		link.receiveCommControlMessage(channel, message);
	}
	
	// --------------------------------------------------------------------------------

	public AbstractNotification deSerialize (CommMessage message) {
		return null;
	}
	
	public CommMessage serialize (AbstractNotification notice) {
		return null;	
	}
	


}
