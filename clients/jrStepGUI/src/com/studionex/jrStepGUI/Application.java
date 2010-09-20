package com.studionex.jrStepGUI;

/*
 * Copyright 2010 Jean-Louis Paquelin
 * 
 * This file is part of jrStepGUI.
 * 
 * jrStepGUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * jrStepGUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jrStepGUI.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact info: jrstepgui@studionex.com
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.studionex.jrStepGUI.rStep.RStep;
import com.studionex.jrStepGUI.rStep.RStepEvent;
import com.studionex.jrStepGUI.rStep.RStepEventListener;
import com.studionex.jrStepGUI.rStep.RStepPlayerEvent;

@SuppressWarnings("serial")
public class Application extends JFrame implements RStepEventListener {
	private RStep rStep;
	
	private MainJPanel mainJPanel;
	/**
	 * Launches the application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Application();
			}
		});
	}
	
	public Application() {
		rStep = new RStep();
		rStep.addEventListener(this);
        		
		setNativeLookAndFeel();
		
		// open a SerialJDialog that allows to choose and open
		// the serial port on which rStep communicates
		SerialJDialog serialJDialog = new SerialJDialog(this);
		serialJDialog.setVisible(true);
		
		if(!rStep.isConnected())
			System.exit(1);

		mainJPanel = new MainJPanel(this);
		mainJPanel.setUIState(MainJPanel.UIStates.MANUAL);
		this.getContentPane().add(mainJPanel);
		
		this.addWindowListener(new ApplicationWindowEventHandler());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		this.setTitle("JRStepGUI");
		this.setSize(700, 525);
		
		displayCentered(this); 
		
		this.setVisible(true);
	}
	
	public RStep getRStep() {
		return rStep;
	}

	public boolean startRStep(String portName) {
		getRStep().connect(portName);
		return getRStep().isConnected();
	}
	
	private void setNativeLookAndFeel() {
		// Get the native look and feel class name
		String nativeLF = UIManager.getSystemLookAndFeelClassName();
		// Install the look and feel
		try {
			UIManager.setLookAndFeel(nativeLF);
		} catch (ClassNotFoundException e) {
			// TODO: log this?
			//e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO: log this?
			//e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO: log this?
			//e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO: log this?
			//e.printStackTrace();
		}
	}
	
	public void errorDialog(String message) {
		JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
		optionPane.createDialog(this, null).setVisible(true); 
	}
	
	public void displayCentered(Component component) {
		// move component to the screen center
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		component.setLocation(
				(screenDimension.width - this.getSize().width) / 2,
				(screenDimension.height - this.getSize().height) / 2); 
	}
	
	private class ApplicationWindowEventHandler extends WindowAdapter {
		/**
		 * This method is called when the user clicks the close button
		 */
		@Override
		public void windowClosing(WindowEvent e) {
			// TODO: anything to do before closing when there is a file playing (e.g. stop spindle)?
			dispose();
			System.exit(0);
		}
	}

	/**
	 * This is the main event handler. It dispaches the events through the UI components.
	 */
	public void eventHandler(RStepEvent rStepEvent) {
		switch(rStepEvent.getReason()) {
		case MESSAGE:
			errorDialog(rStepEvent.toString());
			break;
		case CONNECTION_EXCEPTION:
			errorDialog("Cannot open rStep serial port");
			break;
		case COMMUNICATION_EXCEPTION:
			// this should occur only while connecting
			// in the other cases (sending or expecting data) it should be catched before 
			errorDialog("An error occured while receiving data from rStep");
			break;
		case PROTOCOL_EXCEPTION:
			errorDialog("rStep doesn't reply properly, try to reset it");
			break;
		case OTHER:
			if(rStepEvent instanceof RStepPlayerEvent) {
				RStepPlayerEvent rStepPlayerEvent = (RStepPlayerEvent)rStepEvent;
				switch(rStepPlayerEvent.getPlayerReason()) {
				case PLAYER_STATE:
					if(mainJPanel != null)
						switch(rStepPlayerEvent.getState()) {
						case READY:
							// update mainJPanel UI
							mainJPanel.setUIState(MainJPanel.UIStates.FILE_OPENED);
							break;
						case PAUSED:
							// update mainJPanel UI
							mainJPanel.setUIState(MainJPanel.UIStates.PAUSED);
							break;
						case PLAYING:
							mainJPanel.setUIState(MainJPanel.UIStates.PLAYING);
							break;
						case ABORTED:
						case FINISHED:
							mainJPanel.setUIState(MainJPanel.UIStates.MANUAL);
							break;
						}
					break;
				case IO_EXCEPTION:
					errorDialog("an error occured on the GCode file");
					break;
				}
			}
			break;
		}
	}
}
