package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

import com.builditboys.robots.time.*;

public class MasterLink extends AbstractLink {

	private enum LinkStateEnum {
		LinkInitState,
		
		LinkSentDoPrepareState,
		LinkReceivedDidPrepareState,
		
		LinkSentDoProceedState,
		LinkReceivedDidProceedState,
		
		LinkReadyState,
		
		LinkActiveState;
	}

	protected LinkStateEnum linkState;
	
	//--------------------------------------------------------------------------------
	// Constructors

	public MasterLink (LinkPortInterface port) {
		super(port);
		setLinkState(LinkStateEnum.LinkInitState);
		
		// the protocol's channel will be set when the protocol is associated with a channel
		iprotocol = new LinkControlProtocol(LinkControlProtocol.CommControlRoleEnum.MASTER);
		oprotocol = new LinkControlProtocol(LinkControlProtocol.CommControlRoleEnum.MASTER);

		controlChannelIn = iprotocol.getInputChannel();
		controlChannelOut = oprotocol.getOutputChannel();

		AbstractChannel.pairChannels(controlChannelIn, controlChannelOut);

		inputChannels.addChannel(controlChannelIn);
		outputChannels.addChannel(controlChannelOut);
	}
	
	// --------------------------------------------------------------------------------
	
	public void enable() {
		if (linkState == LinkStateEnum.LinkReadyState) {
			linkState = LinkStateEnum.LinkActiveState;
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public void disable() {
		if (linkState == LinkStateEnum.LinkActiveState) {
			linkState = LinkStateEnum.LinkReadyState;
		}
		// for any other states, just do nothing since you are already
		// effectively disabled
	}


	// --------------------------------------------------------------------------------
	// Do some work, the top level, gets called in a loop
	
	// be sure to look at isSendableChannel, etc to understand how the various
	// states affect the actions of the sender and receiver

	public synchronized void doWork () throws InterruptedException {
		while (true) {
			setLinkState(LinkStateEnum.LinkInitState);
			
			// --------------------
			// start off by sending a DO_PREPARE
			oprotocol.sendDoPrepare(false);
			setLinkState(LinkStateEnum.LinkSentDoPrepareState);
			linkWait(DID_PREPARE_TIMEOUT);


			// --------------------
			// if we got a DID_PREPARE, then send a DO_PROCEED
			if (linkState == LinkStateEnum.LinkReceivedDidPrepareState) {
				oprotocol.sendDoProceed(false);
				setLinkState(LinkStateEnum.LinkSentDoProceedState);
				linkWait(DID_PROCEED_TIMEOUT);
			}
			else {
				// failure, start over
				continue;
			}

			// --------------------
			// if we got a DID_PROCEED, then the link is happy
			// just keep it that way
			if (linkState == LinkStateEnum.LinkReceivedDidProceedState) {
				setLinkState(LinkStateEnum.LinkReadyState);
				lastKeepAliveReceivedTime = Time.getAbsoluteTime();
				while ((linkState == LinkStateEnum.LinkReadyState)
						|| (linkState == LinkStateEnum.LinkActiveState)) {
					// make sure you have recently received a keep alive message
					// also, you could be awakened by receiving a keep alive so 
					// keep track of how long you need to wait to send a keep alive
					if (keepAliveOk()) {
						long timeToNextSend = timeToNextKeepAlive();
						if (timeToNextSend <= 0) {
							oprotocol.sendKeepAlive();
							lastKeepAliveSentTime = Time.getAbsoluteTime();
						}
						else {
							linkWait(timeToNextSend);
						}
					}
					else {
						System.out.println("Master Keep Alive Timout");
						break;
					}
				}
				// problems, time out or receive error, start over
				continue;
			}
			else {
				// failure, start over
				continue;
			}
		}
	}
	
	// --------------------------------------------------------------------------------
	// The receiver link control protocol calls this when it gets a link control message
	
	protected synchronized void receivedNeedDoPrepare (AbstractChannel channel, LinkMessage message) {
		switch (linkState) {
		// do prepare state discards everything but a did prepare
		case LinkInitState:
		case LinkSentDoPrepareState:
			break;
		// otherwise honor the request
		default:
			setLinkState(LinkStateEnum.LinkInitState);
			notify();
			break;
		}
	}
	
	protected synchronized void receivedDidPrepare (AbstractChannel channel, LinkMessage message) {
		switch (linkState) {
		// do prepare state discards everything but a did prepare
		// its satisfied here
		case LinkInitState:
		case LinkSentDoPrepareState:
			setLinkState(LinkStateEnum.LinkReceivedDidPrepareState);
			notify();
			break;
		// otherwise, out of sync
		default:
			setLinkState(LinkStateEnum.LinkInitState);
			notify();
			break;
		}
	}

	protected synchronized void receivedDidProceed (AbstractChannel channel, LinkMessage message) {
		switch (linkState) {
		// do prepare state discards everything but a did prepare
		case LinkInitState:
		case LinkSentDoPrepareState:
			break;
		// honor the request
		case LinkSentDoProceedState:
			setLinkState(LinkStateEnum.LinkReceivedDidProceedState);
			notify();
			break;
		// otherwise, out of sync
		default:
			setLinkState(LinkStateEnum.LinkInitState);
			notify();
			break;
		}
	}
	
	protected synchronized void receivedImAlive (AbstractChannel channel, LinkMessage message) {
		switch (linkState) {
		// do prepare state discards everything but a did prepare
		case LinkInitState:
		case LinkSentDoPrepareState:
			break;
		// stay active
		case LinkReadyState:
		case LinkActiveState:
			break;
		// otherwise, out of sync
		default:
			setLinkState(LinkStateEnum.LinkInitState);
			notify();
			break;
		}
		lastKeepAliveReceivedTime = Time.getAbsoluteTime();
	}
	
	// --------------------------------------------------------------------------------
	// The receiver calls this when it detects an error
	
	protected synchronized void receiveReceiverException (Exception e) {
		System.out.println("Master Link Receive Exception");
		setLinkState(LinkStateEnum.LinkInitState);
		notify();
	}
	
	// --------------------------------------------------------------------------------
	// Interaction with the sender and receiver

	public synchronized boolean isSendableChannel (AbstractChannel channel) {
//		synchronized (System.out){
//			System.out.println("master is sendable");
//			System.out.println(controlChannelOut);
//			System.out.println(controlChannelIn);
//			System.out.println(channel);
//		}
		return (channel == controlChannelOut) || (linkState == LinkStateEnum.LinkActiveState);
	}
	
	public synchronized boolean isReceivableChannel (AbstractChannel channel) {
//		synchronized (System.out){
//			System.out.println("master is receivable");
//			System.out.println(controlChannelOut);
//			System.out.println(controlChannelIn);
//			System.out.println(channel);
//		}
		return (channel == controlChannelIn) || (linkState == LinkStateEnum.LinkActiveState);
	}
	
	public synchronized boolean isForceInitialSequenceNumbers () {
		switch (linkState) {
		case LinkInitState:
		case LinkSentDoPrepareState:
			return true;
		default:
			return false;
		}
	}

	// --------------------------------------------------------------------------------

	public String getRole () {
		return "Master";
	}
	
	// --------------------------------------------------------------------------------

	private void setLinkState (LinkStateEnum state) {
//		System.out.println("Master -> " + state.toString());
		linkState = state;
	}


}