package com.builditboys.robots.communication;


import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import com.builditboys.robots.communication.LinkPortInterface;

public class DebuggingLinkPort implements LinkPortInterface {
	
	private boolean isOpen = false;

	ArrayBlockingQueue<Byte> readBuffer;
	ArrayBlockingQueue<Byte> writeBuffer;

	// --------------------------------------------------------------------------------

	public DebuggingLinkPort(ArrayBlockingQueue<Byte> rBuffer,
			ArrayBlockingQueue<Byte> wBuffer) {
		readBuffer = rBuffer;
		writeBuffer = wBuffer;
	}

	// --------------------------------------------------------------------------------

	public byte readByte() throws InterruptedException {
		return readBuffer.take();
	}

	public void writeByte(byte bite) throws InterruptedException {
		writeBuffer.put(bite);
	}
	
	// --------------------------------------------------------------------------------

	public void open() throws IOException {
		System.out.println("Openning debugging link port");
		isOpen = true;
	}
	
	public void close () {
		System.out.println("Closing debugging link port");
		isOpen = false;
	}

	public boolean isOpen() {
		return isOpen;
	}

}
