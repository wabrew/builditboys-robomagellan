package com.builditboys.robots.system;

import com.builditboys.robots.infrastructure.AbstractNotification;
import com.builditboys.robots.infrastructure.SubscriberInterface;
import com.builditboys.robots.time.Time;


public class SystemNotification extends AbstractNotification {

	public enum SystemActionEnum {
		START1,
		START2,
		ESTOP,
		NORMAL_STOP;
	}

	private SystemActionEnum systemAction;

	//--------------------------------------------------------------------------------

	public SystemActionEnum getSystemAction() {
		return systemAction;
	}

	//--------------------------------------------------------------------------------

	public SystemNotification (SystemActionEnum action) {
		systemAction = action;
	}
	
	public void publishSelf(SubscriberInterface subscriber) {
		((SystemNotificationSubscriberInterface) subscriber).receiveNotification(this);
	}
}
