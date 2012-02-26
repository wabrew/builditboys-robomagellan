package testcommunication;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.builditboys.robots.communication.LinkPortInterface;


public class DebuggingCommPort implements LinkPortInterface {

	ArrayBlockingQueue<Byte> readBuffer;
	ArrayBlockingQueue<Byte> writeBuffer;

	public DebuggingCommPort(ArrayBlockingQueue<Byte> rBuffer, ArrayBlockingQueue<Byte> wBuffer) {
		readBuffer = rBuffer;
		writeBuffer = wBuffer;
	}

	public byte readByte() throws InterruptedException {
		return readBuffer.take();
	}

	public void writeByte(byte bite) throws InterruptedException {
		writeBuffer.put(bite);
	}

	public void writeBytes(byte bites[]) throws InterruptedException {
		for (byte b : bites) {
			writeByte(b);
		}

	}

	public void tossInput() {
	}

	public void tossOutput() {
	}

}
