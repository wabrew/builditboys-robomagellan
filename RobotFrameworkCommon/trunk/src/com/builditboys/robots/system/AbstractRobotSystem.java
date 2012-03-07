package com.builditboys.robots.system;

import java.io.IOException;

import com.builditboys.robots.communication.LinkPortInterface;
import com.builditboys.robots.communication.MasterLink;
import com.builditboys.robots.infrastructure.ParameterInterface;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.infrastructure.StringParameter;
import com.builditboys.robots.system.SystemNotification.SystemActionEnum;
import com.builditboys.robots.time.Time;

public abstract class AbstractRobotSystem implements ParameterInterface {

	public static ParameterServer parameterServer = ParameterServer.getInstance();

	public static AbstractRobotSystem instance;

	private static final SystemNotification start1Notice = new SystemNotification(SystemActionEnum.START1);
	private static final SystemNotification start2Notice = new SystemNotification(SystemActionEnum.START2);
	private static final SystemNotification eStopNotice = new SystemNotification(SystemActionEnum.ESTOP);
	private static final SystemNotification stopNotice = new SystemNotification(SystemActionEnum.NORMAL_STOP);

	private static final int ROBOT_SYSTEM_PHASE_WAIT = 200;

	protected String configFileName;
	protected String robotName;
	
	protected LinkPortInterface linkPort;
	protected MasterLink masterLink;

	// --------------------------------------------------------------------------------

	static AbstractRobotSystem getIntance() {
		if (instance != null) {
			return instance;
		} else {
			throw new IllegalStateException("Instance not setup by child class");
		}
	}

	public synchronized void startRobotSystem() throws InterruptedException, IOException {
		ParameterServer.getInstance().addParameter(this);
		StringParameter robotNameParameter = (StringParameter) ParameterServer.getInstance().getParameter("ROBOT_NAME");

		robotName = robotNameParameter.getValue();
		
		masterLink = new MasterLink("Robot Link", linkPort);
		ParameterServer.getInstance().addParameter(masterLink);
		masterLink.startLink();
		System.out.println("Link started");

		Time.initializeLocalTime();
		System.out.println("Local time initialized");

		start1Notice.publish(this);
		wait(ROBOT_SYSTEM_PHASE_WAIT);

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
		System.out.println("Exception in thread " + threadName + ": " + e);
		e.printStackTrace();
		eStopNotice.publish(instance);
	}
	
	// --------------------------------------------------------------------------------

	public String getName () {
		return robotName;
	}
	// --------------------------------------------------------------------------------

}
