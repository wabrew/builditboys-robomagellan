package com.builditboys.robots.communication;

import static com.builditboys.robots.communication.CommParameters.COMM_CONTROL_CHANNEL_NUMBER;

public class SlaveCommLink extends AbstractCommLink implements Runnable {
	
	private enum LinkStateEnum {	
		LinkInitState,
		LinkNeedsDoPrepare,
		LinkSentDidPrepare,
		LinkActiveState;
	}

	protected LinkStateEnum state;
	
	protected InputChannel slaveIn;
	protected OutputChannel slaveOut;
	
	//--------------------------------------------------------------------------------
	// Constructors

	public SlaveCommLink (CommPortInterface port) {
		super(port);
		state = LinkStateEnum.LinkInitState;
		
		// the protocol's channel will be set when the protocol is associated with a channel
		CommControlProtocol iprotocol = new CommControlProtocol(null, CommControlProtocol.CommControlRoleEnum.SLAVE);
		CommControlProtocol oprotocol = new CommControlProtocol(null, CommControlProtocol.CommControlRoleEnum.SLAVE);
	
		slaveIn = new InputChannel(iprotocol, COMM_CONTROL_CHANNEL_NUMBER);
		slaveOut = new OutputChannel(oprotocol, COMM_CONTROL_CHANNEL_NUMBER);
		AbstractChannel.pairChannels(slaveIn, slaveOut);
		inputChannels.addChannel(slaveIn);
		outputChannels.addChannel(slaveOut);
	}

	// --------------------------------------------------------------------------------
	// Do some work, the top level, gets called in a loop

	public void doWork () throws InterruptedException {
		Thread.sleep(1000);
	}


}
