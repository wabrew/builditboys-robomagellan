package com.builditboys.robots.system;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;

import com.builditboys.robots.communication.WindowsLinkPort;


public class WindowsRobotSystem extends AbstractRobotSystem {
		
	
	//--------------------------------------------------------------------------------

	public static void launchWindowsRobotSystem () throws IOException {
		instance = new WindowsRobotSystem();
		instance.startRobotSystem();
	}

	//--------------------------------------------------------------------------------

	public void startRobotSystem () throws IOException {
		// windows specific stuff here
		linkPort = new WindowsLinkPort("COM10", 115200, true);
		
		super.startRobotSystem();
		
		// windows specific stuff here
	}
	
	public void stopRobotSystem () {
		// windows specific stuff here
		
		super.stopRobotSystem();
		
		// windows specific stuff here
	}
	
	//--------------------------------------------------------------------------------


}
