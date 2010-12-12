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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;


@SuppressWarnings("serial")
public class FileJPanel extends JPanel implements UIStatesHandler {
	public static String NO_FILE_MESSAGE = "no file selected";
	
	private UIStatesHandler.UIStates uiState;
	
	public static enum PBCCmd {PAUSE, ABORT, PLAY, OPEN};
	
	private JTextField pathJTextField;
	private JButton openJButton;

	private JButton pauseJButton;
	private JButton abortJButton;
	private JButton playJButton;

	private Application application;
	private MainJPanel mainJPanel;

	public FileJPanel(Application application, MainJPanel mainJPanel) {
		super();
	
		this.application = application;
		this.mainJPanel = mainJPanel;
		
		buildUI();
		setUIState(UIStatesHandler.UIStates.STARTUP);
		
	}
	
	private void buildUI() {
		this.setLayout(new GridBagLayout());

		JPanel pathJPanel = new JPanel();
		pathJPanel.setLayout(new GridBagLayout());
		{
			pathJTextField = new JTextField(NO_FILE_MESSAGE);
			pathJTextField.setEditable(false);
			pathJTextField.setMargin(new Insets(0, MainJPanel.GAP, 0, 0));
			pathJPanel.add(pathJTextField,
					new GridBagConstraints(
							/* gridx */ 0, /* gridy */ 0,
							/* gridwidth */ 1, /* gridheight */ 1,
							/* weightx */ 1.0, /* weighty */ 1.0,
							/* anchor */GridBagConstraints.EAST,
							/* fill */ GridBagConstraints.BOTH,
							/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
							/* ipadx */ 0, /* ipady */ 0));
	
			openJButton = new JButton(new Action(PBCCmd.OPEN));
			pathJPanel.add(openJButton,
					new GridBagConstraints(
							/* gridx */ 1, /* gridy */ 0,
							/* gridwidth */ 1, /* gridheight */ 1,
							/* weightx */ 0.0, /* weighty */ 1.0,
							/* anchor */GridBagConstraints.EAST,
							/* fill */ GridBagConstraints.VERTICAL,
							/* insets */ new Insets(0, 0, 0, 0),
							/* ipadx */ 0, /* ipady */ 0));
		}
		this.add(pathJPanel,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 0,
						/* gridwidth */ 3, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, MainJPanel.GAP, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		playJButton = new JButton(new Action(PBCCmd.PLAY));
		this.add(playJButton,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 1,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.25, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));

		pauseJButton = new JButton(new Action(PBCCmd.PAUSE));
		this.add(pauseJButton,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 1,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.5, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		abortJButton = new JButton(new Action(PBCCmd.ABORT));
		this.add(abortJButton,
				new GridBagConstraints(
						/* gridx */ 2, /* gridy */ 1,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.25, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
	}

	private class Action extends AbstractAction {
		public Action(PBCCmd pbc) {
			super();
			putValue(ACTION_COMMAND_KEY, pbc.name());
			putValue(NAME, pbc.toString());
			switch(pbc) {
			case PAUSE:
				putValue(SHORT_DESCRIPTION, "pause");
				break;
			case ABORT:
				putValue(SHORT_DESCRIPTION, "abort playback");
				break;
			case PLAY:
				putValue(SHORT_DESCRIPTION, "play G-Code file");
				break;
			case OPEN:
				putValue(NAME, "...");
				putValue(SHORT_DESCRIPTION, "open a G-Code file");
				break;
			}
		}

		public void actionPerformed(ActionEvent actionEvent) {
			if(actionEvent.getActionCommand() == PBCCmd.PAUSE.name()) {
				if(application.hasPlayer() && getUIState() == UIStatesHandler.UIStates.PLAYING) {
					// disabling the button to show the click
					mainJPanel.setUIState(UIStatesHandler.UIStates.WAITING);
					mainJPanel.setStatusText("Waiting for the player...");
					application.playerPause();
				}
			} else if(actionEvent.getActionCommand() == PBCCmd.ABORT.name()) {
				if(application.hasPlayer()) {
					// disabling the button to show the click
					mainJPanel.setUIState(UIStatesHandler.UIStates.WAITING);
					mainJPanel.setStatusText("Aborting the playback...", MainJPanel.MESSAGE_DELAY);
					application.playerAbort();
				}
			} else if(actionEvent.getActionCommand() == PBCCmd.PLAY.name()) {
				if(application.hasPlayer()) {
					// disabling the button to show the click
					mainJPanel.setUIState(UIStatesHandler.UIStates.WAITING);
					mainJPanel.setStatusText("Waiting for the player...");
					application.playerPlay();
				}
			} else if(actionEvent.getActionCommand() == PBCCmd.OPEN.name()) {
				open();
			} else 
				System.out.println("Unknown button " + actionEvent.getActionCommand());
		}
	}
	
	private void open() {
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileFilter() {
	        public boolean accept(File file) {
	            String filename = file.getName();
	            return filename.endsWith(".gcode") || filename.endsWith(".nc") || file.isDirectory();
	        }
	        public String getDescription() {
	            return "G-Code (*.nc)";
	        }
	    });

		int result = chooser.showOpenDialog(this);

		switch(result) {
		case JFileChooser.APPROVE_OPTION:
			// Approve (Open or Save) was clicked
			pathJTextField.setText(chooser.getSelectedFile().toString());
			application.openFile(chooser.getSelectedFile());
			break;
		case JFileChooser.CANCEL_OPTION:
			// Opening canceled
			break;
		case JFileChooser.ERROR_OPTION:
			// Error during selection
			break;
		}
	}
	
	public UIStatesHandler.UIStates getUIState() {
		return uiState;
	}

	public void setUIState(UIStatesHandler.UIStates uiState) {
		this.uiState = uiState;

		switch(uiState) {
		case READY:
			pathJTextField.setText(NO_FILE_MESSAGE);
			openJButton.setEnabled(true);
			
			pauseJButton.setEnabled(false);
			abortJButton.setEnabled(false);
			playJButton.setEnabled(false);

			break;
		case FILE_OPENED:
			openJButton.setEnabled(true);
			
			pauseJButton.setEnabled(false);
			abortJButton.setEnabled(true);
			playJButton.setEnabled(true);

			break;
		case PLAYING:
			openJButton.setEnabled(false);
			
			pauseJButton.setEnabled(true);
			abortJButton.setEnabled(true);
			playJButton.setEnabled(false);

			break;
		case PAUSED:
			openJButton.setEnabled(false);
	
			pauseJButton.setEnabled(false);
			abortJButton.setEnabled(true);
			playJButton.setEnabled(true);

			break;
		case WAITING:
		case STARTUP:
			openJButton.setEnabled(false);

			pauseJButton.setEnabled(false);
			abortJButton.setEnabled(false);
			playJButton.setEnabled(false);

			break;
		}
	}

}
