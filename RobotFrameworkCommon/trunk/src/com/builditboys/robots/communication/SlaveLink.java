package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

import com.builditboys.robots.communication.AbstractProtocol.ProtocolRoleEnum;
import com.builditboys.robots.time.SystemTimeSystem;

public class SlaveLink extends AbstractLink implements Runnable {
	
	//--------------------------------------------------------------------------------
	// Constructors

	public SlaveLink (String nm, LinkPortInterface port) {
		super("Slave", nm, port);
		setLinkState(LinkStateEnum.LinkInitState);
		
		LinkControlProtocol.addProtocolToLink(this, ProtocolRoleEnum.SLAVE);
		
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
			// wait a little to give the master a chance to start things
			// off before the slave starts chiming in
			linkWait(SLAVE_START_DELAY);

			// --------------------
			// if the master has not already started the init process
			// remind the master to do so by sending a NEED_DO_PREPARE
			// if the master did start things already, just fall through
			if (linkState != LinkStateEnum.LinkReceivedDoPrepareState) {
				linkOutputControlProtocol.sendNeedDoPrepare(false);
				setLinkState(LinkStateEnum.LinkSentNeedDoPrepareState);
				linkWait(NEED_PREPARE_TIMEOUT);
			}

			// --------------------
			// if we got a DO_PREPARE, then send DID_PREPARE
			if (linkState == LinkStateEnum.LinkReceivedDoPrepareState) {
				// master told us to reset, so we do
				linkOutputControlProtocol.sendDidPrepare(false);
				setLinkState(LinkStateEnum.LinkSentDidPrepareState);
				linkWait(DO_PROCEED_TIMEOUT);
			}
			else {
				// failure, start over
				continue;
			}

			// --------------------
			// if we got a DO_PROCEED, then send a DID_PROCEED
			if (linkState == LinkStateEnum.LinkReceivedDoProceedState) {
				linkOutputControlProtocol.sendDidProceed(false);
				setLinkState(LinkStateEnum.LinkSentDidProceedState);
				linkWait(IM_ALIVE_TIMEOUT);
			}
			else {
				// failure, start over
				continue;
			}

			// --------------------
			// if we got an IM_ALIVE, then the link is happy
			// just keep it that way
			if (linkState == LinkStateEnum.LinkReceivedImAliveState) {
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
						System.out.println("Slave Keep Alive Timout");
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
	// Slave message receivers
	
	protected synchronized void receivedDoPrepare (AbstractChannel channel, LinkMessage message) {
		switch (linkState) {
		// need prepare state discards everything but a do prepare
		// its satisfied here
		case LinkInitState:
		case LinkSentNeedDoPrepareState:
			setLinkState(LinkStateEnum.LinkReceivedDoPrepareState);
			notify();
			break;
		// otherwise, out of sync
		default:
			setLinkState(LinkStateEnum.LinkInitState);
			notify();
			break;
		}		
	}
	
	protected synchronized void receivedDoProceed (AbstractChannel channel, LinkMessage message) {
		switch (linkState) {
		// need prepare state discards everything but a do prepare
		case LinkInitState:
		case LinkSentNeedDoPrepareState:
			break;
		case LinkSentDidPrepareState:
			setLinkState(LinkStateEnum.LinkReceivedDoProceedState);
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
		// need prepare state discards everything but a do prepare
		case LinkSentNeedDoPrepareState:
			break;
		// honor the request, go active
		case LinkSentDidProceedState:
			setLinkState(LinkStateEnum.LinkReceivedImAliveState);
			notify();
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
		case LinkSentNeedDoPrepareState:
			return true;
		default:
			return false;
		}
	}
	
}
