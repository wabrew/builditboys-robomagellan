package com.builditboys.robots.communication;

public class CRC8Calculator extends AbstractCRCCalculator {

	private byte CRC;
	
	//--------------------------------------------------------------------------------
	// Constructors
	
	public CRC8Calculator () {	
	}
	
	//--------------------------------------------------------------------------------
	// CRC building
	
	public void start () {
		CRC = 0;
	}
	
	public void extend (byte bite) {
		CRC++;
	}

	public void end () {
	
	}
	
	public byte get () {
		return CRC;
	}
	
}
