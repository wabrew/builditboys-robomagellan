package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.CommParameters.*;

public abstract class AbstractSenderReceiver implements Runnable {

	protected AbstractCommLink link;

	protected CommPortInterface port;

	protected CRC8Calculator crc8;
	protected CRC16Calculator crc16;

	protected int sequenceNumber;

	protected volatile ThreadControlEnum threadControl;
	protected String threadName;
	protected Thread thread;
	protected volatile boolean shouldRun = true;
	
	// --------------------------------------------------------------------------------

	public void startThread (String threadName) {
		if (thread != null) {
			throw new IllegalStateException();
		}
		this.threadName = threadName;
		shouldRun = true;
		threadControl = ThreadControlEnum.RUN;
		thread = new Thread(this, threadName);
		System.out.println("Starting " + threadName);
		thread.start();
	}

	// --------------------------------------------------------------------------------
	// Run

	public void run() {
		try {
			while (shouldRun) {
				checkThreadControl();
				doWork();
			}
		} catch (InterruptedException e) {
			System.out.println(threadName + " interrupted");
		}
	}
	
	public abstract void doWork () throws InterruptedException;

	// --------------------------------------------------------------------------------
	// Thread control

	public void suspendThread () {
		threadControl = ThreadControlEnum.SUSPEND;
	}
	
	public void resumeThread () {
		threadControl = ThreadControlEnum.RUN;
	}

	public void stopThread () {
		threadControl = ThreadControlEnum.STOP;
		thread.interrupt();
	}
	
	public void threadJoin () throws InterruptedException {
		thread.join();
	}
	
	protected synchronized void checkThreadControl() throws InterruptedException {
		do {
			switch (threadControl) {
			case SUSPEND:
				wait();
				break;
			case RUN:
				break;
			case STOP:
				shouldRun = false;
				break;
			default:
				throw new IllegalStateException();
			}
		} while (threadControl == ThreadControlEnum.SUSPEND);
	}

	// --------------------------------------------------------------------------------
	// Sequence numbers

	protected void resetSequenceNumber() {
		sequenceNumber = SEQUENCE_NUM_MIN - 1;
	}

	protected int nextSequenceNumber() {
		sequenceNumber++;
		if (sequenceNumber > SEQUENCE_NUM_MAX) {
			sequenceNumber = SEQUENCE_NUM_MIN;
		}
		return sequenceNumber;
	}

}
