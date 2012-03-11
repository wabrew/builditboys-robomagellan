package com.builditboys.robots.communication;

import com.builditboys.robots.utilities.FillableBuffer;

public abstract class AbstractProtocolMessage {
	
	private int length;
	
	protected AbstractProtocolMessage (int len) {
		length = len;
	}
	
	public int getLength() {
		return length;
	}

	protected abstract void deConstruct (FillableBuffer buff);
	
	protected abstract void reConstruct (FillableBuffer buff);

}
