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
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;

import com.studionex.jrStepGUI.UIStatesHandler.UIStates;
import com.studionex.jrStepGUI.rStep.RStep;
import com.studionex.jrStepGUI.rStep.RStepPlayer;
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
//	private boolean startup = true;
	private RStepPlayer rStepPlayer;

	private UIStates uiState;

	private MainJPanel mainJPanel;
	private JPanelOutputStream serialJPanelOutputStream;
	
	public Application() {
		rStep = new RStep();

		// connect a PrintStream to monitor rStep I/Os
		rStep.setSerialMonitor(new PrintStream(getSerialJPanelOutputStream(), true));
		rStep.setTimestamping(true);
		
		// listen to some rStep outputs
		EventBus.subscribe("RStep START", this);
		EventBus.subscribe(Pattern.compile("RStep Reply:.*"), this);
		EventBus.subscribe(Pattern.compile("RStep Debug:.*"), this);
		EventBus.subscribe(Pattern.compile("RStep Syntax error:.*"), this);
		
		EventBus.subscribe(Pattern.compile("RStep Sen[dt]"), this);
		EventBus.subscribe(Pattern.compile("RStep CommunicationException"), this);

		rStepPlayer = new RStepPlayer();

		// listen to all RStepPlayer outputs
		EventBus.subscribe(Pattern.compile("PlayThread.*"), this);

		mainJPanel = new MainJPanel(this);
		this.getContentPane().add(mainJPanel);
		
		this.addWindowListener(new ApplicationWindowEventHandler());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		this.setTitle("jrStepGUI");
		this.setSize(700, 525);
		
		MySwingUtilities.displayCentered(this); 
		
		setUIState(UIStates.STARTUP);
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
		mainJPanel.setUIState(UIStates.FILE_OPENED);
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

	public void onEvent(String topic, Object data) {
		if(topic.startsWith("RStep")) {
			if(topic.equals("RStep Send") && !isUIStatePlayingOrPaused()) {
				setUIState(UIStates.WAITING);
				
			} else if(topic.equals("RStep Sent") && !isUIStatePlayingOrPaused()) {
				setUIState(UIStates.READY);
				
			} else if(topic.equals("RStep CommunicationException")) {
				errorDialog("A communication error occured while sending " + (String)data, JOptionPane.ERROR_MESSAGE);
				
			} else if(data instanceof InputEvent) {
				if(data instanceof ReplyEvent && isUIStateStartup()) {
					ReplyEvent replyEvent = (ReplyEvent)data;
					
					if(!replyEvent.isOk()) {
						errorDialog(
							"rStep returned some error at startup: " + replyEvent.getKind() +
								"\nIt may be caused by a badly initialized rStep EEPROM.\n",
							JOptionPane.ERROR_MESSAGE);
					}
		
					setUIState(UIStates.READY);
					
				} else if(data instanceof StartEvent) {
					if(!isUIStateStartup()) {
						if(isUIStatePlayingOrPaused())
							playerAbort();
						errorDialog("rStep restarted.", JOptionPane.WARNING_MESSAGE);

						setUIState(UIStates.READY);

					} else {
						System.out.println();
						setUIState(UIStates.READY);
					}
					
				} else if(data instanceof DebugMessageEvent) {
					System.out.println((DebugMessageEvent)data);
					
				} else if(data instanceof SyntaxMessageEvent) {
					SyntaxMessageEvent messageEvent = (SyntaxMessageEvent)data;
					System.out.println((SyntaxMessageEvent)data);
					errorDialog("rStep returned a badly formatted message: " + messageEvent.getMessage() + "\nThis should never occur, please report it.", JOptionPane.ERROR_MESSAGE);
		
				}
			}

		} else if(topic.startsWith("PlayThread")) {
			if(topic.equals("PlayThread FILE_OPENED")) {
				setUIState(UIStates.FILE_OPENED);
				
			} else if(topic.equals("PlayThread PAUSED")) {
				setUIState(UIStates.PAUSED);
				
			} else if(topic.equals("PlayThread PLAYING")) {
				setUIState(UIStates.PLAYING);
				
			} else if(topic.equals("PlayThread ABORTED") || topic.equals("PlayThread FINISHED")) {
				setUIState(UIStates.READY);
				
			} else if(topic.equals("PlayThread IOException")) {
				errorDialog("An error occured while reading the GCode file.\n" + (IOException)data, JOptionPane.ERROR_MESSAGE);
				
			}
		}
	}
	
	private void setUIState(UIStates uiState) {
		this.uiState = uiState;
		mainJPanel.setUIState(uiState);
	}
	
	private boolean isUIStateStartup() {
		return uiState == UIStates.STARTUP;
	}
	
	private boolean isUIStatePlayingOrPaused() {
		return uiState == UIStates.PAUSED || uiState == UIStates.PLAYING;
	}
	
	public void errorDialog(String message, int messageType) {
		JOptionPane optionPane = new JOptionPane(message, messageType, JOptionPane.DEFAULT_OPTION);
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
