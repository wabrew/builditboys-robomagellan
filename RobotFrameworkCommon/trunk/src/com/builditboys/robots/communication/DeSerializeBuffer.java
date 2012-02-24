package com.builditboys.robots.communication;

public class DeSerializeBuffer extends FillableBuffer {

	// --------------------------------------------------------------------------------
	// Constructors
	
	DeSerializeBuffer(int capacity) {
		super(capacity);
	}

	// --------------------------------------------------------------------------------
	// The deserialization methods
	
	public byte deSerializeBytes1 () {
		byte bite1 = getByte();
		byte value;
		
		value = bite1;
		return value;
	}

	public short deSerializeBytes2 () {
		byte bite1 = getByte();
		byte bite2 = getByte();
		short value;
		
		value = bite2;
		value <<= 8;
		value |= bite1;
			
		return value;
	}

	public int deSerializeBytes4 () {
		byte bite1 = getByte();
		byte bite2 = getByte();
		byte bite3 = getByte();
		byte bite4 = getByte();
		int value;
		
		value = bite4;
		value <<= 8;
		value |= bite3;
		value <<= 8;
		value |= bite2;
		value <<= 8;
		value |= bite1;
			
		return value;
	}

}
