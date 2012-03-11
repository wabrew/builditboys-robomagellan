package com.builditboys.robots.infrastruture;

import com.builditboys.robots.infrastructure.DistributionList;
import com.builditboys.robots.infrastructure.ParameterServer;

public class TestInfrastructure {
	
	private ExampleParameter parm = new ExampleParameter("Example Parameter");
	private ExampleParameter getParm;
	
	private ExampleNotification notice = new ExampleNotification();
	
	private ExampleSubscriber1 subscriber1 = new ExampleSubscriber1();
	private ExampleSubscriber2 subscriber2 = new ExampleSubscriber2();
	private NonExampleSubscriber nonSubscriber1 = new NonExampleSubscriber();
	
	private DistributionList distList = new DistributionList("test list");
	
	public void test () {
		// parameter server
		ParameterServer.addParameter(parm);
		getParm = (ExampleParameter) ParameterServer.getParameter("Example Parameter");
		if (getParm == null) {
			System.out.println("could not find parameter");
		}
		else {
			System.out.println(getParm.getValue1());
			parm.setValue1(142);
			System.out.println(getParm.getValue1());
		}

		
		// publish/subscribe
		distList.subscribe(subscriber1);
		distList.subscribe(subscriber2);
		
// causes a cast exception (its supposed to)
//		distList.subscribe(nonSubscriber1);
		notice.publish(this, distList);
			
	}

	public static void main (String args[]) {
		TestInfrastructure test = new TestInfrastructure();
		test.test();
	}

}
