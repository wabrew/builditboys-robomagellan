package com.builditboys.robots.infrastructure;

public class StringParameter implements ParameterInterface {
	
	String name;
	
	String value;
	
	//--------------------------------------------------------------------------------

	public StringParameter (String nm) {
		name = nm;
	}
	
	public StringParameter (String nm, String val) {
		name = nm;
		value = val;
	}
	
	//--------------------------------------------------------------------------------

	public String getName () {
		return name;
	}
	

	public synchronized String getValue() {
		return value;
	}

	public synchronized void setValue(String value) {
		this.value = value;
	}
	
	

}
