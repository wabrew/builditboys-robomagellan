package com.builditboys.robots.units;


public enum AngleUnit {
	RADIANS(1.0),
	DEGREES(Math.PI/180.0);
	
	// Conversion factor to convert a unit to the common unit
	private final double conversionFactor;

	//--------------------------------------------------------------------------------
	// Constructor

	private AngleUnit (double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}
	
	public double convert (double val, AngleUnit target) {
		if (conversionFactor == target.conversionFactor){
			return val;
		}
		else {
			return val * conversionFactor / target.conversionFactor;
		}
	}

}
