package com.builditboys.robots.infrastructure;

public class ObjectParameter extends AbstractParameter implements ParameterInterface {
	
	Object value = null;
	
	//--------------------------------------------------------------------------------

	public ObjectParameter (String nm) {
		name = nm;
	}
	
	public ObjectParameter (String nm, Object val) {
		name = nm;
		value = val;
	}

	//--------------------------------------------------------------------------------

	public synchronized Object getValue() {
		return value;
	}

	public synchronized void setValue(Object value) {
		this.value = value;
	}
	
	// --------------------------------------------------------------------------------

	public String toString() {
		return "Object parm: " + value;
	}


}
