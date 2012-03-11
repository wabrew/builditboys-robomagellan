package com.builditboys.robots.communication;

import com.builditboys.robots.utilities.FillableBuffer;

public abstract class AbstractCRCCalculator {

	//--------------------------------------------------------------------------------
	// CRC building - do a start, some number of extends then an end
	// the value can then be extracted with a type specific get

	//--------------------------------------------------------------------------------
	// Sub classes must define these
	
	public abstract void start ();
	public abstract void end ();
	public abstract void extend (byte bite);

	//--------------------------------------------------------------------------------
	// Common methods
	
	public void extend (int ibite) {
		extend((byte) ibite);
	}
	
	public void extend (short sbite) {
		extend((byte) sbite);
	}

	public void extend (byte[] bytes) {
		for (byte b: bytes) {
			extend(b);
		}
	}
	
	public void extend (byte[] bytes, int count) {
		for (int i = 0; i < count; i++) {
			extend(bytes[i]);
		}
	}
	
	public void extend (FillableBuffer buff) {
		for (int i = 0; i < buff.size(); i++) {
			extend(buff.getByte(i));
		}
	}

}
