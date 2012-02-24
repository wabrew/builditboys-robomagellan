package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.CommParameters.RECEIVE_ESCAPE_BYTE;
import static com.builditboys.robots.communication.CommParameters.RECEIVE_INDICATE_ESCAPE;
import static com.builditboys.robots.communication.CommParameters.RECEIVE_INDICATE_SYNC_1;
import static com.builditboys.robots.communication.CommParameters.RECEIVE_POSTAMBLE_LENGTH;
import static com.builditboys.robots.communication.CommParameters.RECEIVE_PREAMBLE_LENGTH;
import static com.builditboys.robots.communication.CommParameters.RECEIVE_SYNC_1_LENGTH;
import static com.builditboys.robots.communication.CommParameters.RECEIVE_SYNC_BYTE_1;
import static com.builditboys.robots.communication.CommParameters.SEND_POSTAMBLE_LENGTH;
import static com.builditboys.robots.communication.CommParameters.SEND_PREAMBLE_LENGTH;

import com.builditboys.robots.time.Clock;

public class Receiver extends AbstractSenderReceiver {

	private InputChannelCollection inputChannels;

	private int receivedSequenceNumber;
	private int receivedChannelNumber;
	private int receivedLength;
	private byte receivedCRC1;
	private short receivedCRC2;
	private CommMessage receivedMessage;
	private long receivedTime;

	private byte lastSyncByte;

	private DeSerializeBuffer preambleBuffer;
	private DeSerializeBuffer postambleBuffer;

	// --------------------------------------------------------------------------------
	// Constructor

	public Receiver(AbstractCommLink lnk, CommPortInterface prt) {
		link = lnk;
		port = prt;
		resetSequenceNumber();
		preambleBuffer = new DeSerializeBuffer(SEND_PREAMBLE_LENGTH);
		postambleBuffer = new DeSerializeBuffer(SEND_POSTAMBLE_LENGTH);
		crc8 = new CRC8Calculator();
		crc16 = new CRC16Calculator();
		inputChannels = link.getInputChannels();
	}

	// --------------------------------------------------------------------------------

	public void reset() {
		resetSequenceNumber();
		resetMessageInfo();
	}

	private void resetMessageInfo() {
		receivedSequenceNumber = 0;
		receivedChannelNumber = 0;
		receivedLength = 0;
		receivedCRC1 = 0;
		receivedCRC2 = 0;
		receivedMessage = null;
		long receivedTime = 0;
	}

	// --------------------------------------------------------------------------------
	// Do some work, the top level, gets called in a loop
	
	public synchronized void doWork () throws InterruptedException {
		receiveMessage();
		AbstractChannel channel = inputChannels.getChannelByNumber(receivedChannelNumber);
		AbstractProtocol protocol = channel.getProtocol();
		protocol.receiveMessage(receivedMessage);
	}

	// --------------------------------------------------------------------------------
	// Receive a message

	public void receiveMessage() throws InterruptedException {
		resetMessageInfo();

		crc8.start();
		crc16.start();

		preambleBuffer.reset();
		postambleBuffer.reset();

		try {
			receivePreSync();
			receivePreamble();
			receiveBody();
			receivePostamble();
		} catch (ReceiveException e) {
			handleReceiveException(e);
		}
		receivedTime = Clock.clockRead();

		System.out.print("Received: ");
		printRaw();
		System.out.println();
	}

	private void receivePreSync() throws InterruptedException {
		boolean synced = false;
		int sync1Count = 0;
		byte bite;

		while (!synced) {
			bite = port.readByte();
			if (bite == RECEIVE_SYNC_BYTE_1) {
				// saw another sync, just increment the count
				sync1Count++;
			} else {
				// saw something else
				if (sync1Count >= RECEIVE_SYNC_1_LENGTH) {
					// we have enough
					synced = true;
					lastSyncByte = bite;
				}
			}
		}
	}

	private void receivePreamble() throws ReceiveException, InterruptedException {
		preambleBuffer.addByte(lastSyncByte);
		for (int i = 0; i < RECEIVE_PREAMBLE_LENGTH - 2; i++) {
			preambleBuffer.addByte(readEscapedByte());
		}

		int expectedSequenceNumber = nextSequenceNumber();
		receivedSequenceNumber = preambleBuffer.deSerializeBytes1();
		receivedChannelNumber = preambleBuffer.deSerializeBytes1();
		receivedLength = preambleBuffer.deSerializeBytes1();

		crc8.extend(preambleBuffer);

		preambleBuffer.addByte(readEscapedByte());
		receivedCRC1 = preambleBuffer.deSerializeBytes1();
		crc8.end();

		if (receivedCRC1 != crc8.get()) {
			throw new ReceiveException("Preamble CRC mismatch");
		}
		if (receivedSequenceNumber != expectedSequenceNumber) {
			throw new ReceiveException("Bad received sequence number");
		}
		if (!AbstractChannel.isLegalChannelNumber(receivedChannelNumber)) {
			throw new ReceiveException("Bad received channel number");
		}
		if (!CommMessage.islegalMessageLength(receivedLength)) {
			throw new ReceiveException("Bad received message length");
		}

		crc16.extend(preambleBuffer);
	}

	private void receiveBody() throws ReceiveException, InterruptedException {
		receivedMessage = new CommMessage(receivedChannelNumber, receivedLength);

		for (int i = 0; i < receivedLength; i++) {
			receivedMessage.addByte(readEscapedByte());
		}
		crc16.extend(receivedMessage);
	}

	private void receivePostamble() throws ReceiveException, InterruptedException {
		for (int i = 0; i < RECEIVE_POSTAMBLE_LENGTH; i++) {
			postambleBuffer.addByte(readEscapedByte());
		}

		receivedCRC2 = postambleBuffer.deSerializeBytes2();

		crc16.end();
		if (receivedCRC2 != crc16.get()) {
			throw new ReceiveException("Preamble CRC mismatch");
		}
	}

	// --------------------------------------------------------------------------------
	// Classifying bytes - need to detect byte escapes

	private byte readEscapedByte() throws ReceiveException, InterruptedException {
		byte bite = port.readByte();

		switch (bite) {
		case RECEIVE_SYNC_BYTE_1:
			throw new ReceiveException("Unescaped sync byte");
		case RECEIVE_ESCAPE_BYTE:
			bite = port.readByte();
			switch (bite) {
			case RECEIVE_INDICATE_SYNC_1:
				return RECEIVE_SYNC_BYTE_1;
			case RECEIVE_INDICATE_ESCAPE:
				return RECEIVE_ESCAPE_BYTE;
			default:
				throw new ReceiveException("Unknown escaped byte");
			}
		default:
			return bite;
		}
	}

	// --------------------------------------------------------------------------------

	private static class ReceiveException extends Exception {
		private String reason;

		ReceiveException(String why) {
			reason = why;
		}
	}
	
	private void handleReceiveException (ReceiveException e) {
		AbstractCommLink link = inputChannels.getLink();
		link.receiveReceiveException(e);
	}
	
	// --------------------------------------------------------------------------------

	private void printRaw() {
		System.out.print(receivedSequenceNumber);
		System.out.print(" ");
		System.out.print(receivedChannelNumber);
		System.out.print(" ");
		System.out.print(receivedLength);
		System.out.print(" ");
		System.out.print(receivedCRC1);
		System.out.print(" ");

		receivedMessage.printBuffer();

		System.out.print(receivedCRC2);
		System.out.print(" ");
	}


}
