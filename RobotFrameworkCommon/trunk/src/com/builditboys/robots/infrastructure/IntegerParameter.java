package com.builditboys.robots.infrastructure;

public class IntegerParameter extends AbstractParameter implements ParameterInterface {

	Integer value = 0;

	// --------------------------------------------------------------------------------

	public IntegerParameter(String nm) {
		name = nm;
	}

	public IntegerParameter(String nm, Integer val) {
		name = nm;
		value = val;
	}

	// --------------------------------------------------------------------------------

	public synchronized Integer getValue() {
		return value;
	}

	public synchronized void setValue(Integer value) {
		this.value = value;
	}
	
	// --------------------------------------------------------------------------------

	public String toString() {
		return "Integer parm: " + value;
	}

}
