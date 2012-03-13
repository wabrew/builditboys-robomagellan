package com.builditboys.robots.utilities;

public class BitBuffer {
	
	private int buffer;
	private int size;
	
	private static final int bufferSize = Integer.SIZE;

	//--------------------------------------------------------------------------------

	public BitBuffer (int val) {
		setValue(val, Integer.SIZE);
	}
	
	public BitBuffer (int val, int sz) {
		setValue(val, sz);
	}
	
	
	public BitBuffer (short val) {
		setValue(val, Short.SIZE);
	}
	
	public BitBuffer (short val, int sz) {
		setValue(val, sz);
	}
	

	public BitBuffer (byte val) {
		setValue(val);
	}
	
	public BitBuffer (byte val, int sz) {
		setValue(val, sz);
	}

	//--------------------------------------------------------------------------------

	public void setValue (int val) {
		setValue(val, Integer.SIZE);
	}
	
	public void setValue (int val, int sz) {
		buffer = val;
		size = sz;
		validateSize(Integer.SIZE);
		clearExcess();
	}
	
	
	public void setValue (short val) {
		setValue(val, Short.SIZE);
	}

	public void setValue (short val, int sz) {
		buffer = val;
		size = sz;
		validateSize(Short.SIZE);
		clearExcess();
	}
	
	
	public void setValue (byte val) {
		setValue(val, Byte.SIZE);
	}

	public void setValue (byte val, int sz) {
		buffer = val;
		size = sz;
		validateSize(Byte.SIZE);
		clearExcess();
	}

	//--------------------------------------------------------------------------------

	public int intValue () {
		return (int) buffer;
	}
	
	public short shortValue () {
		return (short) buffer;
	}

	public byte byteValue () {
		return (byte) buffer;
	}

	//--------------------------------------------------------------------------------
	
	public void clearFromTo (int from, int to) {
		validateIndices(from, to);
		int mask = ~(1 << from);
		for (int i = from; i <= to; i++) {
			buffer &= mask;
			mask <<= 1;
		}
	}
	
	public void clearBit (int bit) {
		clearFromTo(bit, bit);
	}
	
	//--------------------------------------------------------------------------------

	public void setFromTo (int from, int to) {
		validateIndices(from, to);
		int mask = (1 << from);
		for (int i = from; i <= to; i++) {
			buffer |= mask;
			mask <<= 1;
		}
	}

	public void setBit (int bit) {
		setFromTo(bit, bit);
	}

	//--------------------------------------------------------------------------------

	public boolean testFromTo (int from, int to) {
		validateIndices(from, to);
		int mask = (1 << from);
		for (int i = from; i <= to; i++) {
			if ((buffer & mask) != 0) {
				return true;
			}
			mask <<= 1;
		}
		return false;
	}
	
	public boolean testBit (int bit) {
		return testFromTo(bit, bit);
	}
	
	//--------------------------------------------------------------------------------

	public int extractFromTo (int from, int to) {
		validateIndices(from, to);
		int mask = 1;
		int val = buffer;
		for (int i = 0; i < bufferSize; i++) {
			if ((i < from) || (i > to)) {
				buffer &= mask;
			}
			mask <<= 1;
		}
		return val >>> from;
	}
	
	// the bit version is done with testBit
	
	//--------------------------------------------------------------------------------

	public void stuffFromTo (int from, int to, int val, int width) {
		validateIndices(from, to);
		if (width > (to - from)) {
			throw new IllegalArgumentException("width too large" + "  " + width);
		}
		if (val >= ((1 << width) - 1)) {
			throw new IllegalArgumentException("value to big" + " " + val);
		}
		clearFromTo(from, to);
		int mask = val << from;
		buffer &= mask;
	}
	
	// the bit version is done with either bitSet or bitClear
	
	//--------------------------------------------------------------------------------

	public void compliment (int mask) {
		buffer = ~buffer;
	}

	public void and (int mask) {
		buffer &= mask;
	}
	
	public void or (int mask) {
		buffer |= mask;
		clearExcess();
	}
	
	public void xor (int mask) {
		buffer ^= mask;
		clearExcess();
	}
	
	public void rightShift (int count) {
		buffer >>>= count;
	}

	public void leftShift (int count) {
		buffer <<= count;
		clearExcess();
	}
	
	//--------------------------------------------------------------------------------

	private void clearExcess () {
		clearFromTo(size, bufferSize);
	}
	
	private void validateIndices (int from, int to) {
		validateIndex(from);
		validateIndex(to);
		if (to < from) {
			throw new IllegalArgumentException("to < from");
		}
	}
	private void validateIndex (int index) {
		if (index <= 0) {
			throw new IndexOutOfBoundsException("index out of range" + " " + index);
		}
		if (index > size) {
			throw new IndexOutOfBoundsException("index out of range" + " " + index);
		}
	}

	private void validateSize (int max) {
		if (size > max) {
			throw new IndexOutOfBoundsException("size too big" + " " + size);
		}
		if (size <= 0) {
			throw new IndexOutOfBoundsException("size too small" + " " + size);
		}
	}
	
	//--------------------------------------------------------------------------------

}
