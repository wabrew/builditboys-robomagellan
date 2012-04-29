package com.builditboys.robots.units;

import com.builditboys.misc.units.AbstractUnit;
import com.builditboys.misc.units.LengthUnits;

public class RobotLengthUnits extends AbstractUnit {

	public static final RobotLengthUnits COUNTS = 
			new RobotLengthUnits("count", "counts", "cnt", 1.0, LengthUnits.MILLIMETER);
	
	
	//--------------------------------------------------------------------------------
    // Constructor
	
	private RobotLengthUnits (String name, String plural, String abbreviation,
							  double conversionFactor, AbstractUnit baseUnit) {
		super(name, plural, abbreviation,
			  UnitKindEnum.LENGTH, conversionFactor, baseUnit);
	}

	//--------------------------------------------------------------------------------


		
}
