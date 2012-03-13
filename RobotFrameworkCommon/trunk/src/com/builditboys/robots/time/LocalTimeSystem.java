package com.builditboys.robots.time;

//--------------------------------------------------------------------------------
// local time -- this is what the robot runs on
// It is a smaller number than internal time or absolute time since
// we need to keep in in 32 bits.  Local time is setup near the beginning
// of a run and lasts for 32 bits.  A wrap around will then occur and
// things will go badly wrong.  When using a millisec clock rate, that gives
// about 50 days.

public class LocalTimeSystem extends AbstractTimeSystem {
	
	private static final LocalTimeSystem instance = new LocalTimeSystem("local");
	
	//--------------------------------------------------------------------------------

	private LocalTimeSystem (String nm) {
		super(nm);
	}
	
	//--------------------------------------------------------------------------------
		
	public static void startLocalTimeNow() {
		instance.startNow();
	}

	// adjusts local time so that it is now ltime
	// generally happens on the PSoC side when Java sends its local time
	public static void correspondLocalTime (int ltime) {
		instance.correspondNow(ltime);
	}
	
	public static int currentTime () {
		return instance.currentTimeInt();
	}

}
