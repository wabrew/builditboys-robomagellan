package com.builditboys.robots.infrastructure;

public class ObjectParameter  implements ParameterInterface {
	
	String name;
	
	Object value;
	
	//--------------------------------------------------------------------------------

	public ObjectParameter (String nm) {
		name = nm;
	}
	
	public ObjectParameter (String nm, Object val) {
		name = nm;
		value = val;
	}

	//--------------------------------------------------------------------------------

	public String getName () {
		return name;
	}
	
	public synchronized Object getValue() {
		return value;
	}

	public synchronized void setValue(Object value) {
		this.value = value;
	}

}
