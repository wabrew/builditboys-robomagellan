package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

public class LinkControlProtocol extends AbstractProtocol {

	public static final AbstractProtocol REPRESENTATIVE = new LinkControlProtocol();

	private static final int MY_CHANNEL_NUMBER = LINK_CONTROL_CHANNEL_NUMBER;

	// --------------------------------------------------------------------------------
	// Constructors

	private LinkControlProtocol() {
	}

	private LinkControlProtocol(ProtocolRoleEnum role) {
		protocolRole = role;
	}

	//--------------------------------------------------------------------------------
	// Channel factories
	
	public InputChannel getInputChannel () {
		channel = new InputChannel(this, MY_CHANNEL_NUMBER);
		return (InputChannel) channel;
	}
	
	public OutputChannel getOutputChannel () {
		channel = new OutputChannel(this, MY_CHANNEL_NUMBER);
		return (OutputChannel) channel;
	}
	
	// --------------------------------------------------------------------------------

	public static AbstractProtocol getRepresentative() {
		return REPRESENTATIVE;
	}
	
	public AbstractProtocol getInstanceRepresentative() {
		return REPRESENTATIVE;
	}

	// --------------------------------------------------------------------------------

	public static void addProtocolToLink (AbstractLink link, ProtocolRoleEnum rol) {
		LinkControlProtocol iproto = new LinkControlProtocol(rol);
		LinkControlProtocol oproto = new LinkControlProtocol(rol);
		link.addProtocol(iproto, oproto);
	}
	
	// --------------------------------------------------------------------------------
	// Link Control Messages - keep in sync with the PSoC
	
	public static final int LINK_CONTROL_MESSAGE_LENGTH = 1;
	
	public static final int MS_DO_PREPARE      = 0;
	public static final int MS_DO_PROCEED      = 1;

	public static final int SM_NEED_DO_PREPARE = 2;
	public static final int SM_DID_PREPARE     = 3;
	public static final int SM_DID_PROCEED     = 4;

	public static final int IM_ALIVE           = 5;

	public enum LinkControlMessageEnum {
		MASTER_DO_PREPARE(MS_DO_PREPARE),
		MASTER_DO_PROCEED(MS_DO_PROCEED),
		
		SLAVE_NEED_DO_PREPARE(SM_NEED_DO_PREPARE),
		SLAVE_DID_PREPARE(SM_DID_PREPARE),
		SLAVE_DID_PROCEED(SM_DID_PROCEED),
		
		MASTER_SLAVE_IM_ALIVE(IM_ALIVE);
		
		private int messageNum;
		
		private LinkControlMessageEnum (int num) {
			messageNum = num;
			associateInverse(messageNum, this);
		}
		
		private static void associateInverse (int num, LinkControlMessageEnum it) {
			NUM_TO_ENUM[num] = it;
		}
		
		private static final int LARGEST_NUM = IM_ALIVE;
		private static final LinkControlMessageEnum NUM_TO_ENUM[] = new LinkControlMessageEnum[LARGEST_NUM];

		// use this to get the mode number for an enum
		public int getMessageNum() {
			return messageNum;
		}

		// use this to map a mode number to its enum
		public static LinkControlMessageEnum numToEnum(int num) {
			if ((num > NUM_TO_ENUM.length) || (num < 0)) {
				throw new IndexOutOfBoundsException("num out of range");
			}
			return NUM_TO_ENUM[num];
		}
	}
	
	// --------------------------------------------------------------------------------
	// Sending messages - Master to Slave messages

	public void sendDoPrepare(boolean doWait) throws InterruptedException {
		if (protocolRole != ProtocolRoleEnum.MASTER) {
			throw new IllegalStateException();
		}	
		LinkMessage message = new LinkMessage(channelNumber, LINK_CONTROL_MESSAGE_LENGTH, doWait);
		message.addByte((byte) MS_DO_PREPARE);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	public void sendDoProceed(boolean doWait) throws InterruptedException {
		if (protocolRole != ProtocolRoleEnum.MASTER) {
			throw new IllegalStateException();
		}	
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
		if (protocolRole != ProtocolRoleEnum.SLAVE) {
			throw new IllegalStateException();
		}	
		LinkMessage message = new LinkMessage(channelNumber, doWait);
		message.addByte((byte) SM_NEED_DO_PREPARE);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	public void sendDidPrepare(boolean doWait) throws InterruptedException {
		if (protocolRole != ProtocolRoleEnum.SLAVE) {
			throw new IllegalStateException();
		}	
		LinkMessage message = new LinkMessage(channelNumber, doWait);
		message.addByte((byte) SM_DID_PREPARE);
		channel.addMessage(message);
		if (doWait) {
			message.doWait();
		}
	}

	public void sendDidProceed(boolean doWait) throws InterruptedException {
		if (protocolRole != ProtocolRoleEnum.SLAVE) {
			throw new IllegalStateException();
		}	
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
