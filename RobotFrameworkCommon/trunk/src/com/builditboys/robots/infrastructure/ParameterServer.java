package com.builditboys.robots.infrastructure;

import java.util.HashMap;
import java.util.Map;

import com.builditboys.robots.utilities.MiscUtilities;

public final class ParameterServer {

	// Singleton
	private static final ParameterServer instance = new ParameterServer();

	private final HashMap<String, ParameterInterface> parameters;

	// --------------------------------------------------------------------------------
	// Constructors

	private ParameterServer() {
		parameters = new HashMap<String, ParameterInterface>();
	}

	// --------------------------------------------------------------------------------
	// Adding parameters

	public static void addParameter (ParameterInterface parm) {
		instance.addParameterI(parm);
	}
	
	// synchronized so that you see a stable set of parameters
	private synchronized void addParameterI (ParameterInterface parm) {
		String name = parm.getName();
		if (name == null) {
			throw new IllegalArgumentException("null parameter name");
		}
		if (parameters.get(name) != null) {
			throw new IllegalStateException();
		}
		parameters.put(name, parm);
	}

	public static void replaceParameter (ParameterInterface parm) {
		instance.replaceParameterI(parm);
	}
	
	// synchronized so that you see a stable set of parameters
	private synchronized void replaceParameterI (ParameterInterface parm) {
		String name = parm.getName();
		if (name == null) {
			throw new IllegalArgumentException("null parameter name");
		}
		parameters.put(parm.getName(), parm);
	}

	// --------------------------------------------------------------------------------
	// Removing parameters

	public static void removeParameter (String key) {
		instance.removeParameterI(key);
	}
	
	// synchronized so that you see a stable set of parameters
	private synchronized void removeParameterI(String key) {
		parameters.remove(key);
	}

	// --------------------------------------------------------------------------------
	// Getting parameters

	public static ParameterInterface getParameter (String key) {
		return instance.getParameterI(key);
	}
	
	// synchronized so that you see a stable set of parameters
	private synchronized ParameterInterface getParameterI(String key) {
		ParameterInterface parm = parameters.get(key);

		if (parm != null) {
			return parm;
		} else {
			throw new IllegalStateException();
		}
	}

	public static ParameterInterface maybeGetParameter (String key) {
		return instance.maybeGetParameterI(key);
	}
	
	// synchronized so that you see a stable set of parameters
	private synchronized ParameterInterface maybeGetParameterI(String key) {
		return parameters.get(key);
	}
	
	// --------------------------------------------------------------------------------

	public static void print () {
		HashMap<String, ParameterInterface> parmMap = instance.parameters;
		System.out.println("Parameters:");
		if (parmMap != null) {
			for (Map.Entry<String, ParameterInterface> entry : parmMap.entrySet()) {
				System.out.println("\"" + entry.getKey() + "\" -> " + MiscUtilities.bestObjectName(entry.getValue()));
			}
		}
		else {
			System.out.println("currently there are no parameters");
		}
	}
	
	// --------------------------------------------------------------------------------


}
