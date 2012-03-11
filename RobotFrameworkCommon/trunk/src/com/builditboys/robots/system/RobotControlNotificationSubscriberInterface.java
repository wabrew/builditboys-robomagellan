package com.builditboys.robots.system;

import com.builditboys.robots.infrastructure.SubscriberInterface;

public interface RobotControlNotificationSubscriberInterface extends SubscriberInterface{

	public abstract void receiveNotification (RobotControlNotification notice);
	
}
