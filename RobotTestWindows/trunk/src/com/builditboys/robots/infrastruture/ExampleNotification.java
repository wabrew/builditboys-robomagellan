package com.builditboys.robots.infrastruture;

import com.builditboys.robots.infrastructure.AbstractNotification;
import com.builditboys.robots.infrastructure.SubscriberInterface;

public class ExampleNotification extends AbstractNotification {
	
	private String myMessage = new String("this is an example notification");
	
	public String getMyMessage() {
		return myMessage;
	}
	
	// not synchronized, but if you call something that depends on state,
	// it should be synchronized
	public void publishSelf (SubscriberInterface subscriber) {
		((ExampleSubscriberInterface) subscriber).receiveNotification(this);
	}

}
