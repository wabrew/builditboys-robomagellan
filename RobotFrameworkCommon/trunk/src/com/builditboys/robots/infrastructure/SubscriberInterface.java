package com.builditboys.robots.infrastructure;


public interface SubscriberInterface {

// this does not work, if you put this in then subscribers need to implement
// the method exactly which is not what you want.  what you want is for a
// subscriber to implement overloaded methods (plural) that have type
// signatures so that the correct method is called depending on the type
// of notification that is being received
// keep this interface for now in case there is some other thing that we
// want to force a subscriber to do
//	abstract void receiveNotification (AbstractNotification notification);
	
}
