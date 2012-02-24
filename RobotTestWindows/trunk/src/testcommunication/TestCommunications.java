package testcommunication;

import com.builditboys.robots.communication.AbstractChannel;
import com.builditboys.robots.communication.CommControlProtocol;
import com.builditboys.robots.communication.CommMessage;
import com.builditboys.robots.communication.CommPortInterface;
import com.builditboys.robots.communication.MasterCommLink;
import com.builditboys.robots.communication.Receiver;
import com.builditboys.robots.communication.Sender;
import com.builditboys.robots.communication.SlaveCommLink;

public class TestCommunications {

	public static void main(String args[]) {
		testCommThreads();
//		testCommMessages();
	}

	static void testCommThreads() {
		DebuggingCommPortBuffer buffer = new DebuggingCommPortBuffer();
		CommPortInterface port1 = buffer.getPort1();
		CommPortInterface port2 = buffer.getPort2();
		
		MasterCommLink masterLink = new MasterCommLink(port1);
		SlaveCommLink slaveLink = new SlaveCommLink(port2);
		
		AbstractChannel masterOut = masterLink.getOutputChannelByProtocol(CommControlProtocol.indicator);
		CommControlProtocol masterOutProto = (CommControlProtocol) masterOut.getProtocol();
//		System.out.println(masterOut);
//		System.out.println(masterOutProto);

		AbstractChannel masterIn = masterLink.getInputChannelByProtocol(CommControlProtocol.indicator);
		CommControlProtocol masterInProto = (CommControlProtocol) masterIn.getProtocol();
//		System.out.println(masterIn);
//		System.out.println(masterInProto);

		AbstractChannel slaveOut = slaveLink.getOutputChannelByProtocol(CommControlProtocol.indicator);
		CommControlProtocol slaveOutProto = (CommControlProtocol) slaveOut.getProtocol();
//		System.out.println(slaveOut);
//		System.out.println(slaveOutProto);

		AbstractChannel slaveIn = slaveLink.getInputChannelByProtocol(CommControlProtocol.indicator);
		CommControlProtocol slaveInProto = (CommControlProtocol) slaveIn.getProtocol();
//		System.out.println(slaveIn);
//		System.out.println(slaveInProto);

		masterLink.startThreads("Test");
		slaveLink.startThreads("Test");

		System.out.println("Links started");

		masterOutProto.sendDoPrepare();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		masterLink.stopThreads();
		slaveLink.stopThreads();

		System.out.println("waiting to join");
		try {
			masterLink.threadJoin();
			slaveLink.threadJoin();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Links stopped");
	}

	// --------------------------------------------------------------------------------

	static void testCommMessages() throws InterruptedException {
		DebuggingCommPortBuffer buffer = new DebuggingCommPortBuffer();
		CommPortInterface port1 = buffer.getPort1();
		CommPortInterface port2 = buffer.getPort2();

		CommPortInterface thisPort = port1;
		
		MasterCommLink link = new MasterCommLink(thisPort);
		CommMessage message1 = new CommMessage(5);
		CommMessage message2 = new CommMessage(5);
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
