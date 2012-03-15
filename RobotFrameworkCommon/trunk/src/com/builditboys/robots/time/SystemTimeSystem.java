package com.builditboys.robots.time;

public class SystemTimeSystem extends AbstractTimeSystem {

	private static final SystemTimeSystem INSTANCE = new SystemTimeSystem("system");
	
	//--------------------------------------------------------------------------------

	private SystemTimeSystem (String nm) {
		super(nm);
	}
	
	//--------------------------------------------------------------------------------
		
	public static long currentTime () {
		return INSTANCE.currentTimeLong();
	}

}
