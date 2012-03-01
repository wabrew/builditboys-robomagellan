package com.builditboys.robots.geometry;

public class GeometricCompare {
	
	//--------------------------------------------------------------------------------
	// For closeness comparisons

	public static final double DEFAULT_LENGTH_DELTA = 1.0E-6;
	public static final double DEFAULT_LENGTH_DELTA_SQ = DEFAULT_LENGTH_DELTA * DEFAULT_LENGTH_DELTA;
    public static final double DEFAULT_ANGLE_DELTA = 1.0E-6;
    
   //--------------------------------------------------------------------------------
   // slightly more numerically stable than the standard computation
 
    public static double sqrtSumSquares (double a1, double a2) {
    	double a1abs = Math.abs(a1);
    	double a2abs = Math.abs(a2);
    	double min, max;
    	
    	if (a1abs < a2abs) {
    		min = a1abs;
    		max = a2abs;
    	}
    	else {
    		min = a2abs;
    		max = a1abs;
    	}
    	double ratio = min/max;
    	return max * Math.sqrt(1.0 + ratio * ratio);
    }
    
	//--------------------------------------------------------------------------------
    // Closeness predicates
    
    public static boolean isLengthClose0 (double v1) {
    	return Math.abs(v1) <= DEFAULT_LENGTH_DELTA;
    }
    
    public static boolean isLengthClose (double v1, double v2) {
    	return Math.abs(v1 - v2) <= DEFAULT_LENGTH_DELTA;
    }
 
    public static boolean isLengthSQClose0 (double v1) {
    	return Math.abs(v1) <= DEFAULT_LENGTH_DELTA_SQ;
    }
    
    public static boolean isLengthSQClose (double v1, double v2) {
    	return Math.abs(v1 - v2) <= DEFAULT_LENGTH_DELTA_SQ;
    }
    
    public static boolean isAngleClose0 (double a1) {
    	return Math.abs(a1) <= DEFAULT_ANGLE_DELTA;
    }

    public static boolean isAngleClose (double a1, double a2) {
    	return Math.abs(a1 - a2) <= DEFAULT_ANGLE_DELTA;
    }

}
