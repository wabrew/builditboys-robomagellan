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

/*
	static AbstractRobotSystem getInstance() {
		if (instance != null) {
			return instance;
		} else {
			throw new IllegalStateException("Instance not setup by child class");
		}
	}
*/
	
	public synchronized void startRobotSystem() throws InterruptedException, IOException {
		StringParameter robotNameParameter = (StringParameter) ParameterServer.getParameter("ROBOT_NAME");

		robotName = robotNameParameter.getValue();
		ParameterServer.addParameter(this);
		
		LocalTimeSystem.startLocalTimeNow();
		System.out.println("Local time initialized");

		start1Notice.publish(this);
		wait(ROBOT_SYSTEM_PHASE_WAIT);

		masterLink = new MasterLink("ROBOT_LINK", linkPort);
		ParameterServer.addParameter(masterLink);
		masterLink.startLink();
		System.out.println("Link started");
		
		

		start2Notice.publish(this);
		wait(ROBOT_SYSTEM_PHASE_WAIT);
	}

	public synchronized void stopRobotSystem() throws InterruptedException, IOException {
		stopNotice.publish(this);
		wait(ROBOT_SYSTEM_PHASE_WAIT);

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
