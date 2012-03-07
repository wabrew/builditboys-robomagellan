package com.builditboys.robots.units;


public enum LengthUnit {
	METERS(1.0),
	CENTIMETERS(0.01), 
	INCHES(1/UnitConstants.INCHES_PER_METER), 
	FEET(UnitConstants.INCHES_PER_FOOT/UnitConstants.INCHES_PER_METER);
	
	// Conversion factor to convert a unit to the common unit
	private final double conversionFactor;
	


	//--------------------------------------------------------------------------------
    // Constructor
	
	private LengthUnit (double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}
	
	public double convert (double val, LengthUnit target) {
		if (conversionFactor == target.conversionFactor) {
			return val;
		}
		else {
			return val * conversionFactor / target.conversionFactor;
		}
	}
}
