package testcommunication;

import java.util.concurrent.ArrayBlockingQueue;

import com.builditboys.robots.communication.FillableBuffer;

public class DebuggingCommPortBuffer {

	ArrayBlockingQueue<Byte> buffer1;
	ArrayBlockingQueue<Byte> buffer2;
	
	DebuggingCommPort port1;
	DebuggingCommPort port2;

	public DebuggingCommPortBuffer() {
		buffer1 = new ArrayBlockingQueue<Byte>(1000);
		buffer2 = new ArrayBlockingQueue<Byte>(1000);
		port1 = new DebuggingCommPort(buffer1, buffer2);
		port2 = new DebuggingCommPort(buffer2, buffer1);	
	}

	public DebuggingCommPort getPort1() {
		return port1;
	}

	public DebuggingCommPort getPort2() {
		return port2;
	}

}
