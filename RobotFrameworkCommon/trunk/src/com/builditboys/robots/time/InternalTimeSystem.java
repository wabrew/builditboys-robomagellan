package com.builditboys.robots.time;

import java.util.Date;

import com.builditboys.robots.units.TimeUnit;


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
		return new Date(TimeUnit.convert(time, UNITS, TimeUnit.MILLISECONDS));
	}
	

}
