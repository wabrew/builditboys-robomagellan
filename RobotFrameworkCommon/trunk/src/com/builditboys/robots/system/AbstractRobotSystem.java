package com.builditboys.robots.system;

import java.io.IOException;

import com.builditboys.robots.communication.LinkPortInterface;
import com.builditboys.robots.communication.MasterLink;
import com.builditboys.robots.infrastructure.DistributionList;
import com.builditboys.robots.infrastructure.ObjectParameter;
import com.builditboys.robots.infrastructure.ParameterInterface;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.infrastructure.StringParameter;
import com.builditboys.robots.time.LocalTimeSystem;

public abstract class AbstractRobotSystem implements ParameterInterface {

	public static AbstractRobotSystem instance;

	private static final SystemNotification start1Notice = SystemNotification.newStart1Notification();
	private static final SystemNotification start2Notice = SystemNotification.newStart2Notification();
	private static final SystemNotification eStopNotice = SystemNotification.newEstopNotification();
	private static final SystemNotification stopNotice = SystemNotification.newStopNotification();

	private static final int ROBOT_SYSTEM_PHASE_WAIT = 200;

	protected String configFileName;
	protected String robotName;
	
	protected LinkPortInterface linkPort;
	protected MasterLink masterLink;
	
	protected DistributionList systemDistList = SystemNotification.getDistributionList();

	// --------------------------------------------------------------------------------
	
	public synchronized void startRobotSystem() throws InterruptedException, IOException {
		startRobotSystemPhase1();
		startRobotSystemPhase2();
		startRobotSystemPhase3();
		
		System.out.println("**Basic setup complete");
		ParameterServer.print();
		System.out.println();
	}
	
	private void startRobotSystemPhase1 () throws InterruptedException {
		System.out.println("**Starting phase 1 setup");
		StringParameter robotNameParameter = (StringParameter) ParameterServer.getParameter("ROBOT_NAME");

		robotName = robotNameParameter.getValue();
		ParameterServer.addParameter(this);
		
		// set up local time first, many things depend on it including notifications
		LocalTimeSystem.startLocalTimeNow();
		System.out.println("Local time initialized");

		// send out the first start phase notification
		start1Notice.publish(this);
		System.out.println("Phase 1 notification sent");
		wait(ROBOT_SYSTEM_PHASE_WAIT);		
	}
	
	private void startRobotSystemPhase2 () throws InterruptedException, IOException {
		System.out.println("**Starting phase 2 setup");

		// get the link to the robot set up
		masterLink = new MasterLink("ROBOT_LINK", linkPort);
		ParameterServer.addParameter(masterLink);

// starting the link should probably happen by listening to a start notice
		masterLink.startLink();
		System.out.println("Link started");
		
		
		// other stuff goes here
		
		start2Notice.publish(this);
		System.out.println("Phase 2 notification sent");
		wait(ROBOT_SYSTEM_PHASE_WAIT);		
	}

	private void startRobotSystemPhase3 () {
		System.out.println("**Starting phase 3 setup");
		
	}


	// --------------------------------------------------------------------------------

	public synchronized void stopRobotSystem() throws InterruptedException, IOException {
		System.out.println();
		System.out.println("Shutting down");
		stopNotice.publish(this);
		System.out.println("Stop notification sent");
		wait(ROBOT_SYSTEM_PHASE_WAIT);

// should really just listen for the stop notice
		masterLink.stopLink();
	}

	// --------------------------------------------------------------------------------

	public static void notifyRobotSystemError (String threadName, Exception e) {
		instance.notifyRobotSystemErrorI(threadName, e);
	}
	
	public void notifyRobotSystemErrorI (String threadName, Exception e) {
		System.out.println("Exception in thread " + threadName + ": " + e);
		e.printStackTrace();
		eStopNotice.publish(instance, systemDistList);
	}
	
	// --------------------------------------------------------------------------------

	public String getName () {
		return "ROBOT_SYSTEM";
	}
	
	public String toString () {
		return "Robot System: \"" + robotName + "\"";
	}
	


}
