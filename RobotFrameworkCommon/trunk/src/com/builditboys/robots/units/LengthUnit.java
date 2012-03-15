package com.builditboys.robots.units;


public class LengthUnit extends AbstractUnit {
	public static final LengthUnit MICROMETERS = new LengthUnit("micrometers", 1.0);
	public static final LengthUnit BASE_UNIT = MICROMETERS;
	
	public static final LengthUnit METERS =      new LengthUnit("meters", 1000000.0);
	public static final LengthUnit CENTIMETERS = new LengthUnit("centimeters", 10000.0);
	public static final LengthUnit MILLIMETERS = new LengthUnit("millimeters", 1000.0);
	public static final LengthUnit INCHES =      new LengthUnit("inches", 1/UnitConstants.INCHES_PER_METER * 1000000.0);
	public static final LengthUnit FEET =        new LengthUnit("feet", UnitConstants.INCHES_PER_FOOT/UnitConstants.INCHES_PER_METER * 1000000.0);
	public static final LengthUnit COUNTS =      new LengthUnit("feet", 500.0);
	
	
	// not a final but pretty close, you can set the base unit but you
	// should then lock it
	private static LengthUnit defaultUnit = BASE_UNIT;
	private static boolean defaultUnitLocked = false;

	//--------------------------------------------------------------------------------
    // Constructor
	
	private LengthUnit (String nm, double conversionFact) {
		super(nm, conversionFact);
	}

	//--------------------------------------------------------------------------------

	public static LengthUnit getDefaultUnit() {
		return defaultUnit;
	}

	public static void setDefaultUnit(LengthUnit defaultLengthUnit) {
		if (!defaultUnitLocked) {
			LengthUnit.defaultUnit = defaultLengthUnit;
		}
		else {
			throw new IllegalStateException("Default length unit is locked");
		}
	}
	
	public static void lockDefaultUnit () {
		defaultUnitLocked = true;
	}
		
}
