// Extend this class to hold the information in you parameter.
// All methods to get or modify the information need to be synchronized.

// 
package com.builditboys.robots.infrastructure;


// instances should use synchronized methods for accessing setting values

public interface ParameterInterface {

	public abstract String getName();
	
	// do NOT have a setName, a parameters name should be assigned in the 
	// constructor

}