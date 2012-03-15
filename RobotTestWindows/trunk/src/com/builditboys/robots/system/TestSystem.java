package com.builditboys.robots.system;

import javax.swing.SwingUtilities;

public class TestSystem {
	
	public static void main (String args[]) {
		System.out.println("Main thread starting");	
		
		Configuration.loadConfigurationFile("foo");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SimpleGUI gui = new SimpleGUI();
				gui.setVisible(true);
			}
		});
		
		System.out.println("Main thread finished");
	}

}
