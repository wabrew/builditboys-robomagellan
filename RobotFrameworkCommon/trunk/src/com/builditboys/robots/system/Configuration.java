package com.builditboys.robots.system;

import com.builditboys.robots.infrastructure.IntegerParameter;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.infrastructure.StringParameter;

public class Configuration {
	
	public static void loadConfiguration (String configFileName) {
		// should read from a config file, this is a temporary hack
		
		StringParameter robotNameParameter = new StringParameter("ROBOT_NAME", "ROBOMAGELLAN");
		StringParameter commPortNameParameter = new StringParameter("COMM_PORT", "COM10");
		IntegerParameter baudRateParameter = new IntegerParameter("COMM_PORT_BAUD_RATE", 115200);

		ParameterServer.getInstance().addParameter(robotNameParameter);
		ParameterServer.getInstance().addParameter(commPortNameParameter);
		ParameterServer.getInstance().addParameter(baudRateParameter);
	}

}
