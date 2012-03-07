package com.builditboys.robots.units;

public enum RoboTimeUnit {
	SECONDS(1*1000*1000),
	MILLISECONDS(1*1000),
	MICROSECONDS(1),
	ABSOLUTES(1*1000),
	LOCALS(1*1000);
	
	// Conversion factor to convert a unit to the common unit
	private final long conversionFactor;

	//--------------------------------------------------------------------------------
    // Constructor
	
	private RoboTimeUnit (long conversionFactor) {
		this.conversionFactor = conversionFactor;
	}
	
	public long convert (long val, RoboTimeUnit target) {
		if (conversionFactor == target.conversionFactor){
			return val;
		}
		else {
			return val * conversionFactor / target.conversionFactor;
		}
	}
}

