package com.builditboys.robots.infrastructure;

import java.util.ArrayList;

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
	
	public synchronized void subscribe (SubscriberInterface subscriber) {
		subscribers.add(subscriber);
	}
	
	// synchronize so that subscriber list is stable 
	public synchronized void unsubscribe (SubscriberInterface subscriber) {
		subscribers.remove(subscriber);
	}

	//--------------------------------------------------------------------------------

	public synchronized void publish (AbstractNotification notice) {
		notePublication(notice);
		for (SubscriberInterface subscriber: subscribers) {
			notice.publishSelf(subscriber);
		}
	}
	
	//--------------------------------------------------------------------------------

	private void notePublication (AbstractNotification notice) {
		System.out.println(MiscUtilities.objectShortName(notice.getPublisher())
						   + " is publishing " 
						   + notice
						   + " at time " 
						   + notice.getPublicationTime()
						   + " to list "
						   + MiscUtilities.objectShortName(this)
						   + " with "
						   + subscribers.size() 
						   + " subscribers");
	}

	//--------------------------------------------------------------------------------
		
	public static DistributionList addDistributionListNamed (String name) {
		DistributionList distList = new DistributionList(name);
		ParameterServer.addParameter(distList);
		return distList;
	}
	
	public static DistributionList getDistributionListNamed (String name) {
		return (DistributionList) ParameterServer.getParameter(name);
	}
}

