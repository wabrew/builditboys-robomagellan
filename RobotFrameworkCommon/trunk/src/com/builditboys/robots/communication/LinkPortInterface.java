package com.builditboys.robots.communication;


import java.io.IOException;

public interface LinkPortInterface {
	
	public void open () throws IOException;
	
	public void close () throws IOException;
	
	public boolean isOpen ();

	public byte readByte ()  throws InterruptedException;
	
	public void writeByte (byte bite) throws InterruptedException, IOException;
	
}
