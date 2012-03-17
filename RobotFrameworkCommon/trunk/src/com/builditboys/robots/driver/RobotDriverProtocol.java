package com.builditboys.robots.driver;

import com.builditboys.robots.communication.AbstractLink;
import com.builditboys.robots.communication.AbstractProtocol;
import com.builditboys.robots.communication.AbstractProtocolMessage;
import com.builditboys.robots.communication.InputChannel;
import com.builditboys.robots.communication.LinkMessage;
import com.builditboys.robots.communication.OutputChannel;
import com.builditboys.robots.utilities.FillableBuffer;
import com.builditboys.robots.utilities.MiscUtilities;

import static com.builditboys.robots.communication.LinkParameters.*;

public class RobotDriverProtocol extends AbstractProtocol {

	private static final AbstractProtocol REPRESENTATIVE = new RobotDriverProtocol();
	
	private static final int MY_CHANNEL_NUMBER = ROBOT_DRIVER_CHANNEL_NUMBER;
		
	// --------------------------------------------------------------------------------
	// Constructors -- you don't construct a protocol directly, use addProtocolToLink

	// for the indicator
	private RobotDriverProtocol() {
	}

	// for the real protocol objects
	private RobotDriverProtocol (ProtocolRoleEnum rol) {
		protocolRole = rol;
	}
	
	//--------------------------------------------------------------------------------
	// Channel factories
	
	public InputChannel getInputChannel () {
		channel = new InputChannel(this, MY_CHANNEL_NUMBER);
		return (InputChannel) channel;
	}
	
	public OutputChannel getOutputChannel () {
		channel = new OutputChannel(this, MY_CHANNEL_NUMBER);
		return (OutputChannel) channel;
	}
	
	// --------------------------------------------------------------------------------

	public static AbstractProtocol getRepresentative() {
		return REPRESENTATIVE;
	}
	
	public AbstractProtocol getInstanceRepresentative() {
		return REPRESENTATIVE;
	}

	// --------------------------------------------------------------------------------

	public static void addProtocolToLink (AbstractLink link, ProtocolRoleEnum rol) {
		RobotDriverProtocol iproto = new RobotDriverProtocol(rol);
		RobotDriverProtocol oproto = new RobotDriverProtocol(rol);
		link.addProtocol(iproto, oproto);
	}
	
	// --------------------------------------------------------------------------------

	public static RobotDriverProtocol getLinkInputProtocol (AbstractLink link) {
		return (RobotDriverProtocol) link.getInputProtocol(REPRESENTATIVE);
	}
	
	public static RobotDriverProtocol getLinkOutputProtocol (AbstractLink link) {
		return (RobotDriverProtocol) link.getOutputProtocol(REPRESENTATIVE);
	}
	
	// --------------------------------------------------------------------------------
	// Robot Driver Messages - keep in sync with the PSoC

	public static final int MS_DRIVE        = 0; // speed, acceleration
	public static final int MS_STOP         = 1; // acceleration
	public static final int MS_MOVE         = 2; // distance, speed, acceleration
	public static final int MS_STEER        = 4; // angle, rate
	
	public static final int MS_SET_BUMP_MODE    = 5; // true/false
	public static final int MS_SET_RANGING_MODE = 6; // true/false
	
	
	public enum RobotDriverMessageEnum {
		DRIVE(MS_DRIVE),
		STOP(MS_STOP),
		MOVE(MS_MOVE),
		STEER(MS_STEER),
		SET_BUMP(MS_SET_BUMP_MODE),
		SET_RANGING(MS_SET_RANGING_MODE);
		
		private int messageNum;
		
		private RobotDriverMessageEnum (int num) {
			messageNum = num;
		}
		
		private static void associateInverse (int num, RobotDriverMessageEnum it) {
			NUM_TO_ENUM[num] = it;
		}
		
		private static final int LARGEST_NUM = MS_SET_RANGING_MODE;
		private static final RobotDriverMessageEnum NUM_TO_ENUM[] = new RobotDriverMessageEnum[LARGEST_NUM + 1];

		static {
			for (RobotDriverMessageEnum num: values()) {
				associateInverse(num.messageNum, num);
			}
		}

		// use this to get the mode number for an enum
		public int getModeNum() {
			return messageNum;
		}

		// use this to map a mode number to its enum
		public static RobotDriverMessageEnum numToEnum(int num) {
			if ((num > NUM_TO_ENUM.length) || (num < 0)) {
				throw new IndexOutOfBoundsException("num out of range");
			}
			return NUM_TO_ENUM[num];
		}
	}
	
	//--------------------------------------------------------------------------------
	// Sending messages -- master to slave
	
	public void sendDrive (int speed, int acceleration, boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.MASTER,
						new RobotDriverMessage(MS_DRIVE, speed, acceleration),
						doWait);
	}
	
	public void sendStop (int acceleration, boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.MASTER,
						new RobotDriverMessage(MS_DRIVE, acceleration),
						doWait);
	}

	public void sendMove (int distance, int speed, int acceleration, boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.MASTER,
						new RobotDriverMessage(MS_DRIVE, distance, speed, acceleration),
						doWait);
	}

	public void sendSteer (int angle, int rate, boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.MASTER,
						new RobotDriverMessage(MS_DRIVE, angle, rate),
						doWait);
	}

	public void sendSetBump (boolean mode, boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.MASTER,
						new RobotDriverMessage(MS_DRIVE, MiscUtilities.booleanToInt(mode)),
						doWait);
	}

	public void sendSetRanging (boolean mode, boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.MASTER,
						new RobotDriverMessage(MS_DRIVE, MiscUtilities.booleanToInt(mode)),
						doWait);
	}

	//--------------------------------------------------------------------------------
	// Sending messages -- slave to master

	// There are no driver messages from slave to master
	
	//--------------------------------------------------------------------------------
	// Receiving messages

	public void receiveMessage (LinkMessage message) throws InterruptedException {
		switch (protocolRole) {
		case MASTER:
			receiveMasterMessage(message);
			break;			
		case SLAVE:
			receiveSlaveMessage(message);
			break;		
		default:
			throw new IllegalStateException("Protocol role is incorrect");
		}
	}
	
	//--------------------------------------------------------------------------------
	// Receiving messages - Master
	
	public void receiveMasterMessage (LinkMessage message) throws InterruptedException {
		int indicator = message.peekByte();
		switch (indicator) {
		default:
			throw new IllegalArgumentException("Unknown robot driver message: " + indicator);
		}
	}
	
	//--------------------------------------------------------------------------------
	// Receiving messages - Slave

	public void receiveSlaveMessage (LinkMessage message) {
		int indicator = message.peekByte();
		switch (indicator) {
		default:
			throw new IllegalArgumentException("Unknown robot driver message: " + indicator);
		}
	}
	
	//--------------------------------------------------------------------------------
	// A class that models a robot driver message
	
	private class RobotDriverMessage extends AbstractProtocolMessage {
		int indicator = 0;
		int arguement1 = 0;
		int arguement2 = 0;
		int arguement3 = 0;
		
		// used to create an empty message to read into
		protected RobotDriverMessage () {
			super(1 + 1);
		}

		protected RobotDriverMessage (int ind) {
			super(1 + 1);
			indicator = ind;
		}
		
		protected RobotDriverMessage (int ind, int arg1) {
			super(1 + 1);
			indicator = ind;
			arguement1 = arg1;
		}
				
		protected RobotDriverMessage (int ind, int arg1, int arg2) {
			super(1 + 1);
			indicator = ind;
			arguement1 = arg1;
			arguement2 = arg2;
		}
		
		protected RobotDriverMessage (int ind, int arg1, int arg2, int arg3) {
			super(1 + 1);
			indicator = ind;
			arguement1 = arg1;
			arguement2 = arg2;
			arguement3 = arg3;
		}

		protected void deConstruct (FillableBuffer buff) {
			buff.deConstructBytes1(indicator);
			buff.deConstructBytes1(arguement1);
			buff.deConstructBytes1(arguement2);
			buff.deConstructBytes1(arguement3);
		}
		
		protected void reConstruct (FillableBuffer buff) {
			indicator = buff.reConstructBytes1();
			arguement1 = buff.reConstructBytes1();
			arguement2 = buff.reConstructBytes1();
			arguement3 = buff.reConstructBytes1();
		}
	}

	
}

