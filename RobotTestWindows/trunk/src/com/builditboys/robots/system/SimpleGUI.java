package com.builditboys.robots.system;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import static com.builditboys.robots.system.WindowsRobotSystem.*;

public class SimpleGUI extends JFrame {
	private boolean robotStarted = false;
	
	public SimpleGUI() {
		initUI();
	}

	//--------------------------------------------------------------------------------

	public final void initUI() {

		JPanel panel = new JPanel();
		getContentPane().add(panel);

		panel.setLayout(new FlowLayout());
//		panel.setToolTipText("A Panel container");

		JButton startButton = new JButton("Start");
		startButton.setBounds(100, 60, 100, 30);
		startButton.setToolTipText("Start the robot");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!robotStarted) {
					System.out.println("Starting robot");
					launchWindowsRobotSystemRunnable("foo");
					robotStarted = true;
				}
				else {
					System.out.println("The robot has already been started; can't restart");
				}
			}
		});
		panel.add(startButton);
		
		JButton stopButton = new JButton("Stop");
		stopButton.setBounds(100, 60, 100, 30);
		stopButton.setToolTipText("Stop the robot");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (robotStarted) {
					System.out.println("Stopping the robot");
					stopWindowsRobotSystemRunnable();
				}
				else {
					System.out.println("The robot has not been stared; can't stop");
				}
			}
		});
		panel.add(stopButton);
		
		JButton quitButton = new JButton("Exit");
		quitButton.setBounds(100, 60, 100, 30);
		quitButton.setToolTipText("Exit the robot controller");
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (robotStarted) {
					System.out.println("Stopping the robot");
					stopWindowsRobotSystemRunnable();					
				}
				System.out.println("Exiting the robot controller");
				System.exit(0);
			}
		});
		panel.add(quitButton);

		setTitle("Robot Controller");
		setSize(300, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}
