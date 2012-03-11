package com.builditboys.robots.infrastructure;

//See also DistributionList
//         NotificationInterface
//         AbstractNotification
//         SubscriberInterface

public interface SubscriberInterface {

// This does not work, if you put this in then subscribers need to implement
// the method exactly which is not what you want.  What you want is for a
// subscriber to implement overloaded methods (plural) that have type
// signatures so that the correct method is called depending on the type
// of notification that is being received.
	
// Keep this interface for now in case there is some other thing that we
// want to force a subscriber to do.  At the very least, it forces subscribers
// to at least have to implement SubscriberInterface which will find potential
// compile time bugs.
//	abstract void receiveNotification (AbstractNotification notification);
	
}
