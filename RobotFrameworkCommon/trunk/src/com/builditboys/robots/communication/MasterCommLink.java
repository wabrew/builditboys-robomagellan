package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.CommParameters.COMM_CONTROL_CHANNEL_NUMBER;

public class MasterCommLink extends AbstractCommLink {

	private enum LinkStateEnum {
		LinkInitState,
		LinkSentDoPrepare,
		LinkSentDoProceed,
		LinkActiveState;
	}

	protected LinkStateEnum state;

	protected InputChannel masterIn;
	protected OutputChannel masterOut;
	
	//--------------------------------------------------------------------------------
	// Constructors

	public MasterCommLink (CommPortInterface port) {
		super(port);
		state = LinkStateEnum.LinkInitState;
		
		// the protocol's channel will be set when the protocol is associated with a channel
		CommControlProtocol iprotocol = new CommControlProtocol(null, CommControlProtocol.CommControlRoleEnum.MASTER);
		CommControlProtocol oprotocol = new CommControlProtocol(null, CommControlProtocol.CommControlRoleEnum.MASTER);

		masterIn = new InputChannel(iprotocol, COMM_CONTROL_CHANNEL_NUMBER);
		masterOut = new OutputChannel(oprotocol, COMM_CONTROL_CHANNEL_NUMBER);
		AbstractChannel.pairChannels(masterIn, masterOut);
		inputChannels.addChannel(masterIn);
		outputChannels.addChannel(masterOut);
	}
	
	// --------------------------------------------------------------------------------
	// Do some work, the top level, gets called in a loop

	public void doWork () throws InterruptedException {
		Thread.sleep(1000);
	}
/*
	// the manager then negotiates the connection
	// manager needs to queue messages, and look at replies
	// channels need to send the messages to someone

	master 
	send a do prepare and then wait for either a time out or a did prepare
	   	need to ignore received garbage
		if time out, do it again
		if did prepare, send a do proceed and then wait for time out our did proceed
			if time out, start over
			if did proceed, then become active and start doing keep alives
 */

}