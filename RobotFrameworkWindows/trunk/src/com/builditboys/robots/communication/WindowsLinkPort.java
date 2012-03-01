package com.builditboys.robots.communication;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;

public class WindowsLinkPort implements LinkPortInterface {
	
	WindowsCommPort commPort;
	
	// --------------------------------------------------------------------------------
	// Constructors
	
	public WindowsLinkPort (String portName, int baud) throws NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException {
		commPort = new WindowsCommPort(portName, baud, true);
	}

	// --------------------------------------------------------------------------------

	public byte readByte () throws InterruptedException {
		return commPort.bufferedReadByte();
	}
	
	public void writeByte (byte bite) throws IOException {
		commPort.writeByte(bite);	
	}
	
	// --------------------------------------------------------------------------------

	public void close () throws IOException {
		commPort.close();
	}
}
