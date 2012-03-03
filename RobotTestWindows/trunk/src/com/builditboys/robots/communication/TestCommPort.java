package com.builditboys.robots.communication;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;

import com.builditboys.robots.communication.WindowsCommPort;

public class TestCommPort {
	
	
	public static void main (String args[]) throws NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException, InterruptedException {
		WindowsCommPort commPort = new WindowsCommPort("COM10", 9600, true);
		

		System.out.println("Writing");
		commPort.write("12345");
		System.out.println("Wrote");
		Thread.sleep(1000);

		System.out.println("Reading");
		for (int i = 0; i < 20; i++) {
			System.out.println(commPort.bufferedRead());
		}
		System.out.println("Read");
		
		
		commPort.close();
		System.out.println("Done");
	}
	


}
