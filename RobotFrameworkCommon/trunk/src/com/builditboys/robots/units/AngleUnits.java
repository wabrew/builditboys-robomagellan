package com.builditboys.robots.units;

// no longer used, now hooks up to misc

public class AngleUnits extends AbstractUnit {
	
	public static final AngleUnits MICRORADIANS =
			new AngleUnits("microradian", "microradians", "urad", 1.0);

	public static final AngleUnits RADIANS =
			new AngleUnits("radian", "radians", "rad", 1000.0);
	
	public static final AngleUnits DEGREES =
			new AngleUnits("degree", "degrees", "deg", (Math.PI/180.0) * 1000.0);
	

	
	public static final AngleUnits BASE_UNIT = MICRORADIANS;

	
	// not a final but pretty close, you can set the base unit but you
	// should then lock it
	private static AngleUnits defaultUnit = BASE_UNIT;
	private static boolean defaultUnitLocked = false;

	//--------------------------------------------------------------------------------
	// Constructor

	private AngleUnits (String name, String plural, String abbreviation, double conversionFactor) {
		super(name, plural, abbreviation, 
			  UnitKindEnum.ANGLE, conversionFactor, BASE_UNIT);
	}
	
	//--------------------------------------------------------------------------------
	
	public static AngleUnits getDefaultUnit() {
		return defaultUnit;
	}

	public static void setDefaultUnit(AngleUnits defaultAngleUnit) {
		if (!defaultUnitLocked) {
			AngleUnits.defaultUnit = defaultAngleUnit;
		}
		else {
			throw new IllegalStateException("Default angle unit is locked");
		}
	}

	public static void lockDefaultUnit () {
		defaultUnitLocked = true;
	}
	
}
