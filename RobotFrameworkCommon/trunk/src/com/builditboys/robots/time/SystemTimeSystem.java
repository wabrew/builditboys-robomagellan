package com.builditboys.robots.time;

public class SystemTimeSystem extends AbstractTimeSystem {

	private static final SystemTimeSystem instance = new SystemTimeSystem("system");
	
	//--------------------------------------------------------------------------------

	private SystemTimeSystem (String nm) {
		super(nm);
	}
	
	//--------------------------------------------------------------------------------
		
	public static long currentTime () {
		return instance.currentTimeLong();
	}

}
