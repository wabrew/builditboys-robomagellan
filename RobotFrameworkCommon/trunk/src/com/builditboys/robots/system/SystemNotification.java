package com.builditboys.robots.system;

import com.builditboys.robots.infrastructure.AbstractNotification;
import com.builditboys.robots.infrastructure.DistributionList;
import com.builditboys.robots.infrastructure.SubscriberInterface;

public class SystemNotification extends AbstractNotification {

	public enum SystemActionEnum {
		START1,
		START2,
		START3,
		ESTOP,
		NORMAL_STOP;
	}

	private SystemActionEnum systemAction;
	
	//--------------------------------------------------------------------------------

	private SystemNotification (SystemActionEnum action) {
		systemAction = action;
	}
	
	//--------------------------------------------------------------------------------
	
	private static final DistributionList DISTRIBUTION_LIST = new DistributionList("SYSTEM_DISTRIBUTION_LIST", true);

	public static DistributionList getDistributionList () {
		return DISTRIBUTION_LIST;
	}
	
	//--------------------------------------------------------------------------------

	public static SystemNotification newStart1Notification () {
		return new SystemNotification(SystemActionEnum.START1);
	}
	
	public static SystemNotification newStart2Notification () {
		return new SystemNotification(SystemActionEnum.START2);
	}
	
	public static SystemNotification newStart3Notification () {
		return new SystemNotification(SystemActionEnum.START3);
	}
	
	public static SystemNotification newEstopNotification () {
		return new SystemNotification(SystemActionEnum.ESTOP);
	}
	
	public static SystemNotification newStopNotification () {
		return new SystemNotification(SystemActionEnum.NORMAL_STOP);
	}

	//--------------------------------------------------------------------------------

	public SystemActionEnum getSystemAction() {
		return systemAction;
	}
	
	//--------------------------------------------------------------------------------

	public void publish (Object publishedBy) {
		publish(publishedBy, DISTRIBUTION_LIST);
	}
	
	public void publishSelf( SubscriberInterface subscriber) {
		((SystemNotificationSubscriberInterface) subscriber).receiveNotification(this);
	}
	
	//--------------------------------------------------------------------------------

	public String toString () {
		return "System Action: " + systemAction.toString();
	}
}
