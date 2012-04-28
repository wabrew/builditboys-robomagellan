package com.builditboys.robots.units;


public class LengthUnits extends AbstractUnit {
	
	public static final LengthUnits MICROMETERS= 
			new LengthUnits("micrometer", "micrometers", "microm", 1.0);
	
	public static final LengthUnits METER =
			new LengthUnits("meter", "meters", "m", 1000000.0);

	public static final LengthUnits CENTIMETER = 
			new LengthUnits("centimeter", "centimeters", "cm", 10000.0);

	public static final LengthUnits MILLIMETER = 
			new LengthUnits("millimeter", "millimeters", "mm", 1000.0);

	public static final LengthUnits INCH =
			new LengthUnits("inch", "inches", "in", 1/UnitConstants.INCHES_PER_METER * 1000000.0);

	public static final LengthUnits FOOT =
			new LengthUnits("foot", "feet", "ft", UnitConstants.INCHES_PER_FOOT/UnitConstants.INCHES_PER_METER * 1000000.0);

	
	
	public static final LengthUnits BASE_UNIT = MICROMETERS;

	
	// not a final but pretty close, you can set the base unit but you
	// should then lock it
	private static LengthUnits defaultUnit = BASE_UNIT;
	private static boolean defaultUnitLocked = false;

	//--------------------------------------------------------------------------------
    // Constructor
	
	private LengthUnits (String name, String plural, String abbreviation, double conversionFactor) {
		super(name, plural, abbreviation,
			  UnitKindEnum.LENGTH, conversionFactor, BASE_UNIT);
	}

	//--------------------------------------------------------------------------------

	public static LengthUnits getDefaultUnit() {
		return defaultUnit;
	}

	public static void setDefaultUnit(LengthUnits defaultLengthUnit) {
		if (!defaultUnitLocked) {
			LengthUnits.defaultUnit = defaultLengthUnit;
		}
		else {
			throw new IllegalStateException("Default length unit is locked");
		}
	}
	
	public static void lockDefaultUnit () {
		defaultUnitLocked = true;
	}
		
}
