package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

import java.io.IOException;

import com.builditboys.robots.time.InternalTimeSystem;
import com.builditboys.robots.time.LocalTimeSystem;
import com.builditboys.robots.utilities.FillableBuffer;

public class Sender extends AbstractSenderReceiver {

	private OutputChannelCollection outputChannels;
	
	private int sentSequenceNumber;
	private AbstractChannel sentChannel;
	private AbstractProtocol sentProtocol;
	private int sentChannelNumber;
	private int sentLength;
	private int sentCRC1;
	private int sentCRC2;
	private LinkMessage sentMessage;
	long sentTime;  // in internal time

	private FillableBuffer preambleBuffer;
	private FillableBuffer postambleBuffer;

	// --------------------------------------------------------------------------------
	// Constructor

	public Sender(AbstractLink link, LinkPortInterface port) {
		this.link = link;
		this.port = port;
		preambleBuffer = new FillableBuffer(SEND_PREAMBLE_LENGTH);
		postambleBuffer = new FillableBuffer(SEND_POSTAMBLE_LENGTH);
		crc8 = new CRC8Calculator();
		crc16 = new CRC16Calculator();
		outputChannels = link.getOutputChannels();
		resetMessageInfo();
	}

	// --------------------------------------------------------------------------------

	private void resetMessageInfo() {
		sentSequenceNumber = 0;
		sentChannel = null;
		sentProtocol = null;
		sentChannelNumber = 0;
		sentLength = 0;
		sentCRC1 = 0;
		sentCRC2 = 0;
		sentMessage = null;
		sentTime = 0;
	}

	// --------------------------------------------------------------------------------
	// Do some work, the top level, gets called in a loop
	
	public synchronized void doWork() throws InterruptedException, IOException {
		
		while (true) {
			resetMessageInfo();
			
			// try to get a message
			sentChannel = (OutputChannel) outputChannels.getChannelWithMessages();
			
			// if you got one, send it, otherwise wait
			if (sentChannel != null) {
				// ask the link if we should really send messages from this channel
				// if not, discard
				if (link.isSendableChannel(sentChannel)) {
					sentProtocol = sentChannel.getProtocol();
					LinkMessage message = sentChannel.getMessage();
					sendMessage(message);
				}
				else {
					System.out.println(link.getRole() + " discarding unsent message for channel " + sentChannel.getChannelNumber());
				}
			}
			else {
				// failed to find a message, wait and try again
				outputChannels.waitForMessage();
			}
		}
	}

	// --------------------------------------------------------------------------------
	// Send a message

	public void sendMessage(LinkMessage message) throws InterruptedException, IOException {
		resetMessageInfo();

		sentMessage = message;
		sentChannelNumber = message.getChannelNumber();
		sentLength = message.size();

		crc8.start();
		crc16.start();

		preambleBuffer.reset();
		postambleBuffer.reset();

		sendPreSync();
		sendPreamble();
		sendBody();
		sendPostamble();
		sendPostSync();

		sentTime = InternalTimeSystem.currentTime();

		synchronized (System.out){
			System.out.print(LocalTimeSystem.currentTime());
			System.out.print(" : " + link.getRole() + " Sent    : ");
			printRaw();
			System.out.println();
		}
		
		if (message.isSendNotify()) {
			message.doNotify();
		}
	}

	private void sendPreSync() throws InterruptedException, IOException {
		for (int i = 0; i < SEND_SYNC_1_LENGTH; i++) {
			port.writeByte(SEND_SYNC_BYTE_1);
		}
	}

	private void sendPreamble() throws InterruptedException, IOException {
		sentSequenceNumber = bestSequenceNumber();

		preambleBuffer.deConstructBytes1(sentSequenceNumber);
		preambleBuffer.deConstructBytes1(sentChannelNumber);
		preambleBuffer.deConstructBytes1(sentLength);

		crc8.extend(preambleBuffer);
		crc8.end();
		sentCRC1 = crc8.get();
		preambleBuffer.deConstructBytes1(sentCRC1);
		crc16.extend(preambleBuffer);

		sendBytes(preambleBuffer);

	}

	private void sendBody() throws InterruptedException, IOException {
		sendBytes(sentMessage);
		crc16.extend(sentMessage);
	}

	private void sendPostamble() throws InterruptedException, IOException {
		crc16.end();

		sentCRC2 = crc16.get();
		postambleBuffer.deConstructBytes2(sentCRC2);
		sendBytes(postambleBuffer);
	}

	private void sendPostSync() throws InterruptedException, IOException {
		for (int i = 0; i < SEND_POST_SYNC_PAD; i++) {
			port.writeByte(SEND_SYNC_BYTE_1);
		}
	}

	// --------------------------------------------------------------------------------
	// Byte escaping

	private void sendByte(byte bite) throws InterruptedException, IOException {
		switch (bite) {
		case SEND_SYNC_BYTE_1:
			port.writeByte(SEND_ESCAPE_BYTE);
			port.writeByte(SEND_INDICATE_SYNC_1);
			break;
		case SEND_ESCAPE_BYTE:
			port.writeByte(SEND_ESCAPE_BYTE);
			port.writeByte(SEND_INDICATE_ESCAPE);
			break;
		default:
			port.writeByte(bite);
			break;
		}
	}

	private void sendBytes(byte[] bytes) throws InterruptedException, IOException {
		for (byte b : bytes) {
			sendByte(b);
		}
	}

	private void sendBytes(byte[] bytes, int count) throws InterruptedException, IOException {
		for (int i = 0; i < count; i++) {
			sendByte(bytes[i]);
		}
	}

	private void sendBytes(FillableBuffer buff) throws InterruptedException, IOException {
		for (int i = 0; i < buff.size(); i++) {
			sendByte(buff.getByte(i));
		}
	}

	// --------------------------------------------------------------------------------

	private void printRaw() {
		System.out.print(sentSequenceNumber);
		System.out.print(" ");
		System.out.print(sentChannelNumber);
		System.out.print(" ");
		System.out.print(sentLength);
		System.out.print(" ");
		System.out.print(sentCRC1);
		System.out.print(" ");

		sentMessage.printBuffer();

		System.out.print(sentCRC2);
		System.out.print(" ");
	}

	// --------------------------------------------------------------------------------

}
