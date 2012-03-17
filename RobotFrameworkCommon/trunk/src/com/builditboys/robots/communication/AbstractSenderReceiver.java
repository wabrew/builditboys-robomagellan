package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

import java.io.IOException;

import com.builditboys.robots.system.AbstractRobotSystem;
import com.builditboys.robots.time.LocalTimeSystem;

public abstract class AbstractSenderReceiver implements Runnable {

	protected AbstractLink link;

	protected LinkPortInterface port;

	protected CRC8Calculator crc8;
	protected CRC16Calculator crc16;

	protected volatile ThreadControlEnum threadControl;
	protected String threadName;
	protected Thread thread;
	protected volatile boolean shouldRun = true;
	protected volatile boolean suspended;

	protected int sequenceNumber;

	// --------------------------------------------------------------------------------
	// Run

	public void run() {
		while (true) {
			try {
				checkThreadControl();
			} catch (InterruptedException e) {
					System.out.println(threadName + ": thread interrupted");
					continue;
			}
			// check said to exit
			// check resumed from a wait
			// check said to keep running
			// if the check was interrupted, control went back to the top
			if (!shouldRun) {
				break;
			}
			try {
				doWork();
			} catch (InterruptedException e1) {
				System.out.println(threadName + ": thread interrupted");
			} catch (Exception e) {
				handleThreadException(e);
			} 
		}
		System.out.println(threadName + ": thread exiting");
	}
	
	private void handleThreadException (Exception e) {
		AbstractRobotSystem.acknowledgeRobotSystemError(threadName, e);
		threadControl = ThreadControlEnum.STOP;
	}

	public abstract void doWork() throws InterruptedException, IOException;

	// --------------------------------------------------------------------------------

	public void threadJoin() throws InterruptedException {
		thread.join();
	}

	// --------------------------------------------------------------------------------
	// Thread control

	public String getThreadName() {
		return threadName;
	}

	public void startThread(String threadName) {
		if (thread != null) {
			throw new IllegalStateException();
		}
		this.threadName = threadName;
		shouldRun = true;
		threadControl = ThreadControlEnum.RUN;
		thread = new Thread(this, threadName);
		System.out.println("Starting " + threadName + " thread");
		thread.start();
	}

	public void suspendThread() {
		threadControl = ThreadControlEnum.SUSPEND;
		thread.interrupt();
	}

	public void resumeThread() {
		if (suspended) {
			threadControl = ThreadControlEnum.RUN;
			notify();
		}
		else {
			throw new IllegalStateException();
		}
	}

	public void stopThread() {
		threadControl = ThreadControlEnum.STOP;
		thread.interrupt();
	}

	protected synchronized void checkThreadControl() throws InterruptedException {
		do {
			switch (threadControl) {
			case SUSPEND:
				suspended = true;
				wait();
				suspended = false;
				break;
			case RUN:
				break;
			case STOP:
				shouldRun = false;
				break;
			default:
				throw new IllegalStateException();
			}
		// loop to avoid spurious awakenings
		} while (threadControl == ThreadControlEnum.SUSPEND);
	}

	// --------------------------------------------------------------------------------
	// Sequence numbers

	protected synchronized void resetSequenceNumber () {
		sequenceNumber = SEQUENCE_NUM_MIN - 1;
	}
	
	protected synchronized int bestSequenceNumber() {
		if (link.isForceInitialSequenceNumbers()) {
			sequenceNumber = SEQUENCE_NUM_MIN;
		}
		else {
			sequenceNumber++;
			if (sequenceNumber > SEQUENCE_NUM_MAX) {
				sequenceNumber = SEQUENCE_NUM_MIN;
			}
		}
		return sequenceNumber;
	}

	// --------------------------------------------------------------------------------

	private static boolean doDebugPrint = true;
	private static boolean doDebugPrintImAlive = false;
	
	protected void debugPrintMessage (String direction, int seqNr, int channelNr, int length, int CRC1, LinkMessage message, int CRC2) {
		if (doDebugPrint) {
			if (LinkControlProtocol.isKeepAliveMessage(message)) {
				if (doDebugPrintImAlive) {
					debugPrintMessage1(direction, seqNr, channelNr, length, CRC1, message, CRC2);
				}
			}
			else {
				debugPrintMessage1(direction, seqNr, channelNr, length, CRC1, message, CRC2);				
			}
		}
	}
	
	private void debugPrintMessage1 (String direction, int seqNr, int channelNr, int length, int CRC1, LinkMessage message, int CRC2) {
		synchronized (System.out) {
			System.out.print(LocalTimeSystem.currentTime());
			System.out.print(" : " + link.getRole() + " " + direction + ": ");
			printRawMessage(seqNr, channelNr, length, CRC1, message, CRC2);
			System.out.println();
		}		
	}
	
	private static void printRawMessage(int seqNr, int channelNr, int length, int CRC1, LinkMessage message, int CRC2) {
		System.out.print(seqNr);
		System.out.print(" ");
		System.out.print(channelNr);
		System.out.print(" ");
		System.out.print(length);
		System.out.print(" ");
		System.out.print(CRC1);
		System.out.print(" ");

		message.printBuffer();

		System.out.print(CRC2);
		System.out.print(" ");
	}

}
