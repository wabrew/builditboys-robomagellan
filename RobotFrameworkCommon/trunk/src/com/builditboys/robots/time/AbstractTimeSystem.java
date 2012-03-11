package com.builditboys.robots.time;

import static com.builditboys.robots.units.TimeUnit.*;
import com.builditboys.robots.units.TimeUnit;

public abstract class AbstractTimeSystem {
	
	private long absoluteOffset = 0;
	protected static TimeUnit units = NANOSECONDS;   // all time systems are in NANOSECONDS
	
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

	public void startNow () {
		absoluteOffset = System.nanoTime();
	}

	public void correspondNow (double time, TimeUnit tunits) {
		absoluteOffset = System.nanoTime() - (long) TimeUnit.convert(time, tunits, units);
	}

	public void correspondNow (long time, TimeUnit tunits) {
		absoluteOffset = System.nanoTime() - (long) TimeUnit.convert(time, tunits, units);
	}

	public void correspondNow (int time, TimeUnit tunits) {
		absoluteOffset = System.nanoTime() - (long) TimeUnit.convert(time, tunits, units);
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
		return TimeUnit.convert((double) (System.nanoTime() - absoluteOffset), NANOSECONDS, TimeUnit.getDefaultUnit());
	}
	
	public long currentTimeLong () {
		return TimeUnit.convert((long) (System.nanoTime() - absoluteOffset), NANOSECONDS, TimeUnit.getDefaultUnit());
	}

	public int currentTimeInt () {
		System.out.println(System.nanoTime() + " " + absoluteOffset);
		long time = TimeUnit.convert(System.nanoTime() - absoluteOffset, NANOSECONDS, TimeUnit.getDefaultUnit());
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
