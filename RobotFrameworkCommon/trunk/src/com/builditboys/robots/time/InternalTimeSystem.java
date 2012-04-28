package com.builditboys.robots.time;

import java.util.Date;

import com.builditboys.robots.units.TimeUnits;


public class InternalTimeSystem extends AbstractTimeSystem {
	
	private static final InternalTimeSystem INSTANCE = new InternalTimeSystem("internal");
	
	//--------------------------------------------------------------------------------

	private InternalTimeSystem (String nm) {
		super(nm);
	}
	
	//--------------------------------------------------------------------------------
		
	public static long currentTime () {
		return INSTANCE.currentTimeLong();
	}

	//--------------------------------------------------------------------------------
	
	public static Date toDate (long time) {
		return new Date(TimeUnits.convert(time, UNITS, TimeUnits.MILLISECONDS));
	}
	

}
