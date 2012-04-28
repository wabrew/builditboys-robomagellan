package com.builditboys.robots.time;


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

}
