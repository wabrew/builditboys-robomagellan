package com.builditboys.robots.communication;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;

import com.builditboys.robots.communication.AbstractChannel;
import com.builditboys.robots.communication.LinkControlProtocol;
import com.builditboys.robots.communication.LinkPortInterface;
import com.builditboys.robots.communication.MasterLink;
import com.builditboys.robots.communication.SlaveLink;
import com.builditboys.robots.communication.WindowsLinkPort;

public class TestCommunications {

	public static void main(String args[]) throws NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException {
//		xxtestLinkThreadsComm();
		System.out.println("Broken, needs to be updated to use system, dies because system intance is not setup");
		try {
			testLinkThreadsComm();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		testLinkThreadsSelf();
//		testLinkMessages();
	}

	static void testLinkThreadsSelf() throws IOException, InterruptedException {
		DebuggingLinkPortBuffer buffer = new DebuggingLinkPortBuffer();
		LinkPortInterface port1 = buffer.getPort1();
		LinkPortInterface port2 = buffer.getPort2();
		
		MasterLink masterLink = new MasterLink("Test Master Link", port1);
		SlaveLink slaveLink = new SlaveLink("Test Slave Link", port2);
		
		AbstractChannel masterOut = masterLink.getOutputChannelByProtocol(LinkControlProtocol.indicator);
		LinkControlProtocol masterOutProto = (LinkControlProtocol) masterOut.getProtocol();
//		System.out.println(masterOut);
//		System.out.println(masterOutProto);

		AbstractChannel masterIn = masterLink.getInputChannelByProtocol(LinkControlProtocol.indicator);
		LinkControlProtocol masterInProto = (LinkControlProtocol) masterIn.getProtocol();
//		System.out.println(masterIn);
//		System.out.println(masterInProto);

		AbstractChannel slaveOut = slaveLink.getOutputChannelByProtocol(LinkControlProtocol.indicator);
		LinkControlProtocol slaveOutProto = (LinkControlProtocol) slaveOut.getProtocol();
//		System.out.println(slaveOut);
//		System.out.println(slaveOutProto);

		AbstractChannel slaveIn = slaveLink.getInputChannelByProtocol(LinkControlProtocol.indicator);
		LinkControlProtocol slaveInProto = (LinkControlProtocol) slaveIn.getProtocol();
//		System.out.println(slaveIn);
//		System.out.println(slaveInProto);

		masterLink.startLink();
		slaveLink.startLink();

		System.out.println("Links started");
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		masterLink.stopLink();
		slaveLink.stopLink();

		System.out.println("waiting to join");
		try {
			masterLink.joinThreads();
			slaveLink.joinThreads();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Links stopped");
	}

	// --------------------------------------------------------------------------------

	
	static void xxtestLinkThreadsComm() throws NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException {
		WindowsLinkPort port1 = new WindowsLinkPort("COM10", 115200, true);
		port1.open();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		port1.close();
	}
	
	static void testLinkThreadsComm() throws NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException, InterruptedException {
		WindowsLinkPort port1 = new WindowsLinkPort("COM10", 115200, true);	
		MasterLink masterLink = new MasterLink("Test Master Link", port1);
		
		try {
			masterLink.startLink();
			System.out.println("Link started");
		
			Thread.sleep(5000);
		}
		catch (Exception e) {
			System.out.println("Catching top level error " + e);
			e.printStackTrace();
		}

		System.out.println("waiting to stop");
		masterLink.stopLink();

		System.out.println("Link stopped");
	}

	// --------------------------------------------------------------------------------

/*
	static void testLinkMessages() throws InterruptedException, IOException {
		DebuggingLinkPortBuffer buffer = new DebuggingLinkPortBuffer();
		LinkPortInterface port1 = buffer.getPort1();
		LinkPortInterface port2 = buffer.getPort2();

		LinkPortInterface thisPort = port1;
		
		MasterLink link = new MasterLink(thisPort);
		LinkMessage message1 = new LinkMessage(5);
		LinkMessage message2 = new LinkMessage(5);
		Sender sender = new Sender(link, thisPort);
		Receiver receiver = new Receiver(link, thisPort);

		message1.addByte((byte) 55);
		message1.addByte((byte) 56);
		message1.addByte((byte) 57);
		sender.sendMessage(message1);

		message2.addByte((byte) 1);
		message2.addByte((byte) 0xFF);
		message2.addByte((byte) 3);
		sender.sendMessage(message2);

		System.out.println();

		receiver.receiveMessage();
		receiver.receiveMessage();

	}
*/

}
