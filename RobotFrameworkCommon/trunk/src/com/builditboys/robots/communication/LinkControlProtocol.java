package com.builditboys.robots.communication;

import com.builditboys.robots.infrastructure.AbstractNotification;

public class LinkControlProtocol extends AbstractProtocol {

	public enum CommControlRoleEnum {
		MASTER, SLAVE;
	};

	private CommControlRoleEnum role;

	public static AbstractProtocol indicator = new LinkControlProtocol();

	// --------------------------------------------------------------------------------
	// Constructors

	private LinkControlProtocol() {
		super(null);
	}

	public LinkControlProtocol(AbstractChannel channel, CommControlRoleEnum role) {
		super(channel);
	}

	// --------------------------------------------------------------------------------

	public AbstractProtocol getIndicator() {
		return indicator;
	}

	// --------------------------------------------------------------------------------
	// Link Control Messages - keep in sync with the PSoC

	public static final int MS_DO_PREPARE = 0;
	public static final int MS_DO_PROCEED = 1;

	public static final int SM_NEED_DO_PREPARE = 2;
	public static final int SM_DID_PREPARE = 3;
	public static final int SM_DID_PROCEED = 4;

	public static final int IM_ALIVE = 5;

	// --------------------------------------------------------------------------------
	// Master to Slave messages

	public void sendDoPrepare() throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, true);
		message.addByte((byte) MS_DO_PREPARE);
		channel.addMessage(message);
		message.doWait();
	}

	public void sendDoProceed() throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, true);
		message.addByte((byte) MS_DO_PROCEED);
		channel.addMessage(message);
		message.doWait();
	}

	// --------------------------------------------------------------------------------
	// Master to Slave messages

	public void sendNeedDoPrepare() throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, true);
		message.addByte((byte) SM_NEED_DO_PREPARE);
		channel.addMessage(message);
		message.doWait();
	}

	public void sendDidPrepare() throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, true);
		message.addByte((byte) SM_DID_PREPARE);
		channel.addMessage(message);
		message.doWait();
	}

	public void sendDidProceed() throws InterruptedException {
		LinkMessage message = new LinkMessage(channelNumber, true);
		message.addByte((byte) SM_DID_PROCEED);
		channel.addMessage(message);
		message.doWait();
	}

	// --------------------------------------------------------------------------------
	// Shared messages

	public void sendKeepAlive() {
		LinkMessage message = new LinkMessage(channelNumber);
		message.addByte((byte) IM_ALIVE);
		channel.addMessage(message);
	}

	// --------------------------------------------------------------------------------

	// this is on the receive side protocol object

	public void receiveMessage(LinkMessage message) {
		AbstractLink link = channel.getLink();
		byte indicator = message.getByte(0);
		switch (indicator) {
		case MS_DO_PREPARE:
			link.receivedDoPrepare(channel, message);
			break;
		case MS_DO_PROCEED:
			link.receivedDoProceed(channel, message);
			break;

		case SM_NEED_DO_PREPARE:
			link.receivedNeedDoPrepare(channel, message);
			break;
		case SM_DID_PREPARE:
			link.receivedDidPrepare(channel, message);
			break;
		case SM_DID_PROCEED:
			link.receivedDidProceed(channel, message);
			break;

		case IM_ALIVE:
			link.receivedImAlive(channel, message);
			break;

		default:
			throw new IllegalStateException();
		}
	}

	// --------------------------------------------------------------------------------

	public AbstractNotification deSerialize(LinkMessage message) {
		return null;
	}

	public LinkMessage serialize(AbstractNotification notice) {
		return null;
	}

}
