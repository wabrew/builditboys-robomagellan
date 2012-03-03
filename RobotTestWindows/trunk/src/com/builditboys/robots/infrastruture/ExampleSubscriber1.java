package com.builditboys.robots.infrastruture;



public class ExampleSubscriber1 implements ExampleSubscriberInterface {
	
	// not synchronized, but if you call something that depends on state,
	// it should be synchronized
	public void receiveNotification (ExampleNotification notice) {
		System.out.print("One received message: ");
		System.out.println(notice.getMyMessage());
	}

}
