package com.builditboys.robots.infrastructure;

public class ByteParameter extends AbstractParameter implements ParameterInterface {
	
	Byte value = 0;
	
	//--------------------------------------------------------------------------------

	public ByteParameter (String nm) {
		name = nm;
	}
	
	public ByteParameter (String nm, Byte val) {
		name = nm;
		value = val;
	}

	// --------------------------------------------------------------------------------

	public static ByteParameter getParameter (String key) {
		return (ByteParameter) ParameterServer.getParameter(key);
	}
	
	public static ByteParameter maybeGetParameter (String key) {
		return (ByteParameter) ParameterServer.getParameter(key);
	}
	
	//--------------------------------------------------------------------------------

	public synchronized Byte getValue() {
		return value;
	}

	public synchronized void setValue(Byte value) {
		this.value = value;
	}
	// --------------------------------------------------------------------------------

	public String toString() {
		return "Byte parm: " + value;
	}

}
