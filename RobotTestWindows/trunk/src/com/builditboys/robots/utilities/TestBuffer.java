package com.builditboys.robots.utilities;

import com.builditboys.robots.utilities.FillableBuffer;

public class TestBuffer {
	
	static final int SIZES[] = {1, 2, 4};
	static final String FIELDS[] = {"b", "s", "i"};

	public static void main (String args[]) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		
		SimpleMessage messageObj = new SimpleMessage();
		int elements[] = {1, 2, 3};
		
		// fill a buffer from the default values
		System.out.println("Filling buffer from object");
		FillableBuffer buffer1 = new FillableBuffer(100);
		buffer1.deConstructFields(messageObj, SIZES, FIELDS);
		buffer1.printBuffer(); // should see serialized version of 1, 2, 3
		System.out.println();
	
		
		// fill in a message object from the buffer
		System.out.println("Filling fields from buffer");
		messageObj.b = 42;
		messageObj.s = 43;
		messageObj.i = 44;
		System.out.println(messageObj.b + " " + messageObj.s + " " + messageObj.i);
		buffer1.reConstructFields(messageObj, SIZES, FIELDS);
		System.out.println(messageObj.b + " " + messageObj.s + " " + messageObj.i);
	
		
		// fill a buffer from the default values
		System.out.println("Filling buffer from elements");
		FillableBuffer buffer2 = new FillableBuffer(100);
		buffer2.deConstructElements(elements, SIZES);
		buffer2.printBuffer(); // should see serialized version of 1, 2, 3
		System.out.println();

		// fill elements from the buffer
		System.out.println("Filling elements from buffer");
		elements[0] = 42;
		elements[1] = 43;
		elements[2] = 44;
		for (int el: elements) System.out.println(el);
		buffer2.reConstructElements(elements, SIZES);
		for (int el: elements) System.out.println(el);
	
	}
	

}
