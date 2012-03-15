package com.builditboys.robots.system;

import java.io.IOException;

import com.builditboys.robots.communication.WindowsLinkPort;
import com.builditboys.robots.infrastructure.IntegerParameter;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.infrastructure.StringParameter;

public class WindowsRobotSystem extends AbstractRobotSystem {

	// --------------------------------------------------------------------------------

	public static WindowsRobotSystem launchWindowsRobotSystem() throws InterruptedException, IOException {
		instance = new WindowsRobotSystem();
		
		instance.startBasicRobotSystem();
		return (WindowsRobotSystem) instance;
	}
	
	public static WindowsRobotSystem safeLaunchWindowsRobotSystem() {
		try {
			return launchWindowsRobotSystem();
		} catch (Exception e) {
			System.out.println("Launch Exception");
			e.printStackTrace();
		}
		return null;
	}
	
	// --------------------------------------------------------------------------------

	public static void launchWindowsRobotSystemRunnable () {
		WindowsRobotSystemStarter starter = new WindowsRobotSystemStarter();
		Thread thread = new Thread(starter, "Robot Starter");
		System.out.println("Starting " + "Robot Starter" + " thread");
		thread.start();
	}
	
	private static class WindowsRobotSystemStarter implements Runnable {
		public void run() {
			safeLaunchWindowsRobotSystem();
		}
	}

	// --------------------------------------------------------------------------------

	public static void stopWindowsRobotSystem () throws InterruptedException, IOException {
		((WindowsRobotSystem) instance).stopBasicRobotSystem();
	}
	
	public static void safeStopWindowsRobotSystem () {
		try {
			stopWindowsRobotSystem();
		} catch (Exception e) {
			System.out.println("Stop Exception");
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------------

	public static void stopWindowsRobotSystemRunnable () {
		WindowsRobotSystemStopper stopper = new WindowsRobotSystemStopper();
		Thread thread = new Thread(stopper, "Robot Stopper");
		System.out.println("Starting " + "Robot Stopper" + " thread");
		thread.start();
	}
	
	private static class WindowsRobotSystemStopper implements Runnable {
		public void run() {
			safeStopWindowsRobotSystem();
		}
	}
	
	// --------------------------------------------------------------------------------

	// assumes that some config parameters have already been set up
	public void startBasicRobotSystem() throws InterruptedException, IOException {
		StringParameter commPortNameParameter = StringParameter.getParameter("COMM_PORT");
		IntegerParameter baudRateParameter = IntegerParameter.getParameter("COMM_PORT_BAUD_RATE");
		
		// --------------------
		// windows specific set up here
		linkPort = new WindowsLinkPort(commPortNameParameter.getValue(),
									   baudRateParameter.getValue(),
									   true);
		
		// --------------------
		// generic set up
		startBasicRobotSystem();

		// --------------------
		// windows specific set up here
	}

	// --------------------------------------------------------------------------------

	public synchronized void stopBasicRobotSystem() throws InterruptedException, IOException {
		// --------------------
		// windows specific set up here

		
		// --------------------
		// generic set up
		stopBasicRobotSystem();
		
		// --------------------
		// windows specific set up here
		
	}


	
}

