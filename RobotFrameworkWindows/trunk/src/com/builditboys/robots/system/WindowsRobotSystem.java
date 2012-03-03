package com.builditboys.robots.system;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;

import com.builditboys.robots.communication.WindowsLinkPort;

public class WindowsRobotSystem extends AbstractRobotSystem {

	// --------------------------------------------------------------------------------

	public static WindowsRobotSystem launchWindowsRobotSystem(String roboName) throws InterruptedException, IOException {
		instance = new WindowsRobotSystem();
		instance.robotName = roboName;

		instance.startRobotSystem();

		return (WindowsRobotSystem) instance;
	}

	// --------------------------------------------------------------------------------

	public void startRobotSystem() throws InterruptedException, IOException {
		// windows specific stuff here
		linkPort = new WindowsLinkPort("COM10", 115200, true);

		super.startRobotSystem();

		// windows specific stuff here
	}

	// --------------------------------------------------------------------------------

}
