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
		
		LinkActiveState;
	}

	protected LinkStateEnum linkState;
	
	//--------------------------------------------------------------------------------
	// Constructors

	public MasterLink (LinkPortInterface port) {
		super(port);
		setLinkState(LinkStateEnum.LinkInitState);
		
		// the protocol's channel will be set when the protocol is associated with a channel
		iprotocol = new LinkControlProtocol(null, LinkControlProtocol.CommControlRoleEnum.MASTER);
		oprotocol = new LinkControlProtocol(null, LinkControlProtocol.CommControlRoleEnum.MASTER);

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
			setLinkState(LinkStateEnum.LinkInitState);
			resetSequenceNumbers();
			
			// --------------------
			// start off by sending a DO_PREPARE
//			resetSequenceNumbers();
			oprotocol.sendDoPrepare();
			setLinkState(LinkStateEnum.LinkSentDoPrepareState);
			wait(DID_PREPARE_TIMEOUT);

			// --------------------
			// if we got a DID_PREPARE, then send a DO_PROCEED
			if (linkState == LinkStateEnum.LinkReceivedDidPrepareState) {
				oprotocol.sendDoProceed();
				setLinkState(LinkStateEnum.LinkSentDoProceedState);
				wait(DID_PROCEED_TIMEOUT);
			}
			else {
				// failure, start over
				continue;
			}

			// --------------------
			// if we got a DID_PROCEED, then the link is happy
			if (linkState == LinkStateEnum.LinkReceivedDidProceedState) {
				setLinkState(LinkStateEnum.LinkActiveState);
				lastKeepAliveReceivedTime = Clock.clockRead();
				while (linkState == LinkStateEnum.LinkActiveState) {
					// make sure you have recently received a keep alive message
					// also, you could be awakened by receiving a keep alive so 
					// keep track of how long you need to wait to send a keep alive
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
		case LinkActiveState:
			break;
		// otherwise, out of sync
		default:
			setLinkState(LinkStateEnum.LinkInitState);
			notify();
			break;
		}
		lastKeepAliveReceivedTime = Clock.clockRead();
	}
	
	// --------------------------------------------------------------------------------
	// The receiver calls this when it detects an error
	
	protected synchronized void receiveReceiverException (Exception e) {
		System.out.println("Master Link Receive Exception");
		setLinkState(LinkStateEnum.LinkInitState);
		notify();
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