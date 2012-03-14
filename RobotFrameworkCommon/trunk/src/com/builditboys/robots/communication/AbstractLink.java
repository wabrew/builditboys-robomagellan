package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

import java.io.IOException;

import com.builditboys.robots.infrastructure.ParameterInterface;
import com.builditboys.robots.system.AbstractRobotSystem;
import com.builditboys.robots.time.*;

// a comm link holds together all the pieces
// the port
// the send and receive threads
// the input and output channel collections

public abstract class AbstractLink implements ParameterInterface, Runnable {

	protected String name;
	
	protected Sender sender;
	protected Receiver receiver;

	protected LinkPortInterface commPort;

	protected InputChannelCollection inputChannels;
	protected OutputChannelCollection outputChannels;

	protected volatile ThreadControlEnum threadControl;
	protected String threadName;
	protected Thread thread;
	protected volatile boolean shouldRun;
	protected volatile boolean suspended;

	protected InputChannel controlChannelIn;
	protected OutputChannel controlChannelOut;

	protected LinkControlProtocol linkInputControlProtocol;
	protected LinkControlProtocol linkOutputControlProtocol;

	protected long lastKeepAliveSentTime = 0;       // system time
	protected long lastKeepAliveReceivedTime = 0;   // system time

	// --------------------------------------------------------------------------------
	// Constructors

	protected AbstractLink(String nm, LinkPortInterface port) {
		name = nm;
		commPort = port;
		inputChannels = new InputChannelCollection(this);
		outputChannels = new OutputChannelCollection(this);
		sender = new Sender(this, commPort);
		receiver = new Receiver(this, commPort);
	}

	// --------------------------------------------------------------------------------

	public String getName () {
		return name;
	}
	
	public String toString () {
		return "Link: \"" + name + "\"";
	}
	
	// --------------------------------------------------------------------------------
	// Starting things up

	// see startThreads below

	// enable and disable let you turn on and off normal message processing
	public abstract void enable();

	public abstract void disable();

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

	public InputChannel getInputChannelN(int channelNumber) {
		return (InputChannel) inputChannels.getChannelByNumber(channelNumber);
	}

	public OutputChannel getOutputChannelN(int channelNumber) {
		return (OutputChannel) outputChannels.getChannelByNumber(channelNumber);
	}

	public InputChannel getInputChannelByProtocol(AbstractProtocol protocol) {
		return (InputChannel) inputChannels.getChannelByProtocol(protocol);
	}

	public OutputChannel getOutputChannelByProtocol(AbstractProtocol protocol) {
		return (OutputChannel) outputChannels.getChannelByProtocol(protocol);
	}

	// --------------------------------------------------------------------------------
	// Adding a protocol

	public void addProtocol(AbstractProtocol iproto, AbstractProtocol oproto) {
		InputChannel channelIn = iproto.getInputChannel();
		OutputChannel channelOut = oproto.getOutputChannel();

		AbstractChannel.pairChannels(channelIn, channelOut);

		inputChannels.addChannel(channelIn);
		outputChannels.addChannel(channelOut);
	}

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
			} catch (InterruptedException e) {
				System.out.println(threadName + ": thread interrupted");
			} catch (Exception e) {
				handleThreadException(e);
			}
		}
		System.out.println(threadName + ": thread exiting");
	}

	public abstract void doWork() throws InterruptedException;

	private void handleThreadException (Exception e) {
		AbstractRobotSystem.notifyRobotSystemError(threadName, e);
		threadControl = ThreadControlEnum.STOP;
	}

	// --------------------------------------------------------------------------------
	// Thread control for all of the link's threads

	public void startLink() throws IOException {
		commPort.open();

		if (commPort.isOpen()) {
			startThread(name);
			sender.startThread(threadName + " Sender");
			receiver.startThread(threadName + " Receiver");
		} else {
			throw new IllegalStateException("link port failed to open");
		}
	}

	public void stopLink() throws IOException, InterruptedException {
		sender.stopThread();
		receiver.stopThread();
		stopThread();

		joinThreads();
		// This should really be in a finally block somewhere
		// but the problem is where. You need to wait for everything
		// to shut down before you can close the port.

		System.out.println(threadName + ": closing the link port");
		commPort.close();
	}

	public void joinThreads() throws InterruptedException {
		sender.threadJoin();
		receiver.threadJoin();
		threadJoin();
	}

	// --------------------------------------------------------------------------------

	public void threadJoin() throws InterruptedException {
		thread.join();
	}

	// --------------------------------------------------------------------------------
	// Thread control for the main link thread

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
		} else {
			throw new IllegalStateException();
		}
	}

	public void stopThread() {
		threadControl = ThreadControlEnum.STOP;
		thread.interrupt();
	}

	protected synchronized void checkThreadControl()
			throws InterruptedException {
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

	protected boolean keepAliveOk() {
		return ((SystemTimeSystem.currentTime() - lastKeepAliveReceivedTime) < IM_ALIVE_TIMEOUT);
	}

	protected long timeToNextKeepAlive() {
		return ((lastKeepAliveSentTime + KEEP_ALIVE_INTERVAL) - SystemTimeSystem.currentTime());
	}

	// --------------------------------------------------------------------------------
	// These methods are used for the receive side to communicate to the
	// link important information about how the link is working

	// slave to master
	protected void receivedNeedDoPrepare(AbstractChannel rchannel,
			LinkMessage message) {
		throw new IllegalStateException();
	}

	protected void receivedDidPrepare(AbstractChannel rchannel,
			LinkMessage message) {
		throw new IllegalStateException();
	}

	protected void receivedDidProceed(AbstractChannel rchannel,
			LinkMessage message) {
		throw new IllegalStateException();
	}

	// master to slave
	protected void receivedDoPrepare(AbstractChannel rchannel,
			LinkMessage message) {
		throw new IllegalStateException();
	}

	protected void receivedDoProceed(AbstractChannel rchannel,
			LinkMessage message) {
		throw new IllegalStateException();
	}

	// both directions
	protected void receivedImAlive(AbstractChannel rchannel, LinkMessage message) {
		throw new IllegalStateException();
	}

	// if receive problems
	protected abstract void receiveReceiverException(Exception e);

	// --------------------------------------------------------------------------------
	// Interaction with the sender and receiver

	public abstract boolean isSendableChannel (AbstractChannel channel);
	
	public abstract boolean isReceivableChannel (AbstractChannel channel);
	
	public abstract boolean isForceInitialSequenceNumbers();

	// --------------------------------------------------------------------------------

	protected void linkWait(long timeout) throws InterruptedException {
		// System.out.println(getRole() + " start wait");
		wait(timeout);
		// System.out.println(getRole() + " end wait");
	}

	// --------------------------------------------------------------------------------

	public abstract String getRole();

}
