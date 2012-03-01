package com.builditboys.robots.communication;

import java.io.IOException;

public interface LinkPortInterface {
	
	public byte readByte ()  throws InterruptedException;
	
	public void writeByte (byte bite) throws InterruptedException, IOException;
	
	public void close () throws IOException;
}
