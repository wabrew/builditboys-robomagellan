package com.builditboys.robots.system;

import java.io.IOException;

import com.builditboys.robots.communication.LinkPortInterface;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.system.SystemNotification.SystemActionEnum;

public abstract class AbstractRobotSystem {
	
	public static ParameterServer parameterServer = ParameterServer.getInstance();
	
	public static AbstractRobotSystem instance;
		
	private static final SystemNotification start1Notice = new SystemNotification(SystemActionEnum.START1);
	private static final SystemNotification start2Notice = new SystemNotification(SystemActionEnum.START2);
	private static final SystemNotification stopNotice = new SystemNotification(SystemActionEnum.STOP);
	
	
	
	protected LinkPortInterface linkPort;

	//--------------------------------------------------------------------------------

	static AbstractRobotSystem getIntance () {
		if (instance != null) {
			return instance;
		}
		else {
			throw new IllegalStateException("Instance not setup by child class");
		}
	}
	
	public void startRobotSystem () throws IOException {
		start1Notice.publish();
		start2Notice.publish();
	}
	
	public void stopRobotSystem () {
		stopNotice.publish();
	}
	
	//--------------------------------------------------------------------------------
	
	

	
	//--------------------------------------------------------------------------------

}
