package com.builditboys.robots.system;

import java.io.IOException;

import com.builditboys.robots.communication.AbstractProtocol.ProtocolRoleEnum;
import com.builditboys.robots.communication.AbstractLink;
import com.builditboys.robots.communication.AbstractProtocol;
import com.builditboys.robots.communication.LinkPortInterface;
import com.builditboys.robots.communication.MasterLink;
import com.builditboys.robots.driver.RobotDriverProtocol;
import com.builditboys.robots.infrastructure.DistributionList;
import com.builditboys.robots.infrastructure.ParameterInterface;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.infrastructure.StringParameter;
import com.builditboys.robots.time.LocalTimeSystem;
import com.builditboys.robots.time.TimeSyncProtocol;

/*
The concept.

The robot system is responsible for getting things started up and shut down. 
AbstractRobotSystem takes care of most of the basic setup and shut down.  You
make subclasses that do more specific setup and shut down.  You can have several
layers in the hierarchy.  For example, the class WindowsRobotSystem does windows
specific setup and shutdown but thats about it.  For your specific robot, you
probably need to extend WindowsRobotSystem to actually do something by creating
a thread that embodies the robot's mission or top-level.

Each layer in the hierarchy has responsibilities.  A leaf layer creates an
instance of itself, stuffs it in INSTANCE and then calls its own copy of 
startRobotSystem.  This method does some specific setup and then calls
super.startRobotSystem.

Similar situation exists with stopRobotSystem.

*/


public abstract class AbstractRobotSystem implements ParameterInterface {

	// Holds the single robot system instance.
	// Not a final but pretty close, gets set in when a subclass is created
	// and cannot be set again.
	private static AbstractRobotSystem INSTANCE;
	

	private static final SystemNotification START1_NOTICE = SystemNotification.newStart1Notification();
	private static final SystemNotification START2_NOTICE = SystemNotification.newStart2Notification();
	private static final SystemNotification START3_NOTICE = SystemNotification.newStart3Notification();
	private static final SystemNotification ESTOP_NOTICE = SystemNotification.newEstopNotification();
	private static final SystemNotification STOP_NOTICE = SystemNotification.newStopNotification();

	private static final int ROBOT_SYSTEM_PHASE_WAIT = 200;

	protected LinkPortInterface linkPort;
	protected MasterLink masterLink;
	
	protected DistributionList systemDistList = SystemNotification.getDistributionList();

	// --------------------------------------------------------------------------------
	
	public static AbstractRobotSystem getInstance() {
		return INSTANCE;
	}

	protected static void setInstance(AbstractRobotSystem instance) {
		if (INSTANCE == null) {
			INSTANCE = instance;
		}
		else {
			throw new IllegalStateException("Robot system instance is already set");
		}
	}
	
	// --------------------------------------------------------------------------------

	protected synchronized void startRobotSystem() throws InterruptedException, IOException {
		startRobotSystemPhase1();
		startRobotSystemPhase2();
		startRobotSystemPhase3();
		
		System.out.println("**Basic setup complete");
		ParameterServer.print();
		RobotState.getParameter("ROBOT_STATE").print();
		System.out.println();
	}
	
	private void startRobotSystemPhase1 () throws InterruptedException {
		System.out.println("**Starting phase 1 setup");

		ParameterServer.addParameter(this);
		
		// set up local time first, many things depend on it including notifications
		LocalTimeSystem.startLocalTimeNow();
		System.out.println("Local time initialized");
		
		// other stuff here

		START1_NOTICE.publish(this);
		System.out.println("Phase 1 notification sent");
		wait(ROBOT_SYSTEM_PHASE_WAIT);		
	}
	
	private void startRobotSystemPhase2 () throws InterruptedException, IOException {
		System.out.println("**Starting phase 2 setup");

		// get the link to the robot set up
		masterLink = new MasterLink("ROBOT_LINK", linkPort);
		ParameterServer.addParameter(masterLink);
		
		TimeSyncProtocol.addProtocolToLink(masterLink, ProtocolRoleEnum.MASTER);
		RobotControlProtocol.addProtocolToLink(masterLink, ProtocolRoleEnum.MASTER, "ROBOT_STATE");
		RobotDriverProtocol.addProtocolToLink(masterLink, ProtocolRoleEnum.MASTER);
		masterLink.startLink();
		System.out.println("Link started");
		
		START2_NOTICE.publish(this);
		System.out.println("Phase 2 notification sent");
		wait(ROBOT_SYSTEM_PHASE_WAIT);		
	}

	private void startRobotSystemPhase3 () throws InterruptedException {
		System.out.println("**Starting phase 3 setup");
		
		masterLink.sleepUntilReady();
		masterLink.enable();
		System.out.println("Link enabled");
		
		// other stuff here
		
		START3_NOTICE.publish(this);
		System.out.println("Phase 3 notification sent");
		wait(ROBOT_SYSTEM_PHASE_WAIT);				
	}

	// --------------------------------------------------------------------------------

	protected synchronized void stopRobotSystem() throws InterruptedException, IOException {
		System.out.println();
		System.out.println("Shutting down");
		STOP_NOTICE.publish(this);
		System.out.println("Stop notification sent");
		wait(ROBOT_SYSTEM_PHASE_WAIT);

// should really just listen for the stop notice
		masterLink.stopLink();
	}

	// --------------------------------------------------------------------------------


	public static void stopTheRobotSystem () throws InterruptedException, IOException {
		AbstractRobotSystem.getInstance().stopRobotSystem();
	}
	
	public static void safeStopTheRobotSystem () {
		try {
			stopTheRobotSystem();
		} catch (Exception e) {
			System.out.println("Stop Exception");
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------------

	public static void stopTheRobotSystemRunnable () {
		TheRobotSystemStopper stopper = new TheRobotSystemStopper();
		Thread thread = new Thread(stopper, "Robot Stopper");
		System.out.println("Starting " + "Abstract Robot Stopper" + " thread");
		thread.start();
	}
	
	private static class TheRobotSystemStopper implements Runnable {
		public void run() {
			safeStopTheRobotSystem();
		}
	}
	
	// --------------------------------------------------------------------------------

	public static void acknowledgeRobotSystemError (String threadName, Exception e) {
		INSTANCE.acknowledgeRobotSystemErrorI(threadName, e);
	}
	
	public void acknowledgeRobotSystemErrorI(String threadName, Exception e) {
		System.out.println("Exception in thread " + threadName + ": " + e);
		e.printStackTrace();
		ESTOP_NOTICE.publish(INSTANCE, systemDistList);
	}
	
	// --------------------------------------------------------------------------------

	public String getName () {
		return "ROBOT_SYSTEM";
	}
	
	// --------------------------------------------------------------------------------

	public static AbstractRobotSystem getParameter (String key) {
		return (AbstractRobotSystem) ParameterServer.getParameter(key);
	}
	
	public static AbstractRobotSystem maybeGetParameter (String key) {
		return (AbstractRobotSystem) ParameterServer.maybeGetParameter(key);
	}
		
	// --------------------------------------------------------------------------------

	public static AbstractProtocol getRobotInputProtocol (AbstractProtocol representative) {
		return INSTANCE.masterLink.getInputProtocol(representative);
	}
	
	public static AbstractProtocol getRobotOutputProtocol (AbstractProtocol representative) {
		return INSTANCE.masterLink.getOutputProtocol(representative);
	}
	
	// --------------------------------------------------------------------------------

	public static void printState () {
		System.out.println("Robot State:");
		RobotState state = RobotState.maybeGetParameter("ROBOT_STATE");
		if (state != null) {
			state.print();
		}
		else {
			System.out.println("  No current robot state");
		}
		AbstractLink link = AbstractLink.maybeGetParameter("ROBOT_LINK");
		if (link != null) {
			System.out.println("  Link state: " + link.getLinkState());
		}
		else {
			System.out.println("  No current link state");
		}
	}

	
	// --------------------------------------------------------------------------------

}
