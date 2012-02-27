package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.SEND_ESCAPE_BYTE;
import static com.builditboys.robots.communication.LinkParameters.SEND_INDICATE_ESCAPE;
import static com.builditboys.robots.communication.LinkParameters.SEND_INDICATE_SYNC_1;
import static com.builditboys.robots.communication.LinkParameters.SEND_POSTAMBLE_LENGTH;
import static com.builditboys.robots.communication.LinkParameters.SEND_POST_SYNC_PAD;
import static com.builditboys.robots.communication.LinkParameters.SEND_PREAMBLE_LENGTH;
import static com.builditboys.robots.communication.LinkParameters.SEND_SYNC_1_LENGTH;
import static com.builditboys.robots.communication.LinkParameters.SEND_SYNC_BYTE_1;

import com.builditboys.robots.time.Clock;

public class Sender extends AbstractSenderReceiver {

	private OutputChannelCollection outputChannels;
	
	private int sentSequenceNumber;
	private AbstractChannel sentChannel;
	private AbstractProtocol sentProtocol;
	private int sentChannelNumber;
	private int sentLength;
	private byte sentCRC1;
	private short sentCRC2;
	private LinkMessage sentMessage;
	long sentTime;

	private SerializeBuffer preambleBuffer;
	private SerializeBuffer postambleBuffer;

	// --------------------------------------------------------------------------------
	// Constructor

	public Sender(AbstractLink link, LinkPortInterface port) {
		this.link = link;
		this.port = port;
		preambleBuffer = new SerializeBuffer(SEND_PREAMBLE_LENGTH);
		postambleBuffer = new SerializeBuffer(SEND_POSTAMBLE_LENGTH);
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
	
	public synchronized void doWork() throws InterruptedException {
		
		while (true) {
			resetMessageInfo();
			
			// try to get a message
			sentChannel = (OutputChannel) outputChannels.getChannelWithMessages();
			
			// if you got one, send it, otherwise wait
			if (sentChannel != null) {
				sentProtocol = sentChannel.getProtocol();
				sendMessage(sentChannel.getMessage());
			}
			else {
				// failed to find a message, wait and try again
				outputChannels.waitForMessage();
			}
		}
	}
	
/*	
// this is probably more complicated than it needs to be now that the
// threads are sent an interrupt when they need to be stopped

	public synchronized void doWork() throws InterruptedException {
		OutputChannel channel;
		
		// try to get and send a message
		channel = (OutputChannel) outputChannels.getChannelWithMessages();
		if (channel != null) {
			sendMessage(channel.getMessage());
			return;
		}
		
		// failed to find a message, wait and try again
		outputChannels.waitForMessage();
		
		// try again to get and send a message (could be sprurious awakening)
		channel = (OutputChannel) outputChannels.getChannelWithMessages();
		if (channel != null) {
			sendMessage(channel.getMessage());
			return;
		}
		
		// if you get here then there was no message to send (spurious awakening),
		// so return and try again when you are called again
	}
*/

	// --------------------------------------------------------------------------------
	// Send a message

	public void sendMessage(LinkMessage message) throws InterruptedException {
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

		sentTime = Clock.clockRead();

		synchronized (System.out){
			System.out.print(Clock.clockRead());
			System.out.print(" : " + link.getRole() + " Sent    : ");
			printRaw();
			System.out.println();
		}
		
		if (message.isSendNotify()) {
			message.doNotify();
		}
	}

	private void sendPreSync() throws InterruptedException {
		for (int i = 0; i < SEND_SYNC_1_LENGTH; i++) {
			port.writeByte(SEND_SYNC_BYTE_1);
		}
	}

	private void sendPreamble() throws InterruptedException {
		sentSequenceNumber = nextSequenceNumber();

		preambleBuffer.serializeBytes1(sentSequenceNumber);
		preambleBuffer.serializeBytes1(sentChannelNumber);
		preambleBuffer.serializeBytes1(sentLength);

		crc8.extend(preambleBuffer);
		crc8.end();
		sentCRC1 = crc8.get();
		preambleBuffer.serializeBytes1(sentCRC1);
		crc16.extend(preambleBuffer);

		sendBytes(preambleBuffer);

	}

	private void sendBody() throws InterruptedException {
		sendBytes(sentMessage);
		crc16.extend(sentMessage);
	}

	private void sendPostamble() throws InterruptedException {
		crc16.end();

		sentCRC2 = crc16.get();
		postambleBuffer.serializeBytes2(sentCRC2);
		sendBytes(postambleBuffer);
	}

	private void sendPostSync() throws InterruptedException {
		for (int i = 0; i < SEND_POST_SYNC_PAD; i++) {
			port.writeByte(SEND_SYNC_BYTE_1);
		}
	}

	// --------------------------------------------------------------------------------
	// Byte escaping

	private void sendByte(byte bite) throws InterruptedException {
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

	private void sendBytes(byte[] bytes) throws InterruptedException {
		for (byte b : bytes) {
			sendByte(b);
		}
	}

	private void sendBytes(byte[] bytes, int count) throws InterruptedException {
		for (int i = 0; i < count; i++) {
			sendByte(bytes[i]);
		}
	}

	private void sendBytes(FillableBuffer buff) throws InterruptedException {
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
