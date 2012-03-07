package com.builditboys.robots.infrastructure;

import java.util.HashMap;

public class ParameterServer {

	private HashMap<String, ParameterInterface> parameters;

	// --------------------------------------------------------------------------------
	// Constructors

	private ParameterServer() {
		parameters = new HashMap<String, ParameterInterface>();
	}

	// --------------------------------------------------------------------------------
	// Singleton

	private static final ParameterServer instance = new ParameterServer();

	public static ParameterServer getInstance() {
		return instance;
	}

	// --------------------------------------------------------------------------------
	// Adding parameters

	// synchronized so that you see a stable set of parameters
	public synchronized void addParameter(ParameterInterface parm) {
		String name = parm.getName();

		if (parameters.get(name) == null) {
			parameters.put(name, parm);
		} else {
			throw new IllegalStateException();
		}
	}

	// synchronized so that you see a stable set of parameters
	public synchronized void replaceParameter(ParameterInterface parm) {
		parameters.put(parm.getName(), parm);
	}

	// --------------------------------------------------------------------------------
	// Removing parameters

	// synchronized so that you see a stable set of parameters
	public synchronized void removeParameter(String key) {
		parameters.remove(key);
	}

	// --------------------------------------------------------------------------------
	// Getting parameters

	// synchronized so that you see a stable set of parameters
	public synchronized ParameterInterface getParameter(String key) {
		ParameterInterface parm = parameters.get(key);

		if (parm != null) {
			return parm;
		} else {
			throw new IllegalStateException();
		}
	}

	// synchronized so that you see a stable set of parameters
	public synchronized ParameterInterface getParameterMaybe(String key) {
		return parameters.get(key);
	}
	
	// --------------------------------------------------------------------------------


}
