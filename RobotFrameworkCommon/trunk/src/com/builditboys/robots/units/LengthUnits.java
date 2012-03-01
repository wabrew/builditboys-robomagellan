package com.builditboys.robots.units;


public enum LengthUnits {
	METERS(1.0),
	CENTIMETERS(0.01), 
	INCHES(1/Constants.INCHES_PER_METER), 
	FEET(Constants.INCHES_PER_FOOT/Constants.INCHES_PER_METER);
	
	// Conversion factor to convert a unit to the common unit
	private final double conversionFactor;
	
	//--------------------------------------------------------------------------------
    // Constructor
	
	private LengthUnits (double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}
	
	public double convert (double val, LengthUnits target) {
		if (target == METERS) {
			return val;
		}
		else {
			return val * conversionFactor / target.conversionFactor;
		}
	}
}
