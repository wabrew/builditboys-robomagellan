package com.builditboys.robots.infrastructure;

public class ByteParameter  implements ParameterInterface {
	
	String name;
	
	Byte value;
	
	//--------------------------------------------------------------------------------

	public ByteParameter (String nm) {
		name = nm;
	}
	
	public ByteParameter (String nm, Byte val) {
		name = nm;
		value = val;
	}

	//--------------------------------------------------------------------------------

	public String getName () {
		return name;
	}
	
	public synchronized Byte getValue() {
		return value;
	}

	public synchronized void setValue(Byte value) {
		this.value = value;
	}

}
