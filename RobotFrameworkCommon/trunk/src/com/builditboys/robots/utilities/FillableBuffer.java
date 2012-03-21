package com.builditboys.robots.utilities;

import java.lang.reflect.Field;
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

	public int size() {
		return fillIndex;
	}

	// --------------------------------------------------------------------------------
	// Get bytes

	public byte getByte(int index) {
		if (index > fillIndex) {
			throw new IndexOutOfBoundsException(getIndex + " >= " + fillIndex);
		}
		return buffer[index];
	}

	public byte peekByte() {
		if (getIndex > fillIndex) {
			throw new IndexOutOfBoundsException(getIndex + " >= " + fillIndex);
		}
		return buffer[getIndex];
	}

	public byte getByte() {
		if (getIndex > fillIndex) {
			throw new IndexOutOfBoundsException(getIndex + " >= " + fillIndex);
		}
		return buffer[getIndex++];
	}

	// --------------------------------------------------------------------------------
	// Add bytes

	public void addByte(byte bite) {
		if (fillIndex >= capacity) {
			throw new IndexOutOfBoundsException(fillIndex + " >= " + capacity);
		}
		buffer[fillIndex++] = bite;
	}

	// --------------------------------------------------------------------------------

	public void printBuffer() {
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
			if (hasNext()) {
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

	// --------------------------------------------------------------------------------
	// The serialization methods

	public void deConstructBytes1(int value) {
		addByte((byte) value);
	}

	public void deConstructBytes2(int value) {
		addByte((byte) value);
		value = value >>> 8;
		addByte((byte) value);
	}

	public void deConstructBytes4(int value) {
		addByte((byte) value);
		value = value >>> 8;
		addByte((byte) value);
		value = value >>> 8;
		addByte((byte) value);
		value = value >>> 8;
		addByte((byte) value);
	}

	public void deConstructBytesN(int value, int length) {
		switch (length) {
		case 1:
			deConstructBytes1(value);
			break;
		case 2:
			deConstructBytes2(value);
			break;
		case 4:
			deConstructBytes4(value);
			break;
		default:
			throw new IllegalArgumentException("Bad serialize length");
		}
	}

	// --------------------------------------------------------------------------------
	// The de-serialization methods

	public int reConstructBytes1() {
		int bite1 = getByte();
		int value;

		value = bite1;
		return value;
	}

	public int reConstructBytes2() {
		int bite1 = getByte() & 0x000000FF;
		int bite2 = getByte();
		int value;
		
		value = bite2;
		value <<= 8;
		value |= bite1;

		return value;
	}

	public int reConstructBytes4() {
		int bite1 = getByte() & 0x000000FF;
		int bite2 = getByte() & 0x000000FF;
		int bite3 = getByte() & 0x000000FF;
		int bite4 = getByte();
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

	public int reConstructBytesN(int length) {
		switch (length) {
		case 1:
			return reConstructBytes1();
		case 2:
			return reConstructBytes2();
		case 4:
			return reConstructBytes4();
		default:
			throw new IllegalArgumentException("Bad de-serialize length");
		}
	}

	// --------------------------------------------------------------------------------

	public void deConstructFields(Object theObject,
								  int sizes[],
								  String fields[]) throws IllegalArgumentException,
								 						  IllegalAccessException,
								 						  SecurityException,
								 						  NoSuchFieldException {
		if (sizes.length != fields.length) {
			throw new IllegalArgumentException("sizes and fields must match");
		}

		int length = sizes.length;
		Class<?> theClass = theObject.getClass();
		Field theField;
		int theValue;
		
		for (int i = 0; i < length; i++) {
			theField = theClass.getDeclaredField(fields[i]);
			theValue = getObjectIntegerField(theObject, theClass, theField);
			deConstructBytesN(theValue, sizes[i]);
		}
	}

	public void reConstructFields(Object theObject,
								  int sizes[],
								  String fields[]) throws SecurityException,
								   					  	  NoSuchFieldException,
								   						  IllegalArgumentException,
								   						  IllegalAccessException {
		if (sizes.length != fields.length) {
			throw new IllegalArgumentException("sizes and fields must match");
		}

		int length = sizes.length;
		Class<?> theClass = theObject.getClass();
		Field theField;
		int theValue;
		
		for (int i = 0; i < length; i++) {
			theField = theClass.getDeclaredField(fields[i]);
			theValue = reConstructBytesN(sizes[i]);
			setObjectIntegerField(theObject, theClass, theField, theValue);
		}
	}

	private static int getObjectIntegerField(Object theObject,
											 Class<?> objectClass,
											 Field field) throws IllegalArgumentException,
													  	         IllegalAccessException {
		Class<?> type = field.getType();		
		if (type.getName().equals("int")) {
			return field.getInt(theObject);
		} else if (type.getName().equals("short")) {
			return field.getShort(theObject);
		} else if (type.getName().equals("byte")) {
			return field.getByte(theObject);
		} else {
			throw new IllegalArgumentException("bad field type");
		}
	}

	private static void setObjectIntegerField(Object theObject,
											  Class<?> objectClass,
											  Field field,
											  int value) throws IllegalArgumentException,
											  				    IllegalAccessException {
		Class<?> type = field.getType();
		if (type.getName().equals("int")) {
			field.setInt(theObject, (int) value);
		} else if (type.getName().equals("short")) {
			field.setShort(theObject, (short) value);
		} else if (type.getName().equals("byte")) {
			field.setByte(theObject, (byte) value);
		} else {
			throw new IllegalArgumentException("bad field type");
		}
	}

	// --------------------------------------------------------------------------------
	
	public void deConstructElements (int elements[], int sizes[]) {
		if (elements.length < sizes.length) {
			throw new IllegalArgumentException("elements and sizes must match");
		}
		
		int length = sizes.length;
		for (int i = 0; i < length; i++) {
			deConstructBytesN(elements[i], sizes[i]);
		}	
	}
	
	public void reConstructElements (int elements[], int sizes[]) {
		if (elements.length < sizes.length) {
			throw new IllegalArgumentException("elements and sizes must match");
		}
		
		int length = sizes.length;
		for (int i = 0; i < length; i++) {
			elements[i] = reConstructBytesN(sizes[i]);
		}	
	}

}
