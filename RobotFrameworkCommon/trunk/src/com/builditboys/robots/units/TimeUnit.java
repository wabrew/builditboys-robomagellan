package com.builditboys.robots.units;

public class TimeUnit extends AbstractUnit {
	public static final TimeUnit SECONDS =      new TimeUnit("seconds", 1.0E6);
	public static final TimeUnit MILLISECONDS = new TimeUnit("milliseconds", 1.0E3);
	public static final TimeUnit MICROSECONDS = new TimeUnit("microseconds", 1.0);
	public static final TimeUnit NANOSECONDS =  new TimeUnit("nanoseconds", 1.0E-3);
	public static final TimeUnit ABSOLUTES =    new TimeUnit("absolutes", 1.0E3);
	public static final TimeUnit LOCALS =       new TimeUnit("locals", 1.0E3);

	public static final TimeUnit BASE_UNIT = MILLISECONDS;
	
	
	private static TimeUnit defaultUnit = BASE_UNIT;
	private static boolean defaultUnitLocked = false;
	
	//--------------------------------------------------------------------------------
    // Constructor
	
	private TimeUnit (String nm, double conversionFact) {
		super(nm, conversionFact);
	}
	
	//--------------------------------------------------------------------------------

	public static TimeUnit getDefaultUnit() {
		return defaultUnit;
	}

	public static void setDefaultUnit(TimeUnit defaultTimeUnit) {
		if (!defaultUnitLocked) {
			TimeUnit.defaultUnit = defaultTimeUnit;
		}
		else {
			throw new IllegalStateException("Default time unit is locked");
		}
	}
	
	public static void lockDefaultUnit () {
		defaultUnitLocked = true;
	}

}


