package com.builditboys.robots.communication;

public class SerializeBuffer extends FillableBuffer {

	// --------------------------------------------------------------------------------
	// Constructors

	SerializeBuffer (int capacity) {
		super(capacity);
	}

	// --------------------------------------------------------------------------------
	// The serialization methods
	
	public void serializeBytes1 (int value) {
		addByte((byte) value);
	}

	public void serializeBytes2 (int value) {
		addByte((byte) value);
		value = value >>> 8;
		addByte((byte) value);
	}

	public void serializeBytes4 (int value) {
		addByte((byte) value);
		value = value >>> 8;
		addByte((byte) value);
		value = value >>> 8;
		addByte((byte) value);
		value = value >>> 8;
		addByte((byte) value);
	}
	
}
