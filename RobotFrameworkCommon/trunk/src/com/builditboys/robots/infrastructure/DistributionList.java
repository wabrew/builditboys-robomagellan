package com.builditboys.robots.infrastructure;

import java.util.ArrayList;

// See also DistributionList
//          NotificationInterface
//          AbstractNotification
//          SubscriberInterface

import com.builditboys.robots.utilities.MiscUtilities;

public class DistributionList implements ParameterInterface {

	// the objects that the dispatch will be delivered to
	private ArrayList<SubscriberInterface> subscribers = new ArrayList<SubscriberInterface>();
	
	private String name;
	
	//--------------------------------------------------------------------------------

	public DistributionList (String nm) {
		name = nm;
	}
	
	//--------------------------------------------------------------------------------
	
	public String getName () {
		return name;
	}
	
	//--------------------------------------------------------------------------------
	// Adding/removing dispatch receivers
	
	// synchronize so that subscriber list is stable 
	public synchronized void subscribe (SubscriberInterface subscriber) {
		subscribers.add(subscriber);
	}
	
	// synchronize so that subscriber list is stable 
	public synchronized void unsubscribe (SubscriberInterface subscriber) {
		subscribers.remove(subscriber);
	}

	//--------------------------------------------------------------------------------

	// synchronize so that the subscriber list is stable
	public synchronized void publish (AbstractNotification notice) {
		notePublication(notice);
		for (SubscriberInterface subscriber: subscribers) {
			notice.publishSelf(subscriber);
		}
	}
	
	//--------------------------------------------------------------------------------

	private void notePublication (AbstractNotification notice) {
		System.out.println(MiscUtilities.bestObjectName(notice.getPublisher())
						   + " is publishing " 
						   + notice
						   + " at time " 
						   + notice.getPublicationTime()
						   + " to "
						   + this
						   + " with "
						   + subscribers.size() 
						   + " subscribers");
	}

	//--------------------------------------------------------------------------------
	// Parameter server interaction
	
	// Make a new distribution list and put it in the parameter server
	public static DistributionList addDistributionListNamed (String name) {
		DistributionList distList = new DistributionList(name);
		ParameterServer.addParameter(distList);
		return distList;
	}
	
	// Get a distribution list from the parameter server
	public static DistributionList getDistributionListNamed (String name) {
		return (DistributionList) ParameterServer.getParameter(name);
	}
	
	
	//--------------------------------------------------------------------------------

	public String toString () {
		return "Dist List: \"" + name + "\"";
	}
}

