package testcommunication;

import com.builditboys.robots.communication.AbstractChannel;
import com.builditboys.robots.communication.LinkControlProtocol;
import com.builditboys.robots.communication.LinkMessage;
import com.builditboys.robots.communication.LinkPortInterface;
import com.builditboys.robots.communication.MasterLink;
import com.builditboys.robots.communication.Receiver;
import com.builditboys.robots.communication.Sender;
import com.builditboys.robots.communication.SlaveLink;

public class TestCommunications {

	public static void main(String args[]) {
		testCommThreads();
//		testCommMessages();
	}

	static void testCommThreads() {
		DebuggingCommPortBuffer buffer = new DebuggingCommPortBuffer();
		LinkPortInterface port1 = buffer.getPort1();
		LinkPortInterface port2 = buffer.getPort2();
		
		MasterLink masterLink = new MasterLink(port1);
		SlaveLink slaveLink = new SlaveLink(port2);
		
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

		masterLink.startThreads("Test");
		slaveLink.startThreads("Test");

		System.out.println("Links started");
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		masterLink.stopThreads();
		slaveLink.stopThreads();

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

	static void testCommMessages() throws InterruptedException {
		DebuggingCommPortBuffer buffer = new DebuggingCommPortBuffer();
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

}
