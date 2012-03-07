package com.builditboys.robots.infrastructure;

public class CharacterParameter  implements ParameterInterface {
	
	String name;
	
	Character value;
	
	public String getName () {
		return name;
	}
	
	public CharacterParameter (String nm) {
		name = nm;
	}
	
	public CharacterParameter (String nm, Character val) {
		name = nm;
		value = val;
	}

	public synchronized Character getValue() {
		return value;
	}

	public synchronized void setValue(Character value) {
		this.value = value;
	}

}
