package com.builditboys.robots.communication;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;

public class WindowsLinkPort implements LinkPortInterface {
	
	WindowsCommPort commPort;
	
	// --------------------------------------------------------------------------------
	// Constructors
	
	public WindowsLinkPort (String portName, int baud, boolean bufferRead) throws NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException {
		commPort = new WindowsCommPort(portName, baud, bufferRead);
	}

	// --------------------------------------------------------------------------------

	public byte readByte () throws InterruptedException {
		return commPort.bufferedReadByte();
	}
	
	public void writeByte (byte bite) throws IOException {
		commPort.writeByte(bite);	
	}
	
	// --------------------------------------------------------------------------------

	public void open () throws IOException{
		try {
			commPort.open();
		} catch (NoSuchPortException e) {
			throw new IOException("No such port");
		} catch (PortInUseException e) {
			throw new IOException("Port in use");
		} catch (UnsupportedCommOperationException e) {
			throw new IOException("Unsupport port operation");
		}
	}
	
	public void close () throws IOException {
		commPort.close();
	}

	public boolean isOpen() {
		return commPort.isConnected();
	}
}
