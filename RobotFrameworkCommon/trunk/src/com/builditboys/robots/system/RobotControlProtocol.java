package com.builditboys.robots.system;

import com.builditboys.robots.communication.AbstractLink;
import com.builditboys.robots.communication.AbstractProtocol;
import com.builditboys.robots.communication.AbstractProtocolMessage;
import com.builditboys.robots.communication.InputChannel;
import com.builditboys.robots.communication.LinkMessage;
import com.builditboys.robots.communication.OutputChannel;
import com.builditboys.robots.infrastructure.AbstractNotification;
import com.builditboys.robots.infrastructure.DistributionList;
import com.builditboys.robots.infrastructure.ParameterInterface;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.utilities.FillableBuffer;
import static com.builditboys.robots.communication.LinkParameters.*;

public class RobotControlProtocol extends AbstractProtocol {

	public static AbstractProtocol indicator = new RobotControlProtocol();
	
	private static int myChannelNumber = ROBOT_CONTROL_CHANNEL_NUMBER;
	
	// --------------------------------------------------------------------------------
	// Constructors

	private RobotControlProtocol() {
	}

	public RobotControlProtocol (ProtocolRoleEnum rol) {
		role = rol;
	}
	
	//--------------------------------------------------------------------------------
	// Channel factories
	
	public InputChannel getInputChannel () {
		channel = new InputChannel(this, myChannelNumber);
		return (InputChannel) channel;
	}
	
	public OutputChannel getOutputChannel () {
		channel = new OutputChannel(this, myChannelNumber);
		return (OutputChannel) channel;
	}
	
	// --------------------------------------------------------------------------------

	public AbstractProtocol getIndicator() {
		return indicator;
	}

	// --------------------------------------------------------------------------------

	public static void addProtocolToLink (AbstractLink link, ProtocolRoleEnum rol) {
		RobotControlProtocol iproto = new RobotControlProtocol(rol);
		RobotControlProtocol oproto = new RobotControlProtocol(rol);
		link.addProtocol(iproto, oproto);
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
	public static final int SM_IM_ALIVE         = 7; // tell the master that the robot is alive
	public static final int SM_HERE_IS_MY_STATE = 8; // the robots state
	
	
	
	public static final int ROBOT_MODE__SAFE   = 0; // safe mode
	public static final int ROBOT_MODE__RC     = 1; // R/C mode
	public static final int ROBOT_MODE__AUTO   = 2; // autonomous mode
	
	public static final int ROBOT_HARD_ESTOP     = 0;
	public static final int ROBOT_SOFT_ESTOP     = 1;
	public static final int ROBOT_PULSE_ESTOP    = 2;
	public static final int ROBOT_TIMEOUT1_ESTOP = 3;
	public static final int ROBOT_TIMEOUT2_ESTOP = 4;
	

	//--------------------------------------------------------------------------------
	// Sending messages
	
	// fix this
	public void sendSetMode (boolean doWait) throws InterruptedException {
		if (role != ProtocolRoleEnum.MASTER) {
			throw new IllegalStateException();
		}
		RobotControlMessage mObject = new RobotControlMessage(MS_SET_MODE);
		sendMessage(mObject, doWait);
	}

	// master to slave
//	public static final int MS_DO_ESTOP         = 1; // tell the robot to estop
//	public static final int MS_CLEAR_ESTOP      = 2; // clear an estop situation
//	public static final int MS_DO_RESET         = 3; // tell the robot to reset
//	public static final int MS_DO_DUMP_STATE    = 4; // tell the robot to dump its state
//	public static final int MS_IM_ALIVE         = 5; // tell the robot the master is alive

	// slave to master
//	public static final int SM_DID_ESTOP        = 6; // tell the master that an estop occured
//	public static final int SM_IM_ALIVE         = 7; // tell the master that the robot is alive
//	public static final int SM_HERE_IS_MY_STATE = 8; // the robots state

	//--------------------------------------------------------------------------------
	// Receiving messages

	public void receiveMessage (LinkMessage message) throws InterruptedException {
		switch (role) {
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
			notice = RobotControlNotification.newEstopNotice();
			notice.publish(this);
			break;
			
		case SM_IM_ALIVE:
			notice = RobotControlNotification.newIsAliveNotice();
			notice.publish(this);
			break;

			
		case SM_HERE_IS_MY_STATE:
			RobotStateMessage messageObject2 = new RobotStateMessage();
			messageObject2.reConstruct(message);
			// could also just update the existing state object
			ParameterServer.replaceParameter(messageObject2);
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
		RobotControlMessage () {
			super(1 + 1);
		}

		RobotControlMessage (int ind) {
			super(1 + 1);
			indicator = ind;
		}
		
		RobotControlMessage (int ind, int arg) {
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
	
	public class RobotStateMessage extends AbstractProtocolMessage
								   implements ParameterInterface {
		int indicator = 0;
		int time = 0;
		int mode = 0;
		int estop = 0;
		int speedControl = 0;
		
		// used to create an empty message to read into
		RobotStateMessage () {
			super(1 + 1);
		}

		RobotStateMessage (int ind, int tim, int amode, int aestop, int aspeedControl) {
			super(1 + 1);
			indicator = ind;
			time = tim;
			mode = amode;
			estop = aestop;
			speedControl = aspeedControl;
		}
				
		protected void deConstruct (FillableBuffer buff) {
			buff.deConstructBytes1(indicator);
			buff.deConstructBytes4(time);
			buff.deConstructBytes1(mode);
			buff.deConstructBytes1(estop);
			buff.deConstructBytes1(speedControl);
		}
		
		protected void reConstruct (FillableBuffer buff) {
			indicator = buff.reConstructBytes1();
			time = buff.reConstructBytes4();
			mode = buff.reConstructBytes1();
			estop = buff.reConstructBytes1();
			speedControl = buff.reConstructBytes1();
		}
		
		public String getName () {
			return "Robot State";
		}
		
	}
	
}
