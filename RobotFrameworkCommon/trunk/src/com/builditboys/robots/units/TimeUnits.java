package com.builditboys.robots.units;

public class TimeUnits extends AbstractUnit {

	public static final TimeUnits SECOND =      
			new TimeUnits("second", "seconds", "sec", 1.0E6);

	public static final TimeUnits MILLISECONDS = 
			new TimeUnits("millisecond", "milliseconds", "msec", 1.0E3);

	public static final TimeUnits MICROSECONDS = 
			new TimeUnits("microsecond", "microseconds", "usec", 1.0);

	public static final TimeUnits NANOSECONDS = 
			new TimeUnits("nanosecond", "nanoseconds", "nsec", 1.0E-3);
	
	public static final TimeUnits MINUTE =      
			new TimeUnits("minute", "minutess", "min", 60.0 * 1.0E6);
	
	public static final TimeUnits HOUR =      
			new TimeUnits("hour", "hours", "hr", 60.0 * 60.0 * 1.0E6);
	
	public static final TimeUnits DAY =      
			new TimeUnits("day", "days", "dy", 60.0 * 60.0 * 24 * 1.0E6);
	
	public static final TimeUnits YEAR =      
			new TimeUnits("year", "years", "yr", 60.0 * 60.0 * 24 * 365.25 * 1.0E6);
	

	
	
	public static final TimeUnits ABSOLUTES =    
			new TimeUnits("absolute", "absolutes", "abst", 1.0E3);

	public static final TimeUnits LOCALS =      
			new TimeUnits("local", "locals", "loct", 1.0E3);
	

	
	public static final TimeUnits BASE_UNIT = MILLISECONDS;

	
	// not a final but pretty close, you can set the base unit but you
	// should then lock it
	private static TimeUnits defaultUnit = BASE_UNIT;
	private static boolean defaultUnitLocked = false;
	
	//--------------------------------------------------------------------------------
    // Constructor
	
	private TimeUnits (String name, String plural, String abbreviation, double conversionFactor) {
		super(name, plural, abbreviation,
			  UnitKindEnum.TIME, conversionFactor, BASE_UNIT);
	}
	
	//--------------------------------------------------------------------------------

	public static TimeUnits getDefaultUnit() {
		return defaultUnit;
	}

	public static void setDefaultUnit(TimeUnits defaultTimeUnit) {
		if (!defaultUnitLocked) {
			TimeUnits.defaultUnit = defaultTimeUnit;
		}
		else {
			throw new IllegalStateException("Default time unit is locked");
		}
	}
	
	public static void lockDefaultUnit () {
		defaultUnitLocked = true;
	}

}


