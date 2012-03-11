package com.builditboys.robots.infrastructure;

import com.builditboys.robots.time.LocalTimeSystem;

//See also DistributionList
//         NotificationInterface
//         AbstractNotification
//         SubscriberInterface

/*
The Big Picture

The main objects:
   Notifications -- see NotificationInterface and AbstractNotification
   Subscribers -- see SubscriberInterface
   Distribution lists -- see DistributionList
   
You create a specific notification class by either implementing
NotificationInterface or more easily by extending AbstractNotification.

You create a specific subscriber interface that extends SubscriberInterface
but is specific to the new notification.

You create distribution lists by instantiating DistributionList.

You arrange for interested parties to subscribe to the distribution list.

You then create new instances of your notification and publish them to the
distribution list.  This causes the subscribers to get method calls to receive
the notification object.  The subscriber should grab any info it needs and
then return.

Some examples include:
   SystemNotification and SystemNotificationSubscriberInterface
   RobotControlNotification and RobotControlNotificationSubscriberInterface

*/


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
	
	// Synchronize so that the notice is stable for all subscribers
	public synchronized void publish (Object publishedBy, DistributionList distList) {
		publisher = publishedBy;
		publicationTime = LocalTimeSystem.currentLocalTime();				
		distList.publish(this);
	}
		
	// Must be overridden, generally does a cast of the subscriber to the
	// appropriate subscriber interface so that you force getting the
	// correct method called.
	public abstract void publishSelf (SubscriberInterface subscriber);

	//--------------------------------------------------------------------------------

	
	public String toString () {
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}
	
}
