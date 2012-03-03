package testsystem;

import java.io.IOException;

import com.builditboys.robots.system.WindowsRobotSystem;

public class TestSystem {
	
	
	public static void main (String args[]) {
		System.out.println("Starting");
		
		WindowsRobotSystem robosys = null;
		try {
			robosys = WindowsRobotSystem.launchWindowsRobotSystem("Test");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			robosys.stopRobotSystem();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Finished");
		
	}

}
