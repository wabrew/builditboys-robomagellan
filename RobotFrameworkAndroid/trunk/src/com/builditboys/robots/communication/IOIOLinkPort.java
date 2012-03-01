package com.builditboys.robots.communication;

public class IOIOLinkPort implements LinkPortInterface {
	
	
	// --------------------------------------------------------------------------------
	// Constructors
	
	// --------------------------------------------------------------------------------

	public byte readByte () {
		System.out.println("IOIOLinkPort readByte not implemented");
		throw new IllegalStateException();
	}
		
	public void writeByte (byte bite) {
		System.out.println("IOIOLinkPort writeByte not implemented");
		throw new IllegalStateException();
	}

	// --------------------------------------------------------------------------------

	public void close () {
		System.out.println("IOIOLinkPort close not implemented");
		throw new IllegalStateException();
	}

}
