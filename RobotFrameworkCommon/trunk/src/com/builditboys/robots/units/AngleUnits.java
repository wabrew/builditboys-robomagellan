package com.builditboys.robots.units;


public enum AngleUnits {
	RADIANS(1.0),
	DEGREES(Math.PI/180.0);
	
	// Conversion factor to convert a unit to the common unit
	private final double conversionFactor;

	//--------------------------------------------------------------------------------
	// Constructor

	private AngleUnits (double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}
	
	public double convert (double val, AngleUnits target) {
		if (target == RADIANS) {
			return val;
		}
		else {
			return val * conversionFactor / target.conversionFactor;
		}
	}

}
