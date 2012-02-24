package com.builditboys.robots.infrastructure;

import java.util.ArrayList;

public abstract class AbstractNotification {
	
	// the objects that the dispatch will be delivered to
	private static ArrayList<SubscriberInterface> subscribers = new ArrayList<SubscriberInterface>();
	
	//--------------------------------------------------------------------------------
	// Adding/removing dispatch receivers
	
	public static synchronized void subscribe (SubscriberInterface subscriber) {
		subscribers.add(subscriber);
	}
	
	// synchronize so that subscriber list is stable 
	public static synchronized void unsubscribe (SubscriberInterface subscriber) {
		subscribers.remove(subscriber);
	}

	//--------------------------------------------------------------------------------
	// Dispatching
	
	// synchronize so that subscriber list is stable, see subscribe, unsubscribe
	public synchronized void publish () {
		for (SubscriberInterface who: subscribers) {
			publishSelf(who);
		}
	}
	
	public abstract void publishSelf (SubscriberInterface who);
	
}
