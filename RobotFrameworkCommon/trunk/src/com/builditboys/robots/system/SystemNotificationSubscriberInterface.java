package com.builditboys.robots.system;

import com.builditboys.robots.infrastructure.SubscriberInterface;

public interface SystemNotificationSubscriberInterface extends SubscriberInterface {
	
	public abstract void receiveNotification (SystemNotification notice);
	
}
