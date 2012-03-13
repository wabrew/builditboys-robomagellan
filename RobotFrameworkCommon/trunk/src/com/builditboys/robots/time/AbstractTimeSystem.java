package com.builditboys.robots.time;

import static com.builditboys.robots.units.TimeUnit.*;
import com.builditboys.robots.units.TimeUnit;

public abstract class AbstractTimeSystem {
	
	// currently, all time systems are lock to this unit
	// to change this requires making the time system converter smarter
	protected static final TimeUnit units = MILLISECONDS;

	private long absoluteOffset = 0;
	
	private String name;
	
	//--------------------------------------------------------------------------------

	protected AbstractTimeSystem (String nm) {
		name = nm;
	}
	
	//--------------------------------------------------------------------------------

	public String getName () {
		return name;
	}
	
	public long getOffset() {
		return absoluteOffset;
	}

	//--------------------------------------------------------------------------------

	protected long readClock () {
		if (units == MILLISECONDS) {
			return System.currentTimeMillis();
		}	
		else if (units == NANOSECONDS) {
			return System.nanoTime();
		}
		else {
			throw new IllegalStateException("Odd clock units");
		}
	}
	
	//--------------------------------------------------------------------------------

	public void startNow () {
		absoluteOffset = readClock();
	}

	public void correspondNow (double time, TimeUnit tunits) {
		absoluteOffset = readClock() - (long) TimeUnit.convert(time, tunits, units);
	}

	public void correspondNow (long time, TimeUnit tunits) {
		absoluteOffset = readClock() - (long) TimeUnit.convert(time, tunits, units);
	}

	public void correspondNow (int time, TimeUnit tunits) {
		absoluteOffset = readClock() - (long) TimeUnit.convert(time, tunits, units);
	}

	
	public void correspondNow (double time) {
		correspondNow(time, TimeUnit.getDefaultUnit());
	}
	
	public void correspondNow (long time) {
		correspondNow(time, TimeUnit.getDefaultUnit());
	}

	public void correspondNow (int time) {
		correspondNow(time, TimeUnit.getDefaultUnit());
	}

	//--------------------------------------------------------------------------------

	// put a method in your subclass that calls one of these
	
	public double currentTimeDouble () {
		return TimeUnit.convert((double) (readClock() - absoluteOffset), units, TimeUnit.getDefaultUnit());
	}
	
	public long currentTimeLong () {
		return TimeUnit.convert((long) (readClock() - absoluteOffset), units, TimeUnit.getDefaultUnit());
	}

	public int currentTimeInt () {
		long time = TimeUnit.convert(readClock() - absoluteOffset, units, TimeUnit.getDefaultUnit());
		if ((time > Integer.MAX_VALUE) || (time < Integer.MIN_VALUE)) {
			throw new IllegalArgumentException("Time overflow");
		}
		return (int) time;
	}
	
	//--------------------------------------------------------------------------------

	public static double convert (double time, AbstractTimeSystem fromSystem, AbstractTimeSystem toSystem) {
		long diff = fromSystem.absoluteOffset - toSystem.getOffset();
		return time - (double) diff;
	}

	public static long convert (long time, AbstractTimeSystem fromSystem, AbstractTimeSystem toSystem) {
		long diff = fromSystem.absoluteOffset - toSystem.getOffset();
		return time - (long) diff;
	}

	public static int convert (int time, AbstractTimeSystem fromSystem, AbstractTimeSystem toSystem) {
		long diff = fromSystem.absoluteOffset - toSystem.getOffset();
		long newTime = time - diff;
		if ((newTime > Integer.MAX_VALUE) || (newTime < Integer.MIN_VALUE)) {
			throw new IllegalArgumentException("Time overflow");
		}		
		return (int) newTime;
	}

}
