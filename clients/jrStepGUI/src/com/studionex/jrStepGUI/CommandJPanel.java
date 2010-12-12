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

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class CommandJPanel extends JPanel implements UIStatesHandler {
	private UIStatesHandler.UIStates uiState;

	private JComboBox commandJComboBox;
	private JButton executeJButton;
	
	private Application application;

	public CommandJPanel(Application application) {
		super();
	
		this.application = application;
		
		buildUI();
		setUIState(UIStatesHandler.UIStates.STARTUP);
	}
	
	private void buildUI() {
		this.setLayout(new GridBagLayout());
		
		commandJComboBox = new JComboBox();
		commandJComboBox.setEditable(true);
		commandJComboBox.setToolTipText("enter a GCode command");
		this.add(commandJComboBox,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.BOTH,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		executeJButton = new JButton(new AbstractAction("Execute") {
			public void actionPerformed(ActionEvent evt) {
				String command = (String)commandJComboBox.getSelectedItem();
				if(command != null) {
					command = command.trim();
					// insert the command at the top of the list if it isn't already in
					if(commandJComboBox.getSelectedIndex() == -1)
						commandJComboBox.insertItemAt(command, 0);
					application.send((String)command);
				}
			}
		});
		executeJButton.setToolTipText("press to execute the command");
		this.add(executeJButton,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.0, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.VERTICAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
	}
	
	public UIStatesHandler.UIStates getUIState() {
		return uiState;
	}

	public void setUIState(UIStatesHandler.UIStates uiState) {
		this.uiState = uiState;
		switch(uiState) {
		case READY:
		case FILE_OPENED:
			commandJComboBox.setEnabled(true);
			executeJButton.setEnabled(true);
			break;

		case PLAYING:
		case PAUSED:
		case WAITING:
		case STARTUP:
			commandJComboBox.setEnabled(false);
			executeJButton.setEnabled(false);
			break;
		}
	}
		
}
