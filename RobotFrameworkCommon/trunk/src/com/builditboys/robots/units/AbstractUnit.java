package com.builditboys.robots.units;

// keep in sync with version in misc

public abstract class AbstractUnit {

	public static enum UnitKindEnum { 
		LENGTH,
		MASS,
		TIME,
		ANGLE,
		WEIGHT,
		VOLUME,
		VELOCITY,
		ANGULAR_VELOCITY,
		POWER,
		ENERGY };
	
	// The name of the unit
	public final String name;
	
	// The plural name of the unit
	public final String plural;
	
	// The abbreviation for the unit
	public final String abbreviation;
	
	// The kind of unit, weight, length, etc.
	public final UnitKindEnum kind;

	// Conversion factor to convert a unit to the common unit
	public final double conversionFactor;
	
	// The conversion factor converts to this
	public final AbstractUnit baseUnit;


	//--------------------------------------------------------------------------------

	public AbstractUnit (String name, String plural, String abbreviation,
				  UnitKindEnum kind, double conversionFactor, AbstractUnit baseUnit) {
		this.name = name;
		this.plural = plural;
		this.abbreviation = abbreviation;
		this.kind = kind;
		this.conversionFactor = conversionFactor;	
		this.baseUnit = baseUnit;
	}
	
	//--------------------------------------------------------------------------------

	public String getName() {
		return name;
	}
	
	public double getConversionFactor() {
		return conversionFactor;
	}

	//--------------------------------------------------------------------------------

	public static double convert (double val, AbstractUnit fromUnit, AbstractUnit toUnit) {
		// same unit, no conversion necessary
		if (fromUnit == toUnit) {
			return val;
		}
		
		if (fromUnit.baseUnit != toUnit.baseUnit) {
			throw new IllegalStateException("Base unit mismatch");
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
		
		if (fromUnit.baseUnit != toUnit.baseUnit) {
			throw new IllegalStateException("Base unit mismatch");
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
		
		if (fromUnit.baseUnit != toUnit.baseUnit) {
			throw new IllegalStateException("Base unit mismatch");
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
