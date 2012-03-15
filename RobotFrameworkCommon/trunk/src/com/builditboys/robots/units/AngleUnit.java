package com.builditboys.robots.units;


public class AngleUnit extends AbstractUnit {
	public static final AngleUnit MICRORADIANS = new AngleUnit("degrees", 1);
	public static final AngleUnit BASE_UNIT = MICRORADIANS;

	public static final AngleUnit RADIANS = new AngleUnit("radians", 1000.0);
	public static final AngleUnit DEGREES = new AngleUnit("degrees", (Math.PI/180.0) * 1000);
	
	private static AngleUnit defaultUnit = BASE_UNIT;
	private static boolean defaultUnitLocked = false;

	//--------------------------------------------------------------------------------
	// Constructor

	private AngleUnit (String nm, double conversionFact) {
		super(nm, conversionFact);
	}
	
	//--------------------------------------------------------------------------------
	
	public static AngleUnit getDefaultUnit() {
		return defaultUnit;
	}

	public static void setDefaultUnit(AngleUnit defaultAngleUnit) {
		if (!defaultUnitLocked) {
			AngleUnit.defaultUnit = defaultAngleUnit;
		}
		else {
			throw new IllegalStateException("Default angle unit is locked");
		}
	}

	public static void lockDefaultUnit () {
		defaultUnitLocked = true;
	}
	
}
