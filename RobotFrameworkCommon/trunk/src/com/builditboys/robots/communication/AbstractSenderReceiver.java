package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

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
					System.out.println(threadName + " check interrupted");
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
			} catch (InterruptedException e) {
				System.out.println(threadName + " work interrupted");
			}
		}
	}

	public abstract void doWork() throws InterruptedException;

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
		System.out.println("Starting " + threadName);
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

}
