package com.builditboys.robots.system;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.builditboys.robots.communication.AbstractLink;
import com.builditboys.robots.communication.AbstractProtocol;
import com.builditboys.robots.infrastructure.ParameterServer;
import com.builditboys.robots.robomagellan.AbstractRoboMagellanRobotSystem;
import com.builditboys.robots.system.RobotState.RobotModeEnum;
import com.builditboys.robots.time.TimeSyncProtocol;

import static com.builditboys.robots.system.TestWindowsRobotSystem.*;
import static com.builditboys.robots.system.AbstractRobotSystem.*;

public class SimpleGUI extends JFrame {
	private JPanel panel;
	
	private boolean robotStarted = false;
	String message1 = "";
	
	public SimpleGUI() {
		initUI();
	}

	//--------------------------------------------------------------------------------

	private final void initUI() {

		panel = new JPanel();
		getContentPane().add(panel);

		panel.setLayout(new FlowLayout());
//		panel.setToolTipText("A Panel container");

		addStartButton();
		addStopButton();
		addExitButton();
		addShowParametersButton();
		addShowRobotStateButton();
		addEnableLinkButton();
		addSetClockButton();
		addCorrespondTimeButton();
		addSetModeButton();
		addExampleButton();
		
		setTitle("Robot Controller");
		setSize(300, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//--------------------------------------------------------------------------------

	public void paint (Graphics g) {
		g.drawString(message1, 6, 80);
	}

	//--------------------------------------------------------------------------------

	private void addStartButton () {
		JButton button = new JButton("Launch");
		button.setBounds(100, 60, 100, 30);
		button.setToolTipText("Launch the robot");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!robotStarted) {
					System.out.println("Launch robot");
					launchTestWindowsRobotSystemRunnable();
					robotStarted = true;
				}
				else {
					System.out.println("The robot has already been started; can't restart");
				}
			}
		});
		panel.add(button);	
	}
	
	private void addStopButton () {
		JButton button = new JButton("Stop");
		button.setBounds(100, 60, 100, 30);
		button.setToolTipText("Stop the robot");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (robotStarted) {
					System.out.println("Stopping the robot");
					stopTheRobotSystemRunnable();
				}
				else {
					System.out.println("The robot has not been stared; can't stop");
				}
			}
		});
		panel.add(button);
	}
	
	private void addExitButton () {
		JButton button = new JButton("Exit");
		button.setBounds(100, 60, 100, 30);
		button.setToolTipText("Exit the robot controller");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (robotStarted) {
					System.out.println("Stopping the robot");
					stopTheRobotSystemRunnable();					
				}
				System.out.println("Exiting the robot controller");
				System.exit(0);
			}
		});
		panel.add(button);
	}
	
	private void addShowParametersButton () {
		JButton button = new JButton("Parameters");
		button.setBounds(100, 60, 100, 30);
		button.setToolTipText("Show the parameter server parameters");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ParameterServer.print();
			}
		});
		panel.add(button);
	}
	
	private void addShowRobotStateButton () {
		JButton button = new JButton("Robot State");
		button.setBounds(100, 60, 100, 30);
		button.setToolTipText("Show the robot state");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				AbstractRoboMagellanRobotSystem.printState();
			}
		});
		panel.add(button);
	}

	private void addEnableLinkButton () {
		JButton button = new JButton("Enable Link");
		button.setBounds(100, 60, 100, 30);
		button.setToolTipText("Enable the link for normal traffic");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				AbstractLink link = AbstractLink.getParameter("ROBOT_LINK");
				link.enable();
			}
		});
		panel.add(button);
	}

	private void addSetClockButton () {
		JButton button = new JButton("Reset Slave Clock");
		button.setBounds(100, 60, 100, 30);
		button.setToolTipText("Reset the slave's clock to 0");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				AbstractLink link = AbstractLink.getParameter("ROBOT_LINK");
				TimeSyncProtocol proto = TimeSyncProtocol.getLinkOutputProtocol(link);
				try {
					proto.sendResetClock(false);
				} catch (InterruptedException e) {
					System.out.println("Clock set interrupted");
					e.printStackTrace();
				}
			}
		});
		panel.add(button);
	}
	
	private void addCorrespondTimeButton () {
		JButton button = new JButton("Corrspond Times");
		button.setBounds(100, 60, 100, 30);
		button.setToolTipText("Correspond the slaves local time to master's");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				AbstractLink link = AbstractLink.getParameter("ROBOT_LINK");
				TimeSyncProtocol proto = TimeSyncProtocol.getLinkOutputProtocol(link);
				try {
					proto.sendCorrespondTime(false);
				} catch (InterruptedException e) {
					System.out.println("Correspond time interrupted");
					e.printStackTrace();
				}
			}
		});
		panel.add(button);
	}
	
	private void addSetModeButton () {
		JButton button = new JButton("Set Safe Mode");
		button.setBounds(100, 60, 100, 30);
		button.setToolTipText("Set the robot to safe mode");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				AbstractLink link = AbstractLink.getParameter("ROBOT_LINK");
				RobotControlProtocol proto = RobotControlProtocol.getLinkOutputProtocol(link);
				try {
					proto.sendSetMode(RobotModeEnum.SAFE_MODE, false);
				} catch (InterruptedException e) {
					System.out.println("Set safe mode interrupted");
					e.printStackTrace();
				}
			}
		});
		panel.add(button);
	}
	
	
	
	
	
	private void addExampleButton () {
		JButton button = new JButton("Example");
		button.setBounds(100, 60, 100, 30);
		button.setToolTipText("Example");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				message1 = "foo";
				repaint();
			}
		});
		panel.add(button);
	}
	
	
	
	//--------------------------------------------------------------------------------


}
