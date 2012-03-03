package com.builditboys.robots.infrastructure;

import java.util.ArrayList;

import com.builditboys.robots.time.Time;

public abstract class AbstractNotification {
	
	// the objects that the dispatch will be delivered to
	private static ArrayList<SubscriberInterface> subscribers = new ArrayList<SubscriberInterface>();
	
	private Object publisher;
	private long publicationTime;

	//--------------------------------------------------------------------------------

	public Object getPublisher() {
		return publisher;
	}

	public long getPublicationTime() {
		return publicationTime;
	}

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
	public synchronized void publish (Object publishedBy) {
		publisher = publishedBy;
		publicationTime = Time.getAbsoluteTime();
		notePublication();
		for (SubscriberInterface subscriber: subscribers) {
			publishSelf(subscriber);
		}
	}
	
	public abstract void publishSelf (SubscriberInterface subscriber);
	
	private void notePublication () {
		System.out.println(publisher 
						   + " is publishing " 
						   + this 
						   + " at " 
						   + publicationTime 
						   + " to " 
						   + subscribers.size() 
						   + " subscribers");
	}
	
}
