package com.builditboys.robots.infrastructure;

//See also DistributionList
//         NotificationInterface
//         AbstractNotification
//         SubscriberInterface

public interface NotificationInterface {

	// the simplest way to implement this interface is to extend AbstractNotification
	
	//--------------------------------------------------------------------------------

	// The object that did the publishing, you as you wish
	public Object getPublisher(); 

	// The time at which publication started (when you call publish)
	public long getPublicationTime();
	
	//--------------------------------------------------------------------------------	
	
	// call this to publish a notification
	// see AbstractNotification for example of what to do
	public abstract void publish (Object publishedBy, DistributionList distList);
	
	// A distribution list will call this to do the actual publishing
	// Its up to you to have a contract with your subscribers
	public abstract void publishSelf (SubscriberInterface subscriber);

}
