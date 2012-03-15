package com.builditboys.robots.system;

import com.builditboys.robots.infrastructure.AbstractNotification;
import com.builditboys.robots.infrastructure.DistributionList;
import com.builditboys.robots.infrastructure.SubscriberInterface;


public class RobotControlNotification  extends AbstractNotification {

	public enum RobotControlActionEnum {
		RECEIVED_ROBOT_DID_ESTOP,
		RECEIVED_ROBOT_IS_ALIVE,
		RECEIVED_ROBOT_STATE;
	}

	private RobotControlActionEnum controlAction;
	
	//--------------------------------------------------------------------------------

	private RobotControlNotification (RobotControlActionEnum action) {
		controlAction = action;
	}
	
	//--------------------------------------------------------------------------------

	private static final DistributionList DISTRIBUTION_LIST = new DistributionList("ROBOT_CONTROL_DISTRIBUTION_LIST", true);

	public static DistributionList getDistributionList () {
		return DISTRIBUTION_LIST;
	}
	
	//--------------------------------------------------------------------------------

	public static RobotControlNotification newEstopNotice () {
		return new RobotControlNotification(RobotControlActionEnum.RECEIVED_ROBOT_DID_ESTOP);
	}
	
	public static RobotControlNotification newIsAliveNotice () {
		return new RobotControlNotification(RobotControlActionEnum.RECEIVED_ROBOT_IS_ALIVE);
	}

	public static RobotControlNotification newRobotStateNotice () {
		return new RobotControlNotification(RobotControlActionEnum.RECEIVED_ROBOT_STATE);
	}
		
	//--------------------------------------------------------------------------------

	public RobotControlActionEnum getRobotControlAction() {
		return controlAction;
	}

	//--------------------------------------------------------------------------------

	public void publish (Object publishedBy) {
		publish(publishedBy, DISTRIBUTION_LIST);
	}
	
	public void publishSelf(SubscriberInterface subscriber) {
		((RobotControlNotificationSubscriberInterface) subscriber).receiveNotification(this);
	}
	
	//--------------------------------------------------------------------------------

	public String toString () {
		return "System Action: " + controlAction.toString();
	}

	
}
