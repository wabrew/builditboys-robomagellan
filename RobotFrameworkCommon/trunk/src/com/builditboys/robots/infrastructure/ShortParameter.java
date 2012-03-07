package com.builditboys.robots.infrastructure;

public class ShortParameter  implements ParameterInterface {
	
	String name;
	
	Short value;
	
	//--------------------------------------------------------------------------------
	
	public ShortParameter (String nm) {
		name = nm;
	}
	
	public ShortParameter (String nm, Short val) {
		name = nm;
		value = val;
	}

	//--------------------------------------------------------------------------------

	public String getName () {
		return name;
	}

	public synchronized Short getValue() {
		return value;
	}

	public synchronized void setValue(Short value) {
		this.value = value;
	}

}
