package com.builditboys.robots.geometry;

public class CoordinateSystem {
	
	private CoordinateSystem baseSystem;
	
	private Length relativeXOffset;
	private Length relativeYOffset;
	private Length relativeXSize;
	private Length relativeYSize;
	private Angle relativeTheta;
	
	private Length absoluteXOffset;
	private Length absoluteYOffset;
	private Length absoluteXSize;
	private Length absoluteYSize;
	private Angle absoluteTheta;
	
//	public static final CoordinateSystem baseCoordinateSystem; //= new CoordinateSystem();

	//--------------------------------------------------------------------------------
	// Constructors
	
	public CoordinateSystem (CoordinateSystem baseCoor,
					  		 Length xOff,
					  		 Length yOff,
					  		 Length xSize,
					  		 Length ySize,
					  		 Angle theta) {
		relativeXOffset = xOff;
		relativeYOffset = yOff;
		relativeXSize = xSize;
		relativeYSize = ySize;
		relativeTheta = theta;
	}
	
	//--------------------------------------------------------------------------------
	// Getters and Setter
	
	public CoordinateSystem getBaseSystem() {
		return baseSystem;
	}

	public Length getRelativeXOffset() {
		return relativeXOffset;
	}

	public Length getRelativeYOffset() {
		return relativeYOffset;
	}

	public Length getRelativeXSize() {
		return relativeXSize;
	}

	public Length getRelativeYSize() {
		return relativeYSize;
	}

	public Angle getRelativeTheta() {
		return relativeTheta;
	}

	public Length getAbsoluteXOffset() {
		return absoluteXOffset;
	}

	public Length getAbsoluteYOffset() {
		return absoluteYOffset;
	}

	public Length getAbsoluteXSize() {
		return absoluteXSize;
	}

	public Length getAbsoluteYSize() {
		return absoluteYSize;
	}

	public Angle getAbsoluteTheta() {
		return absoluteTheta;
	}

	//--------------------------------------------------------------------------------
	// Some operations
	
	public void Translate (Length newXOff, Length newYOff) {
		
	}

	public void Scale (Length newXSize, Length newYSize) {
		
	}

	public void Rotate (Angle newTheta) {
		
	}

	//--------------------------------------------------------------------------------
	// Updating absolute coordinate info
	
	private void updateAbsolutes () {
		baseSystem.updateAbsolutes();
		updateMyAbsolutes();
	}
	
	private void updateMyAbsolutes () {
//		absoluteXOffset = baseSystem.absoluteXOffset + relativeXOffset;
		
	}
}
