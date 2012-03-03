package com.builditboys.robots.communication;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;

import com.builditboys.robots.system.AbstractRobotSystem;

// see RXTX for more info
// see http://java.sun.com/products/javacomm/reference/docs/API_users_guide_3.html
// see http://docs.oracle.com/cd/E17802_01/products/products/javacomm/reference/api/index.html
// blocking behavior is determined by threshold and timeout, but the driver
// may or may not support these

public class WindowsCommPort {

	private String portName;
	private int baud;
	private boolean doReadBuffering = false;

	private boolean isConnected = false;

	private CommPortIdentifier portIdentifier;
	private SerialPort serialPort;
	private InputStream inStream;
	private OutputStream outStream;

	private ArrayBlockingQueue<Byte> inputBuffer;
	private SerialReader reader;
	private String threadName;
	private Thread thread;

	private static final int READ_BUFFER_SIZE = 1024;

	// --------------------------------------------------------------------------------
	// Constructors

	public WindowsCommPort() {
	}

	public WindowsCommPort(String portNm, int bd) throws NoSuchPortException,
			PortInUseException, IOException, UnsupportedCommOperationException {
		portName = portNm;
		baud = bd;
		doReadBuffering = false;
	}

	public WindowsCommPort(String portNm, int bd, boolean bufferReads)
			throws NoSuchPortException, PortInUseException, IOException,
			UnsupportedCommOperationException {
		portName = portNm;
		baud = bd;
		doReadBuffering = bufferReads;
	}

	// --------------------------------------------------------------------------------

	public void open() throws NoSuchPortException, PortInUseException,
							  IOException, UnsupportedCommOperationException {
		// This is a little unorthodox, but there are problems opening a comm
		// port if it was not closed properly.  If the comm port was never openned
		// then closing it does not hurt anything.  I tried to put a close
		// in a finally block somewhere but  its kind of hard to find a spot
		// that is late enough after all the other threads have been shutdown.
		close();
		connect();
	}

	/*
	 * public void close () throws IOException {
	 * System.out.println("closing port"); thread.interrupt(); inStream.close();
	 * outStream.close(); serialPort.close(); System.out.println("port closed");
	 * }
	 */

	public void close() throws IOException {
		if (thread != null) {
			thread.interrupt();
		}

		inputBuffer = null;
		reader = null;
		threadName = null;
		thread = null;

		if (serialPort != null) {
			serialPort.close();
			serialPort = null;
		}

		if (inStream != null) {
			inStream.close();
			inStream = null;
		}

		if (outStream != null) {
			outStream.close();
			outStream = null;
		}

		portIdentifier = null;
		isConnected = false;
	}

	// --------------------------------------------------------------------------------

	public String getPortName() {
		return portName;
	}

	public int getBaud() {
		return baud;
	}

	public boolean isConnected() {
		return isConnected;
	}

	// --------------------------------------------------------------------------------

	// the read port does not block as it should

	// this should have been on by default and should cause reads to block
	// it does not work
	// serialPort.disableReceiveThreshold();
	// serialPort.disableReceiveTimeout();

	public synchronized void connect() throws NoSuchPortException,
			PortInUseException, IOException, UnsupportedCommOperationException {
		if (isConnected) {
			throw new IllegalStateException();
		}
		if (!isLegalBaudRate(baud)) {
			throw new IllegalArgumentException();
		}

		portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			throw new IllegalStateException();
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(),
					2000);

			if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				inStream = serialPort.getInputStream();
				outStream = serialPort.getOutputStream();

				if (doReadBuffering) {
					inputBuffer = new ArrayBlockingQueue<Byte>(READ_BUFFER_SIZE);
					startThread("Comm Port Reader");
				}

				isConnected = true;
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

	// --------------------------------------------------------------------------------

	public byte readByte() throws IOException {
		if (!doReadBuffering) {
			return (byte) inStream.read();
		} else {
			throw new IllegalStateException();
		}
	}

	public int read() throws IOException {
		if (!doReadBuffering) {
			return inStream.read();
		} else {
			throw new IllegalStateException();
		}
	}

	// --------------------------------------------------------------------------------

	public void writeByte(byte bite) throws IOException {
		outStream.write(bite);
	}

	public void write(int bite) throws IOException {
		outStream.write(bite);
	}

	public void write(byte bytes[]) throws IOException {
		outStream.write(bytes);
	}

	public void write(String str) throws IOException {
		outStream.write(str.getBytes());
	}

	// --------------------------------------------------------------------------------
	// We use a thread to poll the port to make buffered read work; kind of a
	// kludge

	private void startThread(String threadNm) {
		if (thread != null) {
			throw new IllegalStateException();
		}
		threadName = threadNm;
		reader = new SerialReader(inStream, inputBuffer);
		thread = new Thread(reader, threadName);
		System.out.println("Starting " + threadName + " thread");
		thread.start();
	}

	// --------------------------------------------------------------------------------

	public byte bufferedReadByte() throws InterruptedException {
		if (doReadBuffering) {
			return inputBuffer.take();
		} else {
			throw new IllegalStateException();
		}
	}

	public int bufferedRead() throws InterruptedException {
		if (doReadBuffering) {
			return inputBuffer.take();
		} else {
			throw new IllegalStateException();
		}
	}

	// --------------------------------------------------------------------------------

	private static final int bauds[] = { 2400, 4800, 9600, 19200, 38400, 57600,
			115200 };

	private boolean isLegalBaudRate(int brate) {
		for (int legalBaud : bauds) {
			if (brate == legalBaud) {
				return true;
			}
		}
		return false;
	}

	// --------------------------------------------------------------------------------

	private class SerialReader implements Runnable {
		InputStream readerStream;
		ArrayBlockingQueue<Byte> portBuffer;

		private static final int COMM_PORT_READER_POLL_INTERVAL = 300;
		private static final int RAW_READ_BUFFER_SIZE = 1024;

		public SerialReader(InputStream in, ArrayBlockingQueue<Byte> pBuffer) {
			readerStream = in;
			portBuffer = pBuffer;
		}

		public synchronized void run() {
			byte buffer[] = new byte[RAW_READ_BUFFER_SIZE];
			int length = -1;
			try {
				while (true) {
					length = readerStream.read(buffer);
//					System.out.println("reader checking " + length);
					if (length > 0) {
						for (int i = 0; i < length; i++) {
							portBuffer.put(buffer[i]);
						}
					} else {
						wait(COMM_PORT_READER_POLL_INTERVAL);
					}
				}
			} catch (InterruptedException e) {
				System.out.println("Comm Port Reader: thread interrupted");
			} catch (Exception e) {
				handleThreadException(e);
			}
			System.out.println("Comm Port Reader: thread exiting");
		}
			
	}
	
	private void handleThreadException (Exception e) {
		AbstractRobotSystem.notifyRobotSystemError(threadName, e);
	}

}
