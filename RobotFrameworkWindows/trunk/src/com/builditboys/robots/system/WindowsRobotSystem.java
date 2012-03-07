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
		instance.configFileName = configName;
		Configuration.loadConfiguration(instance.configFileName);
		instance.startRobotSystem();
		return (WindowsRobotSystem) instance;
	}

	// --------------------------------------------------------------------------------

	public void startRobotSystem() throws InterruptedException, IOException {
		StringParameter commPortNameParameter = (StringParameter) ParameterServer.getInstance().getParameter("COMM_PORT");
		IntegerParameter baudRateParameter = (IntegerParameter) ParameterServer.getInstance().getParameter("COMM_PORT_BAUD_RATE");
		
		// windows specific stuff here
		linkPort = new WindowsLinkPort(commPortNameParameter.getValue(),
									   baudRateParameter.getValue(),
									   true);

		super.startRobotSystem();

		// windows specific stuff here
	}

	// --------------------------------------------------------------------------------

}
