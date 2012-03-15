package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

import com.builditboys.robots.communication.AbstractProtocol.ProtocolRoleEnum;
import com.builditboys.robots.time.SystemTimeSystem;

public class MasterLink extends AbstractLink {
	
	//--------------------------------------------------------------------------------
	// Constructors

	public MasterLink (String nm, LinkPortInterface port) {
		super("Master", nm, port);
		setLinkState(LinkStateEnum.LinkInitState);
		
		LinkControlProtocol.addProtocolToLink(this, ProtocolRoleEnum.MASTER);
		
		controlChannelIn = getInputChannelByProtocol(LinkControlProtocol.getRepresentative());
		linkInputControlProtocol = (LinkControlProtocol) controlChannelIn.getProtocol();

		controlChannelOut = getOutputChannelByProtocol(LinkControlProtocol.getRepresentative());
		linkOutputControlProtocol = (LinkControlProtocol) controlChannelOut.getProtocol();
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
			linkOutputControlProtocol.sendDoPrepare(false);
			setLinkState(LinkStateEnum.LinkSentDoPrepareState);
			linkWait(DID_PREPARE_TIMEOUT);


			// --------------------
			// if we got a DID_PREPARE, then send a DO_PROCEED
			if (linkState == LinkStateEnum.LinkReceivedDidPrepareState) {
				linkOutputControlProtocol.sendDoProceed(false);
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
				lastKeepAliveReceivedTime = SystemTimeSystem.currentTime();
				while ((linkState == LinkStateEnum.LinkReadyState)
						|| (linkState == LinkStateEnum.LinkActiveState)) {
					// make sure you have recently received a keep alive message
					// also, you could be awakened by receiving a keep alive so 
					// keep track of how long you need to wait to send a keep alive
					if (keepAliveOk()) {
						long timeToNextSend = timeToNextKeepAlive();
						if (timeToNextSend <= 0) {
							linkOutputControlProtocol.sendKeepAlive();
							lastKeepAliveSentTime = SystemTimeSystem.currentTime();
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
	// Master message receivers
	
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
		lastKeepAliveReceivedTime = SystemTimeSystem.currentTime();
	}
	
	// --------------------------------------------------------------------------------
	// Interaction with the sender and receiver

	public synchronized boolean isForceInitialSequenceNumbers () {
		switch (linkState) {
		case LinkInitState:
		case LinkSentDoPrepareState:
			return true;
		default:
			return false;
		}
	}

}