package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.LinkParameters.*;

import com.builditboys.robots.time.Clock;

public class SlaveLink extends AbstractLink implements Runnable {
	
	private enum LinkStateEnum {	
		LinkInitState,
		
		LinkSentNeedDoPrepareState,
		
		LinkReceivedDoPrepareState,
		LinkSentDidPrepareState,
		
		LinkReceivedDoProceedState,
		LinkSentDidProceedState,
		
		LinkReceivedImAliveState,
		
		LinkActiveState;
	}

	protected LinkStateEnum linkState;
	
	//--------------------------------------------------------------------------------
	// Constructors

	public SlaveLink (LinkPortInterface port) {
		super(port);
		setLinkState(LinkStateEnum.LinkInitState, "constructor");
		
		// the protocol's channel will be set when the protocol is associated with a channel
		iprotocol = new LinkControlProtocol(null, LinkControlProtocol.CommControlRoleEnum.SLAVE);
		oprotocol = new LinkControlProtocol(null, LinkControlProtocol.CommControlRoleEnum.SLAVE);
	
		controlChannelIn = new InputChannel(iprotocol, COMM_CONTROL_CHANNEL_NUMBER);
		controlChannelOut = new OutputChannel(oprotocol, COMM_CONTROL_CHANNEL_NUMBER);

		AbstractChannel.pairChannels(controlChannelIn, controlChannelOut);

		inputChannels.addChannel(controlChannelIn);
		outputChannels.addChannel(controlChannelOut);
	}

	// --------------------------------------------------------------------------------
	// Do some work, the top level, gets called in a loop

	public synchronized void doWork () throws InterruptedException {
		while (true) {
			setLinkState(LinkStateEnum.LinkInitState, "one");
			resetSequenceNumbers();
			
			// --------------------
			// start off by sending a NEED_DO_PREPARE
			oprotocol.sendNeedDoPrepare();
			setLinkState(LinkStateEnum.LinkSentNeedDoPrepareState, "two");
			wait(NEED_PREPARE_TIMEOUT);

			// --------------------
			// if we got a DO_PREPARE, then send DID_PREPARE
			if (linkState == LinkStateEnum.LinkReceivedDoPrepareState) {
				oprotocol.sendDidPrepare();
				setLinkState(LinkStateEnum.LinkSentDidPrepareState, "three");
				wait(DO_PROCEED_TIMEOUT);
			}
			else {
				// failure, start over
				continue;
			}

			// --------------------
			// if we got a DO_PROCEED, then send a DID_PROCEED
			if (linkState == LinkStateEnum.LinkReceivedDoProceedState) {
				oprotocol.sendDidProceed();
				setLinkState(LinkStateEnum.LinkSentDidProceedState, "four");
				wait(IM_ALIVE_TIMEOUT);
			}
			else {
				// failure, start over
				continue;
			}

			// --------------------
			// if we got an IM_ALIVE, then the link is happy
			if (linkState == LinkStateEnum.LinkReceivedImAliveState) {
				setLinkState(LinkStateEnum.LinkActiveState, "five");
				lastKeepAliveReceivedTime = Clock.clockRead();
				while (linkState == LinkStateEnum.LinkActiveState) {
					// make sure you have recently receive a keep alive message
					if (keepAliveOk()) {
						long timeToNextSend = timeToNextKeepAlive();
						if (timeToNextSend <= 0) {
							oprotocol.sendKeepAlive();
							lastKeepAliveSentTime = Clock.clockRead();
						}
						else {
							wait(timeToNextSend);
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
	// The receiver link control protocol calls this when it gets a link control message
	
	protected synchronized void receivedDoPrepare (AbstractChannel channel, LinkMessage message) {
		switch (linkState) {
		// need prepare state discards everything but a do prepare
		// its satisfied here
		case LinkInitState:
		case LinkSentNeedDoPrepareState:
			setLinkState(LinkStateEnum.LinkReceivedDoPrepareState, "six");
			notify();
			break;
		// otherwise, out of sync
		default:
			setLinkState(LinkStateEnum.LinkInitState, "seven");
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
			setLinkState(LinkStateEnum.LinkReceivedDoProceedState, "eight");
			notify();
			break;
		// otherwise, out of sync
		default:
			setLinkState(LinkStateEnum.LinkInitState, "nine");
			notify();
			break;
		}		
	}
		
	// only the master should receive this
	protected void receivedNeedDoPrepare (AbstractChannel channel, LinkMessage message) {
		throw new IllegalStateException();
	}
	
	// only the master should receive this
	protected void receivedDidPrepare (AbstractChannel channel, LinkMessage message) {
		throw new IllegalStateException();
		
	}

	// only the master should receive this
	protected void receivedDidProceed (AbstractChannel channel, LinkMessage message) {
		throw new IllegalStateException();
	}

	
	protected synchronized void receivedImAlive (AbstractChannel channel, LinkMessage message) {
		switch (linkState) {
		// need prepare state discards everything but a do prepare
		case LinkSentNeedDoPrepareState:
			break;
		// honor the request, go active
		case LinkSentDidProceedState:
			setLinkState(LinkStateEnum.LinkReceivedImAliveState, "ten");
			notify();
			break;
		// stay active
		case LinkActiveState:
			break;
		// otherwise, out of sync
		default:
			setLinkState(LinkStateEnum.LinkInitState, "eleven");
			notify();
			break;
		}
		lastKeepAliveReceivedTime = Clock.clockRead();
	}
		
	// --------------------------------------------------------------------------------
	// The receiver calls this when it detects an error
	
	protected synchronized void receiveReceiverException (Exception e) {
		System.out.println("Slave Link Receive Exception");
		setLinkState(LinkStateEnum.LinkInitState, "twelve");
		notify();
	}
	
	// --------------------------------------------------------------------------------

	public String getRole () {
		return "Slave ";
	}
	
	// --------------------------------------------------------------------------------

	private void setLinkState (LinkStateEnum state, String where) {
//		System.out.println("Slave  -> " + state.toString() + " in " + where);
		linkState = state;
	}

}
