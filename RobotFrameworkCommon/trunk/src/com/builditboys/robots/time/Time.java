package com.builditboys.robots.time;


public class Time {
	
	
/*
 Java is the slave, PSoC is the master
 
 Java side does
 
   InitializeLocalTime to get its local time setup.
   
   Sends a set clock to the PSoC (since it has only a 32 bit clock, we want it
   to be reset to correspond to now.
   
   Sends a correspond clock to PSoC to get its local time about the same as the Java
   side.
   
 PSoC side periodically sends clock sync requests to the Java side.  A clock sync
 has 4 local time values
 	time PSoC sent
 	time received on Java side
 	time sent back from the Java side
 	time received back on the PSoC side
 	
 From these numbers, it is possible to compute the skew between the two local times.
 The PSoC compensates for this skew and also tries to adjust its clock rate to keep the
 skew from changing.

*/

	//--------------------------------------------------------------------------------
	// local time -- this is what the robot runs on
	// It is a smaller number than internal time or absolute time since
	// we need to keep in in 32 bits.  Local time is setup near the beginning
	// of a run and lasts for 32 bits.  A wrap around will then occur and
	// things will go badly wrong.  When using a millisec clock rate, that gives
	// about 50 days.
	
	private static long localTimeOffset = 0;
	
	// generally, the slave side (Java) would do this at startup
	public static void initializeLocalTime () {
		localTimeOffset = getInternalTime();
	}
	
	// makes the current internal time correspond to the given local time
	// generally happens on the PSoC side when Java sends its local time
	public static void correspondLocalInternalTimes (long ltime) {
		localTimeOffset = getInternalTime() - ltime;
	}

	
	
	public static long getLocalTimeStart () {
		return localTimeOffset;
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
	
	
	
	public static long localTimeToInternalTime (long ltime) {
		return ltime + localTimeOffset;
	}

	public static long internalTimeToLocalTime (long itime) {
		return itime - localTimeOffset;
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
	
	
	
	public static long absoluteTimeToInternalTime (long atime) {
		return atime;
	}
	
	public static long internalTimeToAbsoluteTime (long itime) {
		return itime;
	}


	//--------------------------------------------------------------------------------
	// Absolute time
	
	public static long getAbsoluteTime () {
		return System.currentTimeMillis();
	}
	
	public static long secondToAbsoluteTime (long sec) {
		return sec * 1000;
	}
	
	public static long millisecToAbsoluteTime (long millisec) {
		return millisec;
	}

	public static long absoluteTimeToSecond (long atime) {
		return atime / 1000;
	}
	
	public static long absoluteTimeToMillisec (long atime) {
		return atime;
	}

	//--------------------------------------------------------------------------------

	

}

