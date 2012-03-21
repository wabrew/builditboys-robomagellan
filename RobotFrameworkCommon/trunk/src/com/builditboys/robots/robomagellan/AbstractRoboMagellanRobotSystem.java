package com.builditboys.robots.robomagellan;

import java.io.IOException;

import com.builditboys.robots.communication.AbstractLink;
import com.builditboys.robots.communication.LinkPortInterface;
import com.builditboys.robots.communication.MasterLink;
import com.builditboys.robots.communication.AbstractProtocol.ProtocolRoleEnum;
import com.builditboys.robots.driver.RobotDriverProtocol;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.system.AbstractRobotSystem;
import com.builditboys.robots.system.RobotControlProtocol;
import com.builditboys.robots.system.RobotState;
import com.builditboys.robots.time.LocalTimeSystem;
import com.builditboys.robots.time.TimeSyncProtocol;

public abstract class AbstractRoboMagellanRobotSystem extends AbstractRobotSystem {
	
	protected LinkPortInterface linkPort;
	protected MasterLink masterLink;
	
	// --------------------------------------------------------------------------------
	// Build
	
	protected void build ()  throws IOException {
		// class specific code here

		super.build();
		
		// class specific code here
		// get the link to the robot set up
		masterLink = new MasterLink("ROBOT_LINK", linkPort);
		ParameterServer.addParameter(masterLink);
		
		TimeSyncProtocol.addProtocolToLink(masterLink, ProtocolRoleEnum.MASTER);
		RobotControlProtocol.addProtocolToLink(masterLink, ProtocolRoleEnum.MASTER, "ROBOT_STATE");
		RobotDriverProtocol.addProtocolToLink(masterLink, ProtocolRoleEnum.MASTER);
	}
	
	// --------------------------------------------------------------------------------
	// Start
		
	protected synchronized void start() throws InterruptedException, IOException {
		// class specific code here

		super.start();
		
		// class specific code here
		masterLink.startLink();
		System.out.println("Link started");
		
		masterLink.sleepUntilReady();
		masterLink.enable();
		System.out.println("Link enabled");
	}
	
	// --------------------------------------------------------------------------------
	// Stop
	
	protected void stop () throws IOException, InterruptedException {
		// class specific code here
		masterLink.stopLink();
		
		super.stop();
		
		// class specific code here

	}
	
	// --------------------------------------------------------------------------------
	// Destroy
	
	protected void destroy () {
		// class specific code here

		super.destroy();
		
		// class specific code here

	}
	
	
	// --------------------------------------------------------------------------------

	public static void printState () {
		System.out.println("Robot State:");
		RobotState state = RobotState.maybeGetParameter("ROBOT_STATE");
		if (state != null) {
			state.print();
		}
		else {
			System.out.println("  No current robot state");
		}
		AbstractLink link = AbstractLink.maybeGetParameter("ROBOT_LINK");
		if (link != null) {
			System.out.println("  Link state: " + link.getLinkState());
		}
		else {
			System.out.println("  No current link state");
		}
	}

	
}
