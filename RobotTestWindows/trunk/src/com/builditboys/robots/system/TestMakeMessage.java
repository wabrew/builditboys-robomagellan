package com.builditboys.robots.system;

import com.builditboys.robots.utilities.FillableBuffer;
import com.builditboys.robots.utilities.SimpleMessage;

public class TestMakeMessage {

	public static void main (String args[]) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		
		FillableBuffer buffer1 = new FillableBuffer(100);
		SimpleMessage message1 = new SimpleMessage();
		int sizes[] = {1, 2, 4};
		String fields[] = {"b", "s", "i"};
		
		buffer1.deConstructFields(message1, sizes, fields);
		buffer1.printBuffer();
		
	}
	

}
