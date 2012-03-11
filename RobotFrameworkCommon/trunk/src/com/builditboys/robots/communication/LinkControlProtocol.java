package com.builditboys.robots.communication;

import com.builditboys.robots.infrastructure.AbstractNotification;
import static com.builditboys.robots.communication.LinkParameters.*;

public class LinkControlProtocol extends AbstractProtocol {

	public static AbstractProtocol indicator = new LinkControlProtocol();

	private static int myChannelNumber = LINK_CONTROL_CHANNEL_NUMBER;

	// --------------------------------------------------------------------------------
	// Constructors

	private LinkControlProtocol() {
	}

	public LinkControlProtocol(ProtocolRoleEnum rol) {
		role = rol;
	}

	//--------------------------------------------------------------------------------
	// Channel factories
	
	public InputChannel getInputChannel () {
		channel = new InputChannel(this, myChannelNumber);
		return (InputChannel) channel;
	}
	
	public OutputChannel getOutputChannel () {
		channel = new OutputChannel(this, myChannelNumber);
		return (OutputChannel) channel;
	}
	
	// --------------------------------------------------------------------------------

	public AbstractProtocol getIndicator() {
		return indicator;
	}

	// --------------------------------------------------------------------------------
	// Link Control Messages - keep in sync with the PSoC
	
	public static final int COMM_CONTROL_MESSAGE_LENGTH = 1;
	
	public static final int MS_DO_PREPARE      = 0;
	public static final int MS_DO_PROCEED      = 1;

	public static final int SM_NEED_DO_PREPARE = 2;
	public static final int SM_DID_PREPARE     = 3;
	public static final int SM_DID_PROCEED     = 4;

	public static final int IM_ALIVE           = 5;

	// --------------------------------------------------------------------------------
	// Sending messages - Master to Slave messages

	public void sendDoPrepare(boolean doWait) throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, COMM_CONTROL_MESSAGE_LENGTH, doWait);
		message.addByte((byte) MS_DO_PREPARE);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	public void sendDoProceed(boolean doWait) throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, doWait);
		message.addByte((byte) MS_DO_PROCEED);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	// --------------------------------------------------------------------------------
	// Sending messages - Slave to Master messages

	public void sendNeedDoPrepare(boolean doWait) throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, doWait);
		message.addByte((byte) SM_NEED_DO_PREPARE);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	public void sendDidPrepare(boolean doWait) throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, doWait);
		message.addByte((byte) SM_DID_PREPARE);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	public void sendDidProceed(boolean doWait) throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, doWait);
		message.addByte((byte) SM_DID_PROCEED);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	// --------------------------------------------------------------------------------
	// Sending messages - Shared messages

	public void sendKeepAlive() {
		LinkMessage message = new LinkMessage(channelNumber);
		message.addByte((byte) IM_ALIVE);
		channel.addMessage(message);
	}

	// --------------------------------------------------------------------------------
	// Receiving messages
	
	public void receiveMessage(LinkMessage message) {
		AbstractLink link = channel.getLink();
		int indicator = message.getByte(0);
		switch (indicator) {
		// Slave side
		case MS_DO_PREPARE:
			link.receivedDoPrepare(channel, message);
			break;
		case MS_DO_PROCEED:
			link.receivedDoProceed(channel, message);
			break;

		// Master side
		case SM_NEED_DO_PREPARE:
			link.receivedNeedDoPrepare(channel, message);
			break;
		case SM_DID_PREPARE:
			link.receivedDidPrepare(channel, message);
			break;
		case SM_DID_PROCEED:
			link.receivedDidProceed(channel, message);
			break;

		// Common
		case IM_ALIVE:
			link.receivedImAlive(channel, message);
			break;

		default:
			throw new IllegalStateException();
		}
	}

}
