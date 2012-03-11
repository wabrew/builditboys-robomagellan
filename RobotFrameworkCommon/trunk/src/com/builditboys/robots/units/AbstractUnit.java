package com.builditboys.robots.units;

public abstract class AbstractUnit {

	// Conversion factor to convert a unit to the common unit
	protected final double conversionFactor;
	protected final String name;
		
	//--------------------------------------------------------------------------------

	AbstractUnit (String nm, double factor) {
		name = nm;
		conversionFactor = factor;
	}
	
	//--------------------------------------------------------------------------------

	public double getConversionFactor() {
		return conversionFactor;
	}

	public String getName() {
		return name;
	}
	
	//--------------------------------------------------------------------------------

	public static double convert (double val, AbstractUnit fromUnit, AbstractUnit toUnit) {
		// same unit, no conversion necessary
		if (fromUnit == toUnit) {
			return val;
		}
		
		// same conversion factor, no conversion necessary
		double fromConversionFactor = fromUnit.conversionFactor;
		double toConversionFactor = toUnit.conversionFactor;
		if (fromConversionFactor == toConversionFactor) {
			return val;
		}
		
		// do the conversion
		double prod;
		double quot;
		prod = val;
		if (fromConversionFactor != 1.0) {
			prod = prod * fromConversionFactor;
		}
		quot = prod;
		if (toConversionFactor != 1.0) {
			quot = quot / toConversionFactor;
		}
		return quot;
	}
	
	public static long convert (long val, AbstractUnit fromUnit, AbstractUnit toUnit) {
		// same unit, no conversion necessary
		if (fromUnit == toUnit) {
			return val;
		}
		
		// same conversion factor, no conversion necessary
		double fromConversionFactor = fromUnit.conversionFactor;
		double toConversionFactor = toUnit.conversionFactor;
		if (fromConversionFactor == toConversionFactor) {
			return val;
		}
		
		// do the conversion
		double prod;
		double quot;
		prod = (double) val;
		if (fromConversionFactor != 1.0) {
			prod = prod * fromConversionFactor;
		}
		quot = prod;
		if (toConversionFactor != 1.0) {
			quot = quot / toConversionFactor;
		}
		return (long) quot;
	}

	public static int convert (int val, AbstractUnit fromUnit, AbstractUnit toUnit) {
		// same unit, no conversion necessary
		if (fromUnit == toUnit) {
			return val;
		}
		
		// same conversion factor, no conversion necessary
		double fromConversionFactor = fromUnit.conversionFactor;
		double toConversionFactor = toUnit.conversionFactor;
		if (fromConversionFactor == toConversionFactor) {
			return val;
		}
		
		// do the conversion
		double prod;
		double quot;
		prod = (double) val;
		if (fromConversionFactor != 1.0) {
			prod = prod * fromConversionFactor;
		}
		quot = prod;
		if (toConversionFactor != 1.0) {
			quot = quot / toConversionFactor;
		}
		return (int) quot;
	}

}
