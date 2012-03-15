package com.builditboys.robots.infrastructure;

import com.builditboys.robots.utilities.MiscUtilities;

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

	// --------------------------------------------------------------------------------

	public static ObjectParameter getParameter (String key) {
		return (ObjectParameter) ParameterServer.getParameter(key);
	}
	
	public static ObjectParameter maybeGetParameter (String key) {
		return (ObjectParameter) ParameterServer.getParameter(key);
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
		return "Object parm: " + MiscUtilities.bestObjectName(value);
	}


}
