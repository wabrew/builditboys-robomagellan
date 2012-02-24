package com.builditboys.robots.communication;

public interface CommPortInterface {
	
	public byte readByte ()  throws InterruptedException;
	
//	public byte[] readBytes ();
	
	public void writeByte (byte bite) throws InterruptedException;
	
	public void writeBytes (byte bites[]) throws InterruptedException;
	
	public void tossInput ();
	
	public void tossOutput ();

}
