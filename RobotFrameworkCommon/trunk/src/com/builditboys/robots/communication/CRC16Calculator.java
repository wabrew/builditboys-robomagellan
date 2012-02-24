package com.builditboys.robots.communication;

public class CRC16Calculator extends AbstractCRCCalculator {
	
	private short CRC;
	
	//--------------------------------------------------------------------------------
	// Constructors
	
	public CRC16Calculator () {	
	}
	
	//--------------------------------------------------------------------------------
	// CRC building - do a start, some number of extends then an end
	// the value can then be extracted
	
	public void start () {
		CRC = 0;
	}
	
	public void extend (byte bite) {
		CRC++;
	}
	
	public void end () {
	
	}
		
	public short get () {
		return CRC;
	}
	
}
