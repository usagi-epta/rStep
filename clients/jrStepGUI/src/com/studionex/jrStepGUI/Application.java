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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintStream;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;

import com.studionex.jrStepGUI.rStep.RStep;
import com.studionex.jrStepGUI.rStep.RStepPlayer;
import com.studionex.jrStepGUI.rStep.RStepPlayerEvent;
import com.studionex.misc.ui.JPanelOutputStream;
import com.studionex.misc.ui.MySwingUtilities;
import com.studionex.rStep.input.DebugMessageEvent;
import com.studionex.rStep.input.InputEvent;
import com.studionex.rStep.input.ReplyEvent;
import com.studionex.rStep.input.StartEvent;
import com.studionex.rStep.input.SyntaxMessageEvent;

@SuppressWarnings("serial")
public class Application extends JFrame implements EventTopicSubscriber<Object>  {
	private RStep rStep;
	private boolean start = true;
	private RStepPlayer rStepPlayer;
	
	private MainJPanel mainJPanel;
	private JPanelOutputStream serialJPanelOutputStream;
	
	public Application() {
		rStep = new RStep();

		// connect a PrintStream to monitor rStep I/Os
		rStep.setSerialMonitor(new PrintStream(getSerialJPanelOutputStream(), true));
		rStep.setTimestamping(true);
		
		EventBus.subscribe(".*", this);

		// listen to some rStep outputs
		EventBus.subscribe("START", this);
		EventBus.subscribe(Pattern.compile("Reply:.*"), this);
		EventBus.subscribe(Pattern.compile("Debug:.*"), this);
		EventBus.subscribe(Pattern.compile("Syntax error:.*"), this);
		
		EventBus.subscribe(Pattern.compile("Sen[dt]"), this);
		EventBus.subscribe(Pattern.compile("CommunicationException"), this);

		rStepPlayer = new RStepPlayer();

		// listen to some RStepPlayer outputs
		EventBus.subscribe("Player state:.*", this);
		EventBus.subscribe("IOException:.*", this);

		mainJPanel = new MainJPanel(this);
		mainJPanel.setUIState(UIStatesHandler.UIStates.WAITING);
		this.getContentPane().add(mainJPanel);
		
		this.addWindowListener(new ApplicationWindowEventHandler());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		this.setTitle("jrStepGUI");
		this.setSize(700, 525);
		
		MySwingUtilities.displayCentered(this); 
		
		this.setVisible(true);

		// open a SerialJDialog that allows to choose and open
		// the serial port on which rStep communicates
		SerialJDialog serialJDialog = new SerialJDialog(this);
		serialJDialog.setVisible(true);
	}
	
	public void send(String command) {
		rStep.send(command, false);
	}
	
	public boolean connect(String serialPortName) {
		return rStep.connect(serialPortName);
	}
	
	public void openFile(File gcodeFile) {
		rStepPlayer.openFile(gcodeFile, rStep);
		mainJPanel.setUIState(UIStatesHandler.UIStates.FILE_OPENED);
	}

	public boolean hasPlayer() {
		return rStepPlayer.hasPlayer();
	}

	public void playerPlay() {
		rStepPlayer.playerPlay();
	}

	public void playerPause() {
		rStepPlayer.playerPause();
	}

	public void playerAbort() {
		rStepPlayer.playerAbort();
	}

	public void onEvent(String topic, Object eventObject) {
		if(topic.equals("Send")) {
			mainJPanel.setUIState(UIStatesHandler.UIStates.WAITING);
			
		} else if(topic.equals("Sent")) {
			mainJPanel.setUIState(UIStatesHandler.UIStates.READY);
			
		} else if(topic.equals("CommunicationException")) {
			errorDialog("A communication error occured while sending " + (String)eventObject);
			
		} else if(eventObject instanceof InputEvent) {
			if(eventObject instanceof ReplyEvent && start) {
				ReplyEvent replyEvent = (ReplyEvent)eventObject;
				
				if(!replyEvent.isOk()) {
					errorDialog("rStep returned some error at startup: " + replyEvent.getKind() + "\nIt may be caused by a badly initialized rStep EEPROM.\n");
				}
	
				mainJPanel.setUIState(UIStatesHandler.UIStates.READY);
				
			} else if(eventObject instanceof StartEvent) {
				start = false;
				mainJPanel.setUIState(UIStatesHandler.UIStates.READY);
				
			} else if(eventObject instanceof DebugMessageEvent) {
				// DebugMessageEvent messageEvent = (DebugMessageEvent)event;
				
			} else if(eventObject instanceof SyntaxMessageEvent) {
				SyntaxMessageEvent messageEvent = (SyntaxMessageEvent)eventObject;
				errorDialog("rStep returned a badly formatted message: " + messageEvent.getMessage() + "\nThis should never occur, please report it.");
	
			}
		} else if(eventObject instanceof RStepPlayerEvent) {
			RStepPlayerEvent rStepPlayerEvent = (RStepPlayerEvent)eventObject;
			switch(rStepPlayerEvent.getPlayerReason()) {
			case PLAYER_STATE:
				if(mainJPanel != null)
					switch(rStepPlayerEvent.getState()) {
					case READY:
						// update mainJPanel UI
						mainJPanel.setUIState(UIStatesHandler.UIStates.FILE_OPENED);
						break;
					case PAUSED:
						// update mainJPanel UI
						mainJPanel.setUIState(UIStatesHandler.UIStates.PAUSED);
						break;
					case PLAYING:
						mainJPanel.setUIState(UIStatesHandler.UIStates.PLAYING);
						break;
					case ABORTED:
					case FINISHED:
						mainJPanel.setUIState(UIStatesHandler.UIStates.READY);
						break;
					}
				break;
			case IO_EXCEPTION:
				errorDialog("An error occured while reading the GCode file.\n" + rStepPlayerEvent.getIOException());
				break;
			}
		}
	}

	public void errorDialog(String message) {
		JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
		optionPane.createDialog(this, null).setVisible(true); 
	}
	
	public JPanelOutputStream getSerialJPanelOutputStream() {
		if(serialJPanelOutputStream == null)
			serialJPanelOutputStream = new JPanelOutputStream();
		return serialJPanelOutputStream;
	}
	
	private class ApplicationWindowEventHandler extends WindowAdapter {
		/**
		 * This method is called when the user clicks the close button
		 */
		@Override
		public void windowClosing(WindowEvent e) {
			// TODO: anything to do before closing when there is a file playing (e.g. stop spindle)?
			rStep.disconnect();
			dispose();
			System.exit(0);
		}
	}
	
	/**
	 * Launches the application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MySwingUtilities.setNativeLookAndFeel();
				
				new Application();
			}
		});
	}
}
