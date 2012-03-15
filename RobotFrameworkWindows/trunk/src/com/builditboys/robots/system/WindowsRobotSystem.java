package com.builditboys.robots.system;

import java.io.IOException;

import com.builditboys.robots.communication.WindowsLinkPort;
import com.builditboys.robots.infrastructure.IntegerParameter;
import com.builditboys.robots.infrastructure.StringParameter;

public abstract class WindowsRobotSystem extends AbstractRobotSystem {

	// --------------------------------------------------------------------------------
	// Start
	
	// assumes that some config parameters have already been set up
	public void startRobotSystem() throws InterruptedException, IOException {
		StringParameter commPortNameParameter = StringParameter.getParameter("COMM_PORT");
		IntegerParameter baudRateParameter = IntegerParameter.getParameter("COMM_PORT_BAUD_RATE");
		
		// --------------------
		// windows specific set up here
		linkPort = new WindowsLinkPort(commPortNameParameter.getValue(),
									   baudRateParameter.getValue(),
									   true);
		
		// --------------------
		// more generic set up
		
		super.startRobotSystem();

		// --------------------
		// windows specific set up here
		
	}

	// --------------------------------------------------------------------------------
	// Stop
	
	public synchronized void stopRobotSystem() throws InterruptedException, IOException {
		// --------------------
		// windows specific set up here

		
		// --------------------
		// more generic shut down
		
		super.stopRobotSystem();
		
		// --------------------
		// windows specific set up here
		
	}

}

