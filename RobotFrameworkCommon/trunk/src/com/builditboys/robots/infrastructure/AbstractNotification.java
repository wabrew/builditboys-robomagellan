package com.builditboys.robots.infrastructure;

import com.builditboys.robots.time.LocalTimeSystem;

public abstract class AbstractNotification implements NotificationInterface {
	
	protected Object publisher;
	protected long publicationTime;

	//--------------------------------------------------------------------------------

	public Object getPublisher() {
		return publisher;
	}

	public long getPublicationTime() {
		return publicationTime;
	}

	//--------------------------------------------------------------------------------
	// Dispatching
	
	// synchronize so that the notice is stable for all subscribers
	public synchronized void publish (Object publishedBy, DistributionList distList) {
		publisher = publishedBy;
		publicationTime = LocalTimeSystem.currentLocalTime();				
		distList.publish(this);
	}
		
	// must be overridden
	public abstract void publishSelf (SubscriberInterface subscriber);

	//--------------------------------------------------------------------------------

	
	public String toString () {
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}
	
}
