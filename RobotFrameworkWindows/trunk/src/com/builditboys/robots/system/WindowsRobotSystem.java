package com.builditboys.robots.system;

import java.io.IOException;

import com.builditboys.robots.communication.WindowsLinkPort;
import com.builditboys.robots.infrastructure.IntegerParameter;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.infrastructure.StringParameter;

public class WindowsRobotSystem extends AbstractRobotSystem {

	// --------------------------------------------------------------------------------

	public static WindowsRobotSystem launchWindowsRobotSystem(String configName) throws InterruptedException, IOException {
		instance = new WindowsRobotSystem();
		
		// load the config file and stuff some parameters before starting
		instance.configFileName = configName;
		Configuration.loadConfiguration(instance.configFileName);
		
		instance.startRobotSystem();
		return (WindowsRobotSystem) instance;
	}
	
	public static WindowsRobotSystem safeLaunchWindowsRobotSystem(String configName) {
		try {
			return launchWindowsRobotSystem(configName);
		} catch (Exception e) {
			System.out.println("Launch Exception");
			e.printStackTrace();
		}
		return null;
	}
	
	
	// --------------------------------------------------------------------------------

	private static class WindowsRobotSystemStarter implements Runnable {
		String configFile;
		
		WindowsRobotSystemStarter (String config) {
			configFile = config;
		}
		
		public void run() {
			safeLaunchWindowsRobotSystem(configFile);
		}
	}
	
	public static void launchWindowsRobotSystemRunnable (String configName) {
		WindowsRobotSystemStarter starter = new WindowsRobotSystemStarter(configName);
		Thread thread = new Thread(starter, "Robot Starter");
		System.out.println("Starting " + "Robot Starter" + " thread");
		thread.start();
	}

	// --------------------------------------------------------------------------------

	public static void stopWindowsRobotSystem () throws InterruptedException, IOException {
		((WindowsRobotSystem) ParameterServer.getParameter("ROBOT_SYSTEM")).stopRobotSystem();
	}
	
	public static void safeStopWindowsRobotSystem () {
		try {
			stopWindowsRobotSystem();
		} catch (Exception e) {
			System.out.println("Stop Exception");
			e.printStackTrace();
		}
	}

	private static class WindowsRobotSystemStopper implements Runnable {
		public void run() {
			safeStopWindowsRobotSystem();
		}
	}
	
	public static void stopWindowsRobotSystemRunnable () {
		WindowsRobotSystemStopper stopper = new WindowsRobotSystemStopper();
		Thread thread = new Thread(stopper, "Robot Stopper");
		System.out.println("Starting " + "Robot Stopper" + " thread");
		thread.start();
	}
	
	// --------------------------------------------------------------------------------

	// assumes that some config parameters have already been set up
	public void startRobotSystem() throws InterruptedException, IOException {
		StringParameter commPortNameParameter = (StringParameter) ParameterServer.getParameter("COMM_PORT");
		IntegerParameter baudRateParameter = (IntegerParameter) ParameterServer.getParameter("COMM_PORT_BAUD_RATE");
		
		// --------------------
		// windows specific set up here
		linkPort = new WindowsLinkPort(commPortNameParameter.getValue(),
									   baudRateParameter.getValue(),
									   true);
		
		// --------------------
		// generic set up
		super.startRobotSystem();

		// --------------------
		// windows specific set up here
	}

	// --------------------------------------------------------------------------------

	public synchronized void stopRobotSystem() throws InterruptedException, IOException {
		// --------------------
		// windows specific set up here

		
		// --------------------
		// generic set up
		super.stopRobotSystem();
		
		// --------------------
		// windows specific set up here
		
	}


	
}

