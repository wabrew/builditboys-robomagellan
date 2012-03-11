package com.builditboys.robots.system;

import com.builditboys.robots.infrastructure.AbstractNotification;
import com.builditboys.robots.infrastructure.DistributionList;
import com.builditboys.robots.infrastructure.SubscriberInterface;

public class SystemNotification extends AbstractNotification {

	public enum SystemActionEnum {
		START1,
		START2,
		ESTOP,
		NORMAL_STOP;
	}

	private SystemActionEnum systemAction;
	
	private static DistributionList distributionList = DistributionList.addDistributionListNamed("system distribution list");

	//--------------------------------------------------------------------------------

	private SystemNotification (SystemActionEnum action) {
		systemAction = action;
	}
	
	//--------------------------------------------------------------------------------

	public static SystemNotification newStart1Notification () {
		return new SystemNotification(SystemActionEnum.START1);
	}
	
	public static SystemNotification newStart2Notification () {
		return new SystemNotification(SystemActionEnum.START2);
	}
	
	public static SystemNotification newEstopNotification () {
		return new SystemNotification(SystemActionEnum.ESTOP);
	}
	
	public static SystemNotification newStopNotification () {
		return new SystemNotification(SystemActionEnum.NORMAL_STOP);
	}

	//--------------------------------------------------------------------------------

	public static DistributionList getDistributionList () {
		return distributionList;
	}
	
	//--------------------------------------------------------------------------------

	public SystemActionEnum getSystemAction() {
		return systemAction;
	}
	
	//--------------------------------------------------------------------------------

	public void publish (Object publishedBy) {
		publish(publishedBy, distributionList);
	}
	
	public void publishSelf( SubscriberInterface subscriber) {
		((SystemNotificationSubscriberInterface) subscriber).receiveNotification(this);
	}
}
