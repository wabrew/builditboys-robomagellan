package testcommunication;

import java.util.concurrent.ArrayBlockingQueue;


public class DebuggingLinkPortBuffer {

	ArrayBlockingQueue<Byte> buffer1;
	ArrayBlockingQueue<Byte> buffer2;
	
	DebuggingLinkPort port1;
	DebuggingLinkPort port2;

	public DebuggingLinkPortBuffer() {
		buffer1 = new ArrayBlockingQueue<Byte>(1000);
		buffer2 = new ArrayBlockingQueue<Byte>(1000);
		port1 = new DebuggingLinkPort(buffer1, buffer2);
		port2 = new DebuggingLinkPort(buffer2, buffer1);	
	}

	public DebuggingLinkPort getPort1() {
		return port1;
	}

	public DebuggingLinkPort getPort2() {
		return port2;
	}

}
