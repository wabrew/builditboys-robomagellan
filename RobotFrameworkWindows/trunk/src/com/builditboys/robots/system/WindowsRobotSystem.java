package com.builditboys.robots.system;


public class WindowsRobotSystem extends AbstractRobotSystem {
		
	public static void launchWindowsRobotSystem () {
		instance = new WindowsRobotSystem();
		instance.startRobotSystem();
	}
	
	public void startRobotSystem () {
		// windows specific stuff here
		super.startRobotSystem();
		// windows specific stuff here
	}
	
	public void stopRobotSystem () {
		// windows specific stuff here
		super.stopRobotSystem();
		// windows specific stuff here
	}


}
