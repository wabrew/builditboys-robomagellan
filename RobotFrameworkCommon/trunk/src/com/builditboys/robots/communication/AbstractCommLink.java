package com.builditboys.robots.communication;

// a comm link hold together all the pieces
// the port
// the send and receive threads
// the input and output channel collections

public abstract class AbstractCommLink implements Runnable {

	protected Sender sender;
	protected Receiver receiver;

	protected CommPortInterface commPort;

	protected InputChannelCollection inputChannels;
	protected OutputChannelCollection outputChannels;

	protected volatile ThreadControlEnum threadControl;
	protected String threadName;
	protected Thread thread;
	protected volatile boolean shouldRun;

	// --------------------------------------------------------------------------------
	// Constructors

	protected AbstractCommLink(CommPortInterface port) {
		commPort = port;
		inputChannels = new InputChannelCollection(this);
		outputChannels = new OutputChannelCollection(this);
		sender = new Sender(this, commPort);
		receiver = new Receiver(this, commPort);
	}

	// --------------------------------------------------------------------------------
	// Channel collections
	
	protected InputChannelCollection getInputChannels() {
		return inputChannels;
	}

	protected OutputChannelCollection getOutputChannels() {
		return outputChannels;
	}

	// --------------------------------------------------------------------------------
	// Finding channels
	
	public AbstractChannel getInputChannelN (int channelNumber) {
		return inputChannels.getChannelByNumber(channelNumber);
	}
	
	public AbstractChannel getOutputChannelN (int channelNumber) {
		return outputChannels.getChannelByNumber(channelNumber);
	}
	
	public AbstractChannel getInputChannelByProtocol (AbstractProtocol protocol) {
		return inputChannels.getChannelByProtocol(protocol);
	}
	
	public AbstractChannel getOutputChannelByProtocol (AbstractProtocol protocol) {
		return outputChannels.getChannelByProtocol(protocol);
	}
	
	// --------------------------------------------------------------------------------
	// Thread stuff
	
	public String getThreadName() {
		return threadName;
	}

	// --------------------------------------------------------------------------------

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

	// --------------------------------------------------------------------------------

	public void startThreads(String threadNm) {
		startThread(threadNm + " Master Comm Link");
		sender.startThread(threadName + " Sender");
		receiver.startThread(threadName + " Receiver");
	}

	public void stopThreads() {
		stopThread();
		sender.stopThread();
		receiver.stopThread();
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

	public abstract void doWork() throws InterruptedException;

	// --------------------------------------------------------------------------------
	// Thread control

	public void suspendThread() {
		threadControl = ThreadControlEnum.SUSPEND;
	}

	public void resumeThread() {
		threadControl = ThreadControlEnum.RUN;
		notify();
	}

	public void stopThread() {
		threadControl = ThreadControlEnum.STOP;
		thread.interrupt();
	}

	public void threadJoin() throws InterruptedException {
		sender.threadJoin();
		receiver.threadJoin();
		thread.join();
	}

	protected synchronized void checkThreadControl()
			throws InterruptedException {
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
	
	public void receiveCommControlMessage (AbstractChannel rChannel, CommMessage message) {
		System.out.println("Link received comm control message");
	}
	
	public void receiveReceiveException (Exception e) {
		System.out.println("Link Receive Exception");
		// this needs to do something to tell the send side that a problem occured
	}

}
