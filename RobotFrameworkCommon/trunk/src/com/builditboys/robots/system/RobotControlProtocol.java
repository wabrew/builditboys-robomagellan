package com.builditboys.robots.system;

import com.builditboys.robots.communication.AbstractLink;
import com.builditboys.robots.communication.AbstractProtocol;
import com.builditboys.robots.communication.AbstractProtocolMessage;
import com.builditboys.robots.communication.InputChannel;
import com.builditboys.robots.communication.LinkMessage;
import com.builditboys.robots.communication.OutputChannel;
import com.builditboys.robots.system.RobotState.EStopIndicatorEnum;
import com.builditboys.robots.system.RobotState.RobotModeEnum;
import com.builditboys.robots.time.LocalTimeSystem;
import com.builditboys.robots.utilities.FillableBuffer;
import static com.builditboys.robots.communication.LinkParameters.*;

public class RobotControlProtocol extends AbstractProtocol {

	private static final AbstractProtocol REPRESENTATIVE = new RobotControlProtocol();
	
	private static final int MY_CHANNEL_NUMBER = ROBOT_CONTROL_CHANNEL_NUMBER;
	
	RobotStateMessage robotStateMessage = new RobotStateMessage();
	
	// --------------------------------------------------------------------------------
	// Constructors

	private RobotControlProtocol() {
	}

	private RobotControlProtocol (ProtocolRoleEnum rol) {
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
		RobotControlProtocol iproto = new RobotControlProtocol(rol);
		RobotControlProtocol oproto = new RobotControlProtocol(rol);
		link.addProtocol(iproto, oproto);
	}
	
	// --------------------------------------------------------------------------------

	public static RobotControlProtocol getLinkInputProtocol (AbstractLink link) {
		return (RobotControlProtocol) link.getInputProtocol(REPRESENTATIVE);
	}
	
	public static RobotControlProtocol getLinkOutputProtocol (AbstractLink link) {
		return (RobotControlProtocol) link.getOutputProtocol(REPRESENTATIVE);
	}
	
	// --------------------------------------------------------------------------------
	// Robot Control Messages - keep in sync with the PSoC

	public static final int MS_SET_MODE         = 0; // set the robots mode
	public static final int MS_DO_ESTOP         = 1; // tell the robot to estop
	public static final int MS_CLEAR_ESTOP      = 2; // clear an estop situation
	public static final int MS_DO_RESET         = 3; // tell the robot to reset
	public static final int MS_DO_DUMP_STATE    = 4; // tell the robot to dump its state
	public static final int MS_IM_ALIVE         = 5; // tell the robot the master is alive
	
	public static final int SM_DID_ESTOP        = 6; // tell the master that an estop occured
	public static final int SM_HERE_IS_MY_STATE = 7; // the robots state
	public static final int SM_IM_ALIVE         = 8; // tell the master that the robot is alive
	
	public enum RobotControlMessageEnum {
		MASTER_SET_MODE(MS_SET_MODE),
		MASTER_DO_ESTOP(MS_DO_ESTOP),
		MASTER_CLEAR_ESTOP(MS_CLEAR_ESTOP),
		MASTER_DO_RESET(MS_DO_RESET),
		MASTER_DO_DUMP_STATE(MS_DO_DUMP_STATE),
		MASTER_IM_ALIVE(MS_IM_ALIVE),
		
		SLAVE_DID_ESTOP(SM_DID_ESTOP),
		SLAVE_HERE_IS_MY_STATE(SM_HERE_IS_MY_STATE),
		SLAVE_IM_ALIVE(SM_IM_ALIVE);
		
		private int messageNum;
		
		private RobotControlMessageEnum (int num) {
			messageNum = num;
		}
		
		private static void associateInverse (int num, RobotControlMessageEnum it) {
			NUM_TO_ENUM[num] = it;
		}
		
		private static final int LARGEST_NUM = SM_HERE_IS_MY_STATE;
		private static final RobotControlMessageEnum NUM_TO_ENUM[] = new RobotControlMessageEnum[LARGEST_NUM + 1];

		static {
			for (RobotControlMessageEnum num: values()) {
				associateInverse(num.messageNum, num);
			}
		}

		// use this to get the mode number for an enum
		public int getModeNum() {
			return messageNum;
		}

		// use this to map a mode number to its enum
		public static RobotControlMessageEnum numToEnum(int num) {
			if ((num > NUM_TO_ENUM.length) || (num < 0)) {
				throw new IndexOutOfBoundsException("num out of range");
			}
			return NUM_TO_ENUM[num];
		}
	}
	
	//--------------------------------------------------------------------------------
	// Sending messages -- master to slave
	
	public void sendSetMode (RobotModeEnum mode, boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.MASTER,
						new RobotControlMessage(MS_SET_MODE,
												mode.getModeNum()),
						doWait);
	}
	
	public void sendClearEstop (EStopIndicatorEnum indicator, boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.MASTER,
						new RobotControlMessage(MS_CLEAR_ESTOP,
												indicator.getIndicatorNum()),
						doWait);
	}

	public void sendDoRest (boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.MASTER,
						new RobotControlMessage(MS_DO_RESET),
						doWait);
	}
	
	public void sendDoDumpState (boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.MASTER,
						new RobotControlMessage(MS_DO_DUMP_STATE),
						doWait);
	}
	
	public void sendMasterIsAlive (boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.MASTER,
						new RobotControlMessage(MS_IM_ALIVE),
						doWait);
	}
	
	//--------------------------------------------------------------------------------
	// Sending messages -- slave to master

	public void sendDidEstop (boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.SLAVE,
						new RobotControlMessage(SM_DID_ESTOP),
						doWait);
	}
	
	public void sendHereIsMyState (boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.SLAVE,
						new RobotStateMessage(SM_HERE_IS_MY_STATE),
						doWait);
	}

	public void sendSlaveIsAlive (boolean doWait) throws InterruptedException {
		sendRoleMessage(ProtocolRoleEnum.SLAVE,
						new RobotControlMessage(SM_IM_ALIVE),
						doWait);
	}

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
		RobotControlNotification notice;
		switch (indicator) {
		
		case SM_DID_ESTOP:
			// just publish the event
			notice = RobotControlNotification.newEstopNotice();
			notice.publish(this);
			break;
			
		case SM_IM_ALIVE:
			// just publish the event
			notice = RobotControlNotification.newIsAliveNotice();
			notice.publish(this);
			break;

			
		case SM_HERE_IS_MY_STATE:
			// update the state and publish event
			robotStateMessage.reConstruct(message);
			RobotState.getInstance().updateState(robotStateMessage.time,
												 robotStateMessage.mode,
												 robotStateMessage.estop);
			notice = RobotControlNotification.newRobotStateNotice();
			notice.publish(this);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown robot control message: " + indicator);
		}
	}
	
	//--------------------------------------------------------------------------------
	// Receiving messages - Slave

	public void receiveSlaveMessage (LinkMessage message) {
		int indicator = message.peekByte();
		switch (indicator) {
		case MS_SET_MODE:
		case MS_DO_ESTOP:
		case MS_CLEAR_ESTOP:
		case MS_DO_RESET:
		case MS_DO_DUMP_STATE:
		case MS_IM_ALIVE:
			throw new IllegalArgumentException("Slave side robot control protocol not implimented");
		default:
			throw new IllegalArgumentException("Unknown robot control message: " + indicator);
		}
	}
	
	//--------------------------------------------------------------------------------
	// A class that models a robot control message
	
	private class RobotControlMessage extends AbstractProtocolMessage {
		int indicator = 0;
		int arguement = 0;
		
		// used to create an empty message to read into
		protected RobotControlMessage () {
			super(1 + 1);
		}

		protected RobotControlMessage (int ind) {
			super(1 + 1);
			indicator = ind;
		}
		
		protected RobotControlMessage (int ind, int arg) {
			super(1 + 1);
			indicator = ind;
			arguement = arg;
		}
				
		protected void deConstruct (FillableBuffer buff) {
			buff.deConstructBytes1(indicator);
			buff.deConstructBytes1(arguement);
		}
		
		protected void reConstruct (FillableBuffer buff) {
			indicator = buff.reConstructBytes1();
			arguement = buff.reConstructBytes1();
		}
	}

	//--------------------------------------------------------------------------------
	// A class that models a robot control message
	
	public class RobotStateMessage extends AbstractProtocolMessage {
		int indicator = 0;
		int time = 0;
		int mode = 0;
		int estop = 0;
		
		// used to create an empty message to read into
		protected RobotStateMessage () {
			super(1 + 1);
		}

		protected RobotStateMessage (int ind) {
			super(1 + 1);
			indicator = ind;
			time = LocalTimeSystem.currentTime();
			mode = 0;
			estop = 0;
			throw new IllegalStateException("java cannot do robot state");
		}
				
		protected void deConstruct (FillableBuffer buff) {
			buff.deConstructBytes1(indicator);
			buff.deConstructBytes4(time);
			buff.deConstructBytes1(mode);
			buff.deConstructBytes1(estop);
		}
		
		protected void reConstruct (FillableBuffer buff) {
			indicator = buff.reConstructBytes1();
			time = buff.reConstructBytes4();
			mode = buff.reConstructBytes1();
			estop = buff.reConstructBytes1();
		}
				
	}
	
}
