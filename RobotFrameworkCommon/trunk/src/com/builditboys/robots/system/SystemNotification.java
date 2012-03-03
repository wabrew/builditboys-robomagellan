package com.builditboys.robots.system;

import com.builditboys.robots.infrastructure.AbstractNotification;
import com.builditboys.robots.infrastructure.SubscriberInterface;


public class SystemNotification extends AbstractNotification {

	public enum SystemActionEnum {
		START1,
		START2,
		STOP;
	}

	SystemActionEnum systemAction;
	
	public SystemNotification (SystemActionEnum action) {
		systemAction = action;
	}
	
	public void publishSelf(SubscriberInterface subscriber) {
		((SystemNotificationSubscriberInterface) subscriber).receiveNotification(this);
	}
}
