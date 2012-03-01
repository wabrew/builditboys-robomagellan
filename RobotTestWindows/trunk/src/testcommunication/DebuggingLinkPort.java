package testcommunication;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.builditboys.robots.communication.LinkPortInterface;

public class DebuggingLinkPort implements LinkPortInterface {

	ArrayBlockingQueue<Byte> readBuffer;
	ArrayBlockingQueue<Byte> writeBuffer;

	// --------------------------------------------------------------------------------

	public DebuggingLinkPort(ArrayBlockingQueue<Byte> rBuffer,
			ArrayBlockingQueue<Byte> wBuffer) {
		readBuffer = rBuffer;
		writeBuffer = wBuffer;
	}

	// --------------------------------------------------------------------------------

	public byte readByte() throws InterruptedException {
		return readBuffer.take();
	}

	public void writeByte(byte bite) throws InterruptedException {
		writeBuffer.put(bite);
	}
	
	// --------------------------------------------------------------------------------

	public void close () {
		System.out.println("Closing debugging link port");
	}

}
