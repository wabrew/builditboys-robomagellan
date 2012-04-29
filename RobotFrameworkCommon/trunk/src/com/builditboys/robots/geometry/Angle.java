package com.builditboys.robots.geometry;

import static com.builditboys.misc.units.AngleUnits.*;
import com.builditboys.misc.units.AngleUnits;

// Angle objects model angles.  They are lockable.  For efficiency, they cache
// their trig functions.   Static methods are provided for making angles in various 
// ways -- either from primitive types or by operating on an existing angle.

public final class Angle {

	// The angle value and units
	private double angleValue;

	private static final AngleUnits NATIVE_UNITS = RADIAN;

	private boolean isLocked;

	// Cached results of the main trig functions, could also cache degrees but
	// probably not worth it
	private double sin;
	private double cos;
	private double tan;

	private boolean sinOk = false;
	private boolean cosOk = false;
	private boolean tanOk = false;

	// --------------------------------------------------------------------------------
	// Constructor (note that it is private, use factory methods)

	private Angle(double val) {
		angleValue = val;
		isLocked = false;
	}

	// --------------------------------------------------------------------------------
	// Creation methods

	public static Angle newAngle(Angle angle) {
		// assumes native units are all the same
		return new Angle(angle.angleValue);
	}

	public static Angle newAngle(double val, AngleUnits units) {
		return new Angle(convert(val, units, NATIVE_UNITS));
	}

	public static Angle newAngleSin(double sin) {
		return new Angle(Math.asin(sin));
	}

	public static Angle newAngleCos(double cos) {
		return new Angle(Math.acos(cos));
	}

	public static Angle newAngleTan(double tan) {
		return new Angle(Math.atan(tan));
	}

	public static Angle newAngleTan2(double x, double y) {
		return new Angle(Math.atan2(y, x));
	}

	// --------------------------------------------------------------------------------
	// Basic Accessors

	public AngleUnits getNativeUnits() {
		return NATIVE_UNITS;
	}

	public double getInRadians() {
		// return nativeUnits.convert(angleValue, RADIANS);
		// assumes native units are always RADIANS
		return angleValue;
	}

	public double getInDegrees() {
		return convert(angleValue, NATIVE_UNITS, DEGREE);
	}

	public double getInUnits(AngleUnits units) {
		if (units == NATIVE_UNITS) {
			return angleValue;
		} else {
			return convert(angleValue, NATIVE_UNITS, units);
		}
	}

	public void setInRadians(double val) {
		if (!isLocked) {
			// assumes native units are always RADIANS
			angleValue = val;
			decache();
		} else {
			throw new IllegalStateException();
		}
	}

	public void setInDegrees(double val) {
		if (!isLocked) {
			angleValue = convert(val, DEGREE, NATIVE_UNITS);
			decache();
		} else {
			throw new IllegalStateException();
		}
	}

	public void setInUnits(double val, AngleUnits units) {
		if (!isLocked) {
			if (units == NATIVE_UNITS) {
				angleValue = val;
			} else {
				setInRadians(convert(val, units, RADIAN));
			}
		} else {
			throw new IllegalStateException();
		}
	}

	// --------------------------------------------------------------------------------
	// Some angle operators - note, they are static and make new angles

	public static Angle add(Angle a1, Angle a2) {
		return new Angle(a1.angleValue + a2.angleValue);
	}

	public static Angle sub(Angle a1, Angle a2) {
		return new Angle(a1.angleValue - a2.angleValue);
	}

	public static Angle add90(Angle a1) {
		return Angle.add(a1, ANGLE_90);
	}

	public static Angle add180(Angle a1) {
		return Angle.add(a1, ANGLE_180);
	}

	public static Angle sub90(Angle a1) {
		return Angle.sub(a1, ANGLE_90);
	}

	public static Angle sub180(Angle a1) {
		return Angle.sub(a1, ANGLE_180);
	}

	public static Angle mul(Angle a1, double multiplicand) {
		return new Angle(a1.angleValue * multiplicand);
	}

	public static Angle div(Angle a1, double divisor) {
		return new Angle(a1.angleValue / divisor);
	}

	public static Angle normalize(Angle a1) {
		return new Angle(a1.angleValue % Math.PI);
	}

	// --------------------------------------------------------------------------------
	// Some angle properties - return properties of the angle

	public double getSin() {
		if (!sinOk) {
			sin = Math.sin(angleValue);
			sinOk = true;
		}
		return sin;
	}

	public double getCos() {
		if (!cosOk) {
			cos = Math.cos(angleValue);
			cosOk = true;
		}
		return cos;
	}

	public double getTan() {
		if (!tanOk) {
			tan = Math.tan(angleValue);
			tanOk = true;
		}
		return tan;
	}

	public boolean isClose0() {
		return GeometricCompare.isAngleClose0(angleValue);
	}

	public boolean isClose(Angle a2) {
		return GeometricCompare.isAngleClose(angleValue, a2.angleValue);
	}

	// --------------------------------------------------------------------------------
	// COnstants for some common angles

	public static final Angle ANGLE_0 = new Angle(0.0);
	public static final Angle ANGLE_30 = new Angle(Math.PI / 6.0);
	public static final Angle ANGLE_45 = new Angle(Math.PI / 4.0);
	public static final Angle ANGLE_60 = new Angle(Math.PI / 3.0);
	public static final Angle ANGLE_90 = new Angle(Math.PI / 2.0);
	public static final Angle ANGLE_180 = new Angle(Math.PI);
	public static final Angle ANGLE_270 = new Angle(Math.PI * 1.5);
	public static final Angle ANGLE_360 = new Angle(Math.PI * 2.0);

	public static final Angle ANGLE_M_30 = new Angle(-Math.PI / 6.0);
	public static final Angle ANGLE_M_45 = new Angle(-Math.PI / 4.0);
	public static final Angle ANGLE_M_60 = new Angle(-Math.PI / 3.0);
	public static final Angle ANGLE_M_90 = new Angle(-Math.PI / 2.0);
	public static final Angle ANGLE_M_180 = new Angle(-Math.PI);
	public static final Angle ANGLE_M_270 = new Angle(-Math.PI * 1.5);
	public static final Angle ANGLE_M_360 = new Angle(-Math.PI * 2.0);

	// --------------------------------------------------------------------------------
	// Make printing a little easier

	public String toString() {
		return String.format("%.2f", angleValue);
	}

	public String getDescription() {
		return String.format("Angle: %.2f radians (%.2f degrees)",
							 convert(angleValue, NATIVE_UNITS, RADIAN),
							 convert(angleValue, NATIVE_UNITS, DEGREE));
	}

	public void describe() {
		System.out.println(getDescription());
	}

	// --------------------------------------------------------------------------------
	// Internals

	// Utility to decache trig info (not currently used)
	private void decache() {
		sinOk = false;
		cosOk = false;
		tanOk = false;
	}

}
