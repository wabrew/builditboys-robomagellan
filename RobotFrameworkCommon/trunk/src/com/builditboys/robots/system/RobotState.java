package com.builditboys.robots.system;

import com.builditboys.robots.infrastructure.ParameterInterface;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.utilities.BitBuffer;

public class RobotState implements ParameterInterface {
	
	//--------------------------------------------------------------------------------
	
	public static final int ROBOT_MODE__SAFE   = 0; // safe mode
	public static final int ROBOT_MODE__RC     = 1; // R/C mode
	public static final int ROBOT_MODE__AUTO   = 2; // autonomous mode
	
	public enum RobotModeEnum {
		SAFE_MODE(ROBOT_MODE__SAFE),
		RC_MODE(ROBOT_MODE__RC),
		AUTONOMOUS_MODE(ROBOT_MODE__AUTO);
		
		private int modeNum;
		
		private RobotModeEnum (int num) {
			modeNum = num;
			associateInverse(modeNum, this);
		}
		
		private static void associateInverse (int num, RobotModeEnum it) {
			NUM_TO_ENUM[num] = it;
		}
		
		private static final int LARGEST_NUM = ROBOT_MODE__AUTO;
		private static final RobotModeEnum NUM_TO_ENUM[] = new RobotModeEnum[LARGEST_NUM];

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
		public static final int ESTOP                 = 0;

		public static final int SOFT_ESTOP            = 1;

		public static final int HARD_ESTOP_ACTIVE     = 2;
		public static final int HARD_ESTOP_LATCH      = 3;

		public static final int PULSE_ESTOP_ACTIVE    = 4;
		public static final int PULSE_ESTOP_LATCH     = 5;

		public static final int DEAD_MAN_ESTOP_ACTIVE = 6;	
		public static final int DEAD_MAN_ESTOP_LATCH  = 7;	
		
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
			associateInverse(indicatorNum, this);
		}
		
		private static void associateInverse (int num, EStopIndicatorEnum it) {
			NUM_TO_ENUM[num] = it;
		}
		
		private static final int LARGEST_NUM = DEAD_MAN_ESTOP_INDICATOR;
		private static final EStopIndicatorEnum NUM_TO_ENUM[] = new EStopIndicatorEnum[LARGEST_NUM];

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
	
	int updateTime = 0;   // local time
	RobotModeEnum mode = RobotModeEnum.SAFE_MODE;
	EStopBits eStopByte = new EStopBits((byte) 0);
	
	private static final RobotState INSTANCE;
	
	// create the singleton and add it to the parameter server
	static {
		RobotState state = new RobotState();
		ParameterServer.addParameter(state);
		INSTANCE = state;
	}
	
	//--------------------------------------------------------------------------------

	private RobotState () {
	}
	
	//--------------------------------------------------------------------------------

	public static RobotState getInstance () {
		return INSTANCE;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getName () {
		return "ROBOT_STATE";
	}
	
	// --------------------------------------------------------------------------------

	public static RobotState getParameter (String key) {
		return (RobotState) ParameterServer.getParameter(key);
	}
	
	public static RobotState maybeGetParameter (String key) {
		return (RobotState) ParameterServer.getParameter(key);
	}
	
	//--------------------------------------------------------------------------------

	public synchronized void updateState (int time, int modeNum, int eStop) {
		updateTime = time;
		mode = RobotModeEnum.numToEnum(modeNum);
		eStopByte.setValue(eStop);
	}
	
	
	//--------------------------------------------------------------------------------

}
