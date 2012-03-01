package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.RECEIVE_ESCAPE_BYTE;
import static com.builditboys.robots.communication.LinkParameters.RECEIVE_INDICATE_ESCAPE;
import static com.builditboys.robots.communication.LinkParameters.RECEIVE_INDICATE_SYNC_1;
import static com.builditboys.robots.communication.LinkParameters.RECEIVE_POSTAMBLE_LENGTH;
import static com.builditboys.robots.communication.LinkParameters.RECEIVE_PREAMBLE_LENGTH;
import static com.builditboys.robots.communication.LinkParameters.RECEIVE_SYNC_1_LENGTH;
import static com.builditboys.robots.communication.LinkParameters.RECEIVE_SYNC_BYTE_1;
import static com.builditboys.robots.communication.LinkParameters.SEND_POSTAMBLE_LENGTH;
import static com.builditboys.robots.communication.LinkParameters.SEND_PREAMBLE_LENGTH;

import com.builditboys.robots.time.Clock;

public class Receiver extends AbstractSenderReceiver {

	private InputChannelCollection inputChannels;

	private int receivedSequenceNumber;
	private int receivedChannelNumber;
	private int receivedLength;
	private byte receivedCRC1;
	private short receivedCRC2;
	private LinkMessage receivedMessage;
	private AbstractChannel receivedChannel;
	private AbstractProtocol receivedProtocol;
	private long receivedTime;
	private boolean receivedOk;

	private byte lastSyncByte;

	private DeSerializeBuffer preambleBuffer;
	private DeSerializeBuffer postambleBuffer;

	// --------------------------------------------------------------------------------
	// Constructor

	public Receiver(AbstractLink lnk, LinkPortInterface prt) {
		link = lnk;
		port = prt;
		preambleBuffer = new DeSerializeBuffer(SEND_PREAMBLE_LENGTH);
		postambleBuffer = new DeSerializeBuffer(SEND_POSTAMBLE_LENGTH);
		crc8 = new CRC8Calculator();
		crc16 = new CRC16Calculator();
		inputChannels = link.getInputChannels();
		resetMessageInfo();
	}

	// --------------------------------------------------------------------------------

	private void resetMessageInfo() {
		receivedSequenceNumber = 0;
		receivedChannelNumber = 0;
		receivedLength = 0;
		receivedCRC1 = 0;
		receivedCRC2 = 0;
		receivedMessage = null;
		receivedChannel = null;
		receivedProtocol = null;
		receivedOk = false;
		receivedTime = 0;
	}

	// --------------------------------------------------------------------------------
	// Do some work, the top level, gets called in a loop

	public synchronized void doWork() throws InterruptedException {
		while (true) {
			resetMessageInfo();
			receiveMessage();
			if (receivedOk) {
				receivedChannel = inputChannels.getChannelByNumber(receivedChannelNumber);
				if (receivedChannel != null) {
					receivedProtocol = receivedChannel.getProtocol();
				
					// ask the link if we are currently receiving from the channel
					// if not, discard
					if (link.isReceivableChannel(receivedChannel)) {
						handleReceivedMessage();
					}
					else {
						System.out.println(link.getRole() + " discarding received message for channel " + receivedChannelNumber);
					}
				}
				else {
					System.out.println(link.getRole() + " discarding received message for uninstalled channel " + receivedChannelNumber);
				}
			}
		}
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
			receivedOk = true;
		} catch (ReceiveException e) {
			handleReceiveException(e);
			receivedOk = false;
		}

		if (receivedOk) {
			receivedTime = Clock.clockRead();

			synchronized (System.out) {
				System.out.print(Clock.clockRead());
				System.out.print(" : " + link.getRole() + " Received: ");
				printRaw();
				System.out.println();
			}
		}
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

	private void receivePreamble() throws ReceiveException,
			InterruptedException {
		preambleBuffer.addByte(lastSyncByte);
		for (int i = 0; i < RECEIVE_PREAMBLE_LENGTH - 2; i++) {
			preambleBuffer.addByte(readEscapedByte());
		}

		int expectedSequenceNumber = bestSequenceNumber();
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
			System.out.println("Expected, Received");
			System.out.println(expectedSequenceNumber);
			System.out.println(receivedSequenceNumber);
			throw new ReceiveException("Bad received sequence number");
		}
		if (!AbstractChannel.isLegalChannelNumber(receivedChannelNumber)) {
			throw new ReceiveException("Bad received channel number");
		}
		if (!LinkMessage.islegalMessageLength(receivedLength)) {
			throw new ReceiveException("Bad received message length");
		}

		crc16.extend(preambleBuffer);
	}

	private void receiveBody() throws ReceiveException, InterruptedException {
		receivedMessage = new LinkMessage(receivedChannelNumber, receivedLength);

		for (int i = 0; i < receivedLength; i++) {
			receivedMessage.addByte(readEscapedByte());
		}
		crc16.extend(receivedMessage);
	}

	private void receivePostamble() throws ReceiveException,
			InterruptedException {
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

	private byte readEscapedByte() throws ReceiveException,
			InterruptedException {
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

	// --------------------------------------------------------------------------------

	private void handleReceivedMessage() {
		receivedProtocol.receiveMessage(receivedMessage);
	}

	private void handleReceiveException(ReceiveException e) {
		AbstractLink link = inputChannels.getLink();
		receivedTime = Clock.clockRead();
		link.receiveReceiverException(e);
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
