package com.builditboys.robots.system;

import java.io.IOException;


public class TestWindowsRobotSystem extends WindowsRobotSystem {

	// --------------------------------------------------------------------------------

	public static void launchTestWindowsRobotSystem() throws InterruptedException, IOException {
		AbstractRobotSystem.setInstance(new TestWindowsRobotSystem());	
		AbstractRobotSystem.getInstance().startRobotSystem();
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


}
