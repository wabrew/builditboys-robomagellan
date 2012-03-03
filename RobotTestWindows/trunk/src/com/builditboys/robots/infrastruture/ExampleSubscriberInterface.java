package com.builditboys.robots.infrastruture;

import com.builditboys.robots.infrastructure.SubscriberInterface;

public interface ExampleSubscriberInterface extends SubscriberInterface {

	public abstract void receiveNotification (ExampleNotification notice);
	
}
