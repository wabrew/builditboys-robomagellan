package com.builditboys.robots.communication;

import java.io.IOException;

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

	public void open() throws IOException {
		System.out.println("IOIOLinkPort open not implemented");
		throw new IllegalStateException();
	}
	
	public void close () {
		System.out.println("IOIOLinkPort close not implemented");
		throw new IllegalStateException();
	}

	
	public boolean isOpen() {
		System.out.println("IOIOLinkPort isOpen not implemented");
		throw new IllegalStateException();
	}

}


