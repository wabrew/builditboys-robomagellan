package com.builditboys.robots.infrastructure;

public class StringParameter extends AbstractParameter implements ParameterInterface {
	
	String value = null;
	
	//--------------------------------------------------------------------------------

	public StringParameter (String nm) {
		name = nm;
	}
	
	public StringParameter (String nm, String val) {
		name = nm;
		value = val;
	}
	
	// --------------------------------------------------------------------------------

	public static StringParameter getParameter (String key) {
		return (StringParameter) ParameterServer.getParameter(key);
	}
	
	public static StringParameter maybeGetParameter (String key) {
		return (StringParameter) ParameterServer.getParameter(key);
	}
	
	//--------------------------------------------------------------------------------

	public synchronized String getValue() {
		return value;
	}

	public synchronized void setValue(String value) {
		this.value = value;
	}
	
	// --------------------------------------------------------------------------------

	public String toString() {
		return "String parm: \"" + value + "\"";
	}

}
