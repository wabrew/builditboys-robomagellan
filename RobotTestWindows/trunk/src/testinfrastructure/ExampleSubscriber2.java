package testinfrastructure;



public class ExampleSubscriber2 implements ExampleSubscriberInterface {
	
	// not synchronized, but if you call something that depends on state,
	// it should be synchronized	
	public synchronized void receiveNotification (ExampleNotification notice) {
		System.out.print("Two received message: ");
		System.out.println(notice.getMyMessage());
	}

}
