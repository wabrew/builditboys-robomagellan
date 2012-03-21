package com.builditboys.robots.system;

import java.io.IOException;

import com.builditboys.robots.communication.WindowsLinkPort;
import com.builditboys.robots.infrastructure.IntegerParameter;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.infrastructure.StringParameter;
import com.builditboys.robots.robomagellan.AbstractRoboMagellanRobotSystem;


public class TestWindowsRobotSystem extends AbstractRoboMagellanRobotSystem {

	// --------------------------------------------------------------------------------
	// Launch
	
	public static void launchTestWindowsRobotSystem() throws InterruptedException, IOException {
		AbstractRobotSystem.setInstance(new TestWindowsRobotSystem());	
		AbstractRobotSystem.getInstance().build();
		AbstractRobotSystem.getInstance().start();
	}
	
	public static void safeLaunchTestWindowsRobotSystem() {
		try {
			launchTestWindowsRobotSystem();
		} catch (Exception e) {
			System.out.println("Launch Exception");
			e.printStackTrace();
		}
	}
	
	// --------------------------------------------------------------------------------

	public static void launchTestWindowsRobotSystemRunnable () {
		TestWindowsRobotSystemStarter starter = new TestWindowsRobotSystemStarter();
		Thread thread = new Thread(starter, "Robot Starter");
		System.out.println("Starting " + "Windows Robot Starter" + " thread");
		thread.start();
	}
	
	private static class TestWindowsRobotSystemStarter implements Runnable {
		public void run() {
			safeLaunchTestWindowsRobotSystem();
		}
	}
	
	// --------------------------------------------------------------------------------
	// Build
	
	protected void build () throws IOException {
		// class specific code here
		System.out.println();
		System.out.println("** Building the robot system");

		StringParameter commPortNameParameter = StringParameter.getParameter("COMM_PORT");
		IntegerParameter baudRateParameter = IntegerParameter.getParameter("COMM_PORT_BAUD_RATE");
		
		// --------------------
		// windows specific set up here
		linkPort = new WindowsLinkPort(commPortNameParameter.getValue(),
									   baudRateParameter.getValue(),
									   true);
		super.build();
		
		// class specific code here
	}
	
	// --------------------------------------------------------------------------------
	// Start
	
	protected void start () throws InterruptedException, IOException {
		// class specific code here
		System.out.println();
		System.out.println("** Starting the robot system");
		
		super.start();
		
		// class specific code here
		System.out.println();
		System.out.println("** Basic setup complete");
		ParameterServer.print();
		RobotState.getParameter("ROBOT_STATE").print();
		System.out.println();
		
//		masterLink.describe();
	}
	
	// --------------------------------------------------------------------------------
	// Stop
	
	protected void stop () throws IOException, InterruptedException {
		// class specific code here
		System.out.println();
		System.out.println("** Stopping the robot system");

		super.stop();
		
		// class specific code here
		
	}
	
	// --------------------------------------------------------------------------------
	// Destroy
	
	protected void destroy () {
		// class specific code here
		System.out.println();
		System.out.println("** Destroying the robot system");

		super.destroy();
		
		// class specific code here
		
	}


}
