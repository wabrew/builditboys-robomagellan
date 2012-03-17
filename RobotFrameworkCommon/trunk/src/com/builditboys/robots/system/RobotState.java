package com.builditboys.robots.system;

import com.builditboys.robots.infrastructure.ParameterInterface;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.utilities.BitBuffer;

public class RobotState implements ParameterInterface {
	
	//--------------------------------------------------------------------------------
	
	public static final int ROBOT_MODE__SAFE    = 0; // safe mode
	public static final int ROBOT_MODE__RC      = 1; // R/C mode
	public static final int ROBOT_MODE__AUTO    = 2; // autonomous mode
	public static final int ROBOT_MODE__UNKNOWN = 3; // unknown mode
	
	public enum RobotModeEnum {
		SAFE_MODE(ROBOT_MODE__SAFE),
		RC_MODE(ROBOT_MODE__RC),
		AUTONOMOUS_MODE(ROBOT_MODE__AUTO),
		UNKNOWN(ROBOT_MODE__UNKNOWN);
		
		private int modeNum;
		
		private RobotModeEnum (int num) {
			modeNum = num;
		}
		
		private static void associateInverse (int num, RobotModeEnum it) {
			NUM_TO_ENUM[num] = it;
		}
		
		private static final int LARGEST_NUM = ROBOT_MODE__UNKNOWN;
		private static final RobotModeEnum NUM_TO_ENUM[] = new RobotModeEnum[LARGEST_NUM + 1];

		static {
			for (RobotModeEnum mode: values()) {
				associateInverse(mode.modeNum, mode);
			}
		}
		
		// use this to get the mode number for an enum
		public int getModeNum() {
			return modeNum;
		}

		// use this to map a mode number to its enum
		public static RobotModeEnum numToEnum(int num) {
			if ((num > NUM_TO_ENUM.length) || (num < 0)) {
				throw new IndexOutOfBoundsException("num out of range");
			}
			return NUM_TO_ENUM[num];
		}
	}
	
	//--------------------------------------------------------------------------------
		
	public class EStopBits extends BitBuffer {
		
		EStopBits (byte theBits) {
			super(theBits);
		}
	
		// keep these in sync with the PSoC
		public static final int ESTOP_BIT                 = 0;

		public static final int SOFT_ESTOP_BIT            = 1;

		public static final int HARD_ESTOP_ACTIVE_BIT     = 2;
		public static final int HARD_ESTOP_LATCH_BIT      = 3;

		public static final int PULSE_ESTOP_ACTIVE_BIT    = 4;
		public static final int PULSE_ESTOP_LATCH_BIT     = 5;

		public static final int DEAD_MAN_ESTOP_ACTIVE_BIT = 6;	
		public static final int DEAD_MAN_ESTOP_LATCH_BIT  = 7;	
		
	}
	
	public enum EStopBitsEnum {
		ESTOP(EStopBits.ESTOP_BIT),
		SOFT_ESTOP(EStopBits.SOFT_ESTOP_BIT),
		HARD_ESTOP_ACTIVE(EStopBits.HARD_ESTOP_ACTIVE_BIT),
		HARD_EDTOP_LATCH(EStopBits.HARD_ESTOP_LATCH_BIT),
		PULSE_ESTOP_ACTIVE(EStopBits.PULSE_ESTOP_ACTIVE_BIT),
		PULSE_ESTOP_LATCH(EStopBits.PULSE_ESTOP_LATCH_BIT),
		DEAD_MAN_ESTOP_ACTIVE(EStopBits.DEAD_MAN_ESTOP_ACTIVE_BIT),
		DEAD_MAN_ESTOP_LATCH(EStopBits.DEAD_MAN_ESTOP_LATCH_BIT);
		
		private int bitNum;
		
		private  EStopBitsEnum (int num) {
			bitNum = num;
		}
		
		private static void associateInverse (int num, EStopBitsEnum it) {
			NUM_TO_ENUM[num] = it;
		}
		
		private static final int LARGEST_NUM = EStopBits.DEAD_MAN_ESTOP_LATCH_BIT;
		private static final EStopBitsEnum NUM_TO_ENUM[] = new EStopBitsEnum[LARGEST_NUM + 1];

		static {
			for (EStopBitsEnum bnum: values()) {
				associateInverse(bnum.bitNum, bnum);
			}
		}

		// use this to get the mode number for an enum
		public int getIndicatorNum() {
			return bitNum;
		}

		// use this to map a mode number to its enum
		public static EStopBitsEnum numToEnum(int num) {
			if ((num > NUM_TO_ENUM.length) || (num < 0)) {
				throw new IndexOutOfBoundsException("num out of range");
			}
			return NUM_TO_ENUM[num];
		}
		
		public boolean testBit(EStopBits bits) {
			return bits.testBit(bitNum);
		}
		
		public void setBit(EStopBits bits) {
			bits.setBit(bitNum);
		}
		
		public void clearBit(EStopBits bits) {
			bits.clearBit(bitNum);
		}
		
		public static void printBits (EStopBits bits) {
			for (EStopBitsEnum bit: values()) {
				if (bit.testBit(bits)) {
					System.out.print(bit + " ");
				}
			}
		}

	}

	
	//--------------------------------------------------------------------------------

	public static final int SOFT_ESTOP_INDICATOR     = 0;
	public static final int HARD_ESTOP_INDICATOR     = 1;	
	public static final int PULSE_ESTOP_INDICATOR    = 2;
	public static final int DEAD_MAN_ESTOP_INDICATOR = 3;

	public enum EStopIndicatorEnum {
		SOFT_ESTOP(SOFT_ESTOP_INDICATOR),
		HARD_ESTOP(HARD_ESTOP_INDICATOR),
		PULSE_ESTOP(PULSE_ESTOP_INDICATOR),
		DEAD_MAN_ESTOP(DEAD_MAN_ESTOP_INDICATOR);
		
		private int indicatorNum;
		
		private  EStopIndicatorEnum (int num) {
			indicatorNum = num;
		}
		
		private static void associateInverse (int num, EStopIndicatorEnum it) {
			NUM_TO_ENUM[num] = it;
		}
		
		private static final int LARGEST_NUM = DEAD_MAN_ESTOP_INDICATOR;
		private static final EStopIndicatorEnum NUM_TO_ENUM[] = new EStopIndicatorEnum[LARGEST_NUM + 1];

		static {
			for (EStopIndicatorEnum indicator: values()) {
				associateInverse(indicator.indicatorNum, indicator);
			}
		}

		// use this to get the mode number for an enum
		public int getIndicatorNum() {
			return indicatorNum;
		}

		// use this to map a mode number to its enum
		public static EStopIndicatorEnum numToEnum(int num) {
			if ((num > NUM_TO_ENUM.length) || (num < 0)) {
				throw new IndexOutOfBoundsException("num out of range");
			}
			return NUM_TO_ENUM[num];
		}
	}
	

	//--------------------------------------------------------------------------------
	// State variables
	
	int captureTime = 0;   // local time
	
	RobotModeEnum mode = RobotModeEnum.UNKNOWN;
	
	EStopBits eStopByte = new EStopBits((byte) 0);
	
	String name;

	//--------------------------------------------------------------------------------

	public RobotState (String nm) {
		name = nm;
	}
		
	//--------------------------------------------------------------------------------
	
	public String getName () {
		return name;
	}
	
	// --------------------------------------------------------------------------------

	public static RobotState getParameter (String key) {
		return (RobotState) ParameterServer.getParameter(key);
	}
	
	public static RobotState maybeGetParameter (String key) {
		return (RobotState) ParameterServer.maybeGetParameter(key);
	}
	
	//--------------------------------------------------------------------------------

	public synchronized void updateState (int time, int modeNum, int eStop) {
		captureTime = time;
		mode = RobotModeEnum.numToEnum(modeNum);
		eStopByte.setValue(eStop);
	}
	
	//--------------------------------------------------------------------------------
	
	public void print () {
		System.out.println("  Time: " + captureTime);
		System.out.println("  Mode: " + mode);
		System.out.print("  Estop: ");
		EStopBitsEnum.printBits(eStopByte);
		System.out.println();
	}
	
	//--------------------------------------------------------------------------------

}
