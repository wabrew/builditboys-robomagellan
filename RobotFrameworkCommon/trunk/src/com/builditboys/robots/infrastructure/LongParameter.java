package com.builditboys.robots.infrastructure;

public class LongParameter extends AbstractParameter implements ParameterInterface {
	
	Long value = (long) 0;
	
	//--------------------------------------------------------------------------------

	public LongParameter (String nm) {
		name = nm;
	}
	
	public LongParameter (String nm, Long val) {
		name = nm;
		value = val;
	}

	// --------------------------------------------------------------------------------

	public static LongParameter getParameter (String key) {
		return (LongParameter) ParameterServer.getParameter(key);
	}
	
	public static LongParameter maybeGetParameter (String key) {
		return (LongParameter) ParameterServer.maybeGetParameter(key);
	}
	
	//--------------------------------------------------------------------------------

	public synchronized Long getValue() {
		return value;
	}

	public synchronized void setValue(Long value) {
		this.value = value;
	}
	// --------------------------------------------------------------------------------

	public String toString() {
		return "Long parm: " + value;
	}


}
