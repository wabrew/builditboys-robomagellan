package com.builditboys.robots.infrastruture;

import com.builditboys.robots.infrastructure.ParameterServer;

public class TestInfrastructure {
	
	private ParameterServer parmServer = ParameterServer.getInstance();
	
	private ExampleParameter parm = new ExampleParameter("Example Parameter");
	private ExampleParameter getParm;
	
	private ExampleNotification notice = new ExampleNotification();
	
	private ExampleSubscriber1 subscriber1 = new ExampleSubscriber1();
	private ExampleSubscriber2 subscriber2 = new ExampleSubscriber2();
	
	public void test () {
		// parameter server
		parmServer.addParameter(parm);
		getParm = (ExampleParameter) parmServer.getParameter("Example Parameter");
		if (getParm == null) {
			System.out.println("could not find parameter");
		}
		else {
			System.out.println(getParm.getValue1());
			parm.setValue1(142);
			System.out.println(getParm.getValue1());
		}

		
		// publish/subscribe
		ExampleNotification.subscribe(subscriber1);
		ExampleNotification.subscribe(subscriber2);
		notice.publish(this);
			
	}

	public static void main (String args[]) {
		TestInfrastructure test = new TestInfrastructure();
		test.test();
	}

}
