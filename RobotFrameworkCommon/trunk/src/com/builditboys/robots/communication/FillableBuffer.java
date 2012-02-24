package com.builditboys.robots.communication;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class FillableBuffer implements Iterable<Byte> {

	protected byte[] buffer;
	protected int capacity;
	protected int fillIndex;
	protected int getIndex;

	// --------------------------------------------------------------------------------
	// Constructors
	
	public FillableBuffer(int capacity) {
		buffer = new byte[capacity];
		this.capacity = capacity;
		fillIndex = 0;
		getIndex = 0;
	}

	// --------------------------------------------------------------------------------

	public void reset() {
		fillIndex = 0;
		getIndex = 0;
	}
	
	// --------------------------------------------------------------------------------

	public int size () {
		return fillIndex;
	}

	// --------------------------------------------------------------------------------
	// Get bytes
	
	public byte getByte (int index) {
		if (index >= fillIndex) {
			throw new IndexOutOfBoundsException();
		}
		return buffer[index];
	}
	
	public byte getByte () {
		if (getIndex >= fillIndex) {
			throw new IndexOutOfBoundsException();	
		}
		return buffer[getIndex++];
	}
	
	// --------------------------------------------------------------------------------
	// Add bytes
	
	public void addByte(byte bite) {
		if (fillIndex >= capacity) {
			throw new IndexOutOfBoundsException();
		}
		buffer[fillIndex++] = bite;
	}
	
	// --------------------------------------------------------------------------------

	public void printBuffer () {
		for (int i = getIndex; i < fillIndex; i++) {
			System.out.printf("%02x ", buffer[i]);
		}
	}
	
	// --------------------------------------------------------------------------------

	private class FillableBufferIterator implements Iterator<Byte> {
		int i;

		public boolean hasNext() {
			if (i < fillIndex)
				return true;
			return false;
		}

		public Byte next() {
			if (hasNext()){
				i++;
				return buffer[i];
			}
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"FillableBufferIterator does not support remove");
		}

	}

	public Iterator<Byte> iterator() {
		return new FillableBufferIterator();
	}

}
