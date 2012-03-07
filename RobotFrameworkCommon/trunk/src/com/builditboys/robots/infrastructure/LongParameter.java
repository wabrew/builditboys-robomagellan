package com.builditboys.robots.infrastructure;

public class LongParameter  implements ParameterInterface {
	
	String name;
	
	Long value;
	
	//--------------------------------------------------------------------------------

	public LongParameter (String nm) {
		name = nm;
	}
	
	public LongParameter (String nm, Long val) {
		name = nm;
		value = val;
	}

	//--------------------------------------------------------------------------------

	public String getName () {
		return name;
	}
	

	public synchronized Long getValue() {
		return value;
	}

	public synchronized void setValue(Long value) {
		this.value = value;
	}

}
