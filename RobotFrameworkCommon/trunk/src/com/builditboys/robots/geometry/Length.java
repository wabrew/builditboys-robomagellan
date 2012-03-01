package com.builditboys.robots.geometry;

import static com.builditboys.robots.units.AngleUnits.DEGREES;
import static com.builditboys.robots.units.LengthUnits.*;

import com.builditboys.robots.units.AngleUnits;
import com.builditboys.robots.units.LengthUnits;


public class Length {
	
    // The dimensions value, and units
	private double lengthValue;
	
	private static final LengthUnits nativeUnits = METERS;
	
	private boolean isLocked;
	
	// consider caching values in other units
	

	//--------------------------------------------------------------------------------
	// Constructor (note that it is private, use factory methods)

   private Length (double val) {
    	lengthValue = val;
     	isLocked = false;
    }

	//--------------------------------------------------------------------------------
   	// Factory methods

   public static Length newLength (Length len) {
	   // assumes native units are all the same
	   return new Length(len.lengthValue);
   }
  
   public static Length newLength (double val, LengthUnits units) {
	   return new Length(units.convert(val, nativeUnits));
   }
   
	//--------------------------------------------------------------------------------
	// Basic Accessors
	
	public LengthUnits getNativeUnits() {
		return nativeUnits;
	}
	
	public double getInUnits(LengthUnits units) {
		if (units == nativeUnits) {
			return lengthValue;
		}
		else {
			return nativeUnits.convert(lengthValue, units);
		}
	}
	
	public void setInUnits(double val, LengthUnits units) {
		if (!isLocked) {
			if (units == nativeUnits) {
				lengthValue = val;
			}
			else {
				lengthValue = units.convert(val, nativeUnits);
			}
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public void lock() {
		isLocked = true;
	}
	
	public void unlock() {
		isLocked = false;
	}

	//--------------------------------------------------------------------------------
	// Some angle operators - note, they are static and make new angles

	public static Length add (Length len1, Length len2) {
		return new Length(len1.lengthValue + len2.lengthValue);
	}
	
	public static Length sub (Length len1, Length len2) {
		return new Length(len1.lengthValue - len2.lengthValue);
	}

	public static Length mul (Length len1, double multiplicand) {
		return new Length(len1.lengthValue * multiplicand);
	}

	public static Length div (Length len1, double divisor) {
		return new Length(len1.lengthValue * divisor);
	}

	//--------------------------------------------------------------------------------
	// Some other length operators - return properties of the length
	
	public boolean isClose0 () {
		return GeometricCompare.isLengthClose0(lengthValue);
	}

	public boolean isClose (Length a2) {
		return GeometricCompare.isLengthClose(lengthValue, a2.lengthValue);
	}

	//--------------------------------------------------------------------------------
	// Make printing a little easier
	
	public String toString () {
		return String.format("%.2f", lengthValue);
	}
	
	public String getDescription () {
		double val = lengthValue;
		// assumes native units are always METERS
		return String.format("Length: %.2f meters", val);
 	}
	
	public void describe () {
		System.out.println(getDescription());
	}
	
	//--------------------------------------------------------------------------------
	// Internals
	
	
  
}
