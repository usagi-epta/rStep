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
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class HomeJPanel extends JPanel implements UIStatesHandler {
	private UIStatesHandler.UIStates uiState;

	public static enum HomeCmd {GO_HOME, SET_HOME};
	
	private JButton goHomeJButton;
	private JButton setHomeJButton;

	private Application application;

	public HomeJPanel(Application application) {
		super();
	
		this.application = application;
		
		buildUI();
		setUIState(UIStates.MANUAL);
	}
	
	private void buildUI() {
		this.setBorder(MainJPanel.createBorder());
		this.setLayout(new GridBagLayout());
		
		goHomeJButton = new JButton(new Action(HomeCmd.GO_HOME));
		this.add(goHomeJButton,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		setHomeJButton = new JButton(new Action(HomeCmd.SET_HOME));
		this.add(setHomeJButton,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
	}
	
	private class Action extends AbstractAction {
		public Action(HomeCmd home) {
			super();
			putValue(ACTION_COMMAND_KEY, home.name());
			switch(home) {
			case GO_HOME:
				putValue(NAME, "go home");
				putValue(SHORT_DESCRIPTION, "return to home");
				break;
			case SET_HOME:
				putValue(NAME, "set home");
				putValue(SHORT_DESCRIPTION, "set home position");
				break;
			}
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			if(actionEvent.getActionCommand() == HomeCmd.GO_HOME.name()) {
				application.getRStep().sendExpectOk("G28");
			} else if(actionEvent.getActionCommand() == HomeCmd.SET_HOME.name()) {
				application.getRStep().sendExpectOk("G92");
			} else 
				System.err.println("Unknown button " + actionEvent.getActionCommand());
		}
	}
	
	public UIStates getUIState() {
		return uiState;
	}

	public void setUIState(UIStates uiState) {
		this.uiState = uiState;
		switch(uiState) {
		case MANUAL:
		case FILE_OPENED:
			goHomeJButton.setEnabled(true);
			setHomeJButton.setEnabled(true);

			break;
		case PLAYING:
		case PAUSED:
		case WAITING:
			goHomeJButton.setEnabled(false);
			setHomeJButton.setEnabled(false);

			break;
		}
	}
		
}
