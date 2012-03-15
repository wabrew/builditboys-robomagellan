package com.builditboys.robots.system;

import java.io.IOException;

import javax.swing.SwingUtilities;

import com.builditboys.robots.system.WindowsRobotSystem;

public class TestSystem {
	
	public static void main (String args[]) {
		System.out.println("Main thread starting");	
		
		Configuration.loadConfigurationFile("foo");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SimpleGUI gui = new SimpleGUI();
				gui.setVisible(true);
			}
		});
		
		System.out.println("Main thread finished");
	}

	//--------------------------------------------------------------------------------

/*
	static final WindowsRobotSystem ROBO_SYS;
	
	private static void startRobot () {
		try {
			robosys = WindowsRobotSystem.launchWindowsRobotSystem("Test");
			
			doNothingWhileRobotRuns();
			
			robosys.stopRobotSystem();

		} catch (InterruptedException e) {
			System.out.println("Main thread interrupted");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private static void runRobot () {
		try {
			robosys = WindowsRobotSystem.launchWindowsRobotSystem("Test");
			
			doNothingWhileRobotRuns();
			
			robosys.stopRobotSystem();

		} catch (InterruptedException e) {
			System.out.println("Main thread interrupted");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	//--------------------------------------------------------------------------------

	private static void doNothingWhileRobotRuns () throws InterruptedException {
		Thread.sleep(5000);  // this is when the robot would really do something
	}

*/
}
