package com.builditboys.robots.units;

import com.builditboys.misc.units.AbstractUnit;
import com.builditboys.misc.units.LengthUnits;

public class RobotLengthUnits extends AbstractUnit {

	public static final RobotLengthUnits COUNTS = 
			new RobotLengthUnits("count", "counts", "cnt", 1.0);
	
	
	
	public static final AbstractUnit BASE_UNIT = LengthUnits.MICROMETERS;

/*	
	// not a final but pretty close, you can set the base unit but you
	// should then lock it
	private static RobotLengthUnits defaultUnit = BASE_UNIT;
	private static boolean defaultUnitLocked = false;
*/
	//--------------------------------------------------------------------------------
    // Constructor
	
	private RobotLengthUnits (String name, String plural, String abbreviation, double conversionFactor) {
		super(name, plural, abbreviation,
			  UnitKindEnum.LENGTH, conversionFactor, BASE_UNIT);
	}

	//--------------------------------------------------------------------------------

/*
	public static RobotLengthUnits getDefaultUnit() {
		return defaultUnit;
	}

	public static void setDefaultUnit(RobotLengthUnits defaultLengthUnit) {
		if (!defaultUnitLocked) {
			RobotLengthUnits.defaultUnit = defaultLengthUnit;
		}
		else {
			throw new IllegalStateException("Default length unit is locked");
		}
	}
	
	public static void lockDefaultUnit () {
		defaultUnitLocked = true;
	}
*/
		
}
