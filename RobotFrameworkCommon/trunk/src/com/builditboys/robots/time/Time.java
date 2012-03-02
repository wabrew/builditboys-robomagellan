package com.builditboys.robots.time;


public class Time {
	
	//--------------------------------------------------------------------------------
	// local time -- this is what the robot runs on
	
	private static long localTimeOffset = 0;
	
	// local time and internal time will be the same
	public static void initializeLocalTime () {
		localTimeOffset = 0;
	}
	
	// makes the current internal time correspond to the given local time
	public static void correspondLocalInternalTimes (long ltime) {
		localTimeOffset = ltime - getInternalTime();
	}
	
	public static long localTimeToInternalTime (long ltime) {
		return ltime - localTimeOffset;
	}

	public static long internalTimeToLocalTime (long itime) {
		return itime + localTimeOffset;
	}

	public static long getLocalTime () {
		return getInternalTime() - localTimeOffset;
	}
	
	public static long secondToLocalTime (long sec) {
		return sec * 1000;
	}
	
	public static long millisecToLocalTime (long millisec) {
		return millisec;
	}

	public static long localTimeToSecond (long ltime) {
		return ltime / 1000;
	}
	
	public static long localTimeToMillisec (long ltime) {
		return ltime;
	}
	
	//--------------------------------------------------------------------------------
	// Internal time
	
	// for the java side, internal time is the same as absolute time
	public static long getInternalTime () {
		return System.currentTimeMillis();
	}
	
	public static long secondToInternalTime (long sec) {
		return sec * 1000;
	}
	
	public static long millisecToInternalTime (long millisec) {
		return millisec;
	}

	public static long internalTimeToSecond (long ltime) {
		return ltime / 1000;
	}
	
	public static long internalTimeToMillisec (long ltime) {
		return ltime;
	}
	

	//--------------------------------------------------------------------------------
	// Absolute time
	
	public static long getAbsoluteTime () {
		return 0;
	}
	
	public static long secondToAbsoluteTime (long sec) {
		return sec * 1000;
	}
	
	public static long millisecToAbsoluteTime (long millisec) {
		return millisec;
	}

	public static long absoluteTimeToSecond (long ltime) {
		return ltime / 1000;
	}
	
	public static long absoluteTimeToMillisec (long ltime) {
		return ltime;
	}
	

}

