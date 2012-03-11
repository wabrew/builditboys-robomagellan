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

	private RobotControlActionEnum RobotControlAction;
	
	private static DistributionList distributionList;

	//--------------------------------------------------------------------------------

	private RobotControlNotification (RobotControlActionEnum action) {
		RobotControlAction = action;
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

	public static DistributionList getDistributionList () {
		return distributionList;
	}
	
	//--------------------------------------------------------------------------------

	public RobotControlActionEnum getRobotControlAction() {
		return RobotControlAction;
	}

	//--------------------------------------------------------------------------------

	public void publish (Object publishedBy) {
		publish(publishedBy, distributionList);
	}
	
	public void publishSelf(SubscriberInterface subscriber) {
		((RobotControlNotificationSubscriberInterface) subscriber).receiveNotification(this);
	}
	
}
