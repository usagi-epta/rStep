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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.studionex.rStep.GCodes;

@SuppressWarnings("serial")
public class AuxJPanel extends JPanel implements UIStatesHandler {
	private UIStatesHandler.UIStates uiState;

	private JButton motorOnJButton;
	private JButton motorOffJButton;
	private JButton inchJButton;
	private JButton millimeterJButton;
	private JButton absoluteJButton;
	private JButton incrementalJButton;
	private JButton setHomeJButton;
	private JButton goHomeJButton;
	private CommandJPanel commandJPanel;
	
	private Application application;

	public AuxJPanel(Application application) {
		super();
	
		this.application = application;
		
		buildUI();
		setUIState(UIStatesHandler.UIStates.WAITING);
	}
	
	private void buildUI() {
		this.setLayout(new GridBagLayout());
		
		motorOnJButton = new JButton(new GCodeAction(GCodes.TURN_SPINDLE_CLOCKWISE, application));
		this.add(motorOnJButton,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		motorOffJButton = new JButton(new GCodeAction(GCodes.STOP_SPINDLE_TURNING, application));
		this.add(motorOffJButton,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 1,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, MainJPanel.GAP, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		inchJButton = new JButton(new GCodeAction(GCodes.INCH_SYSTEM_SELECTION, application));
		this.add(inchJButton,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 2,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		millimeterJButton = new JButton(new GCodeAction(GCodes.MILLIMETER_SYSTEM_SELECTION, application));
		this.add(millimeterJButton,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 3,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, MainJPanel.GAP, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		setHomeJButton = new JButton(new GCodeAction(GCodes.OFFSET_COORDINATE_SYSTEMS, application));
		this.add(setHomeJButton,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		goHomeJButton = new JButton(new GCodeAction(GCodes.RETURN_TO_HOME, application));
		this.add(goHomeJButton,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 1,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, MainJPanel.GAP, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		absoluteJButton = new JButton(new GCodeAction(GCodes.ABSOLUTE_DISTANCE_MODE, application));
		this.add(absoluteJButton,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 2,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		incrementalJButton = new JButton(new GCodeAction(GCodes.INCREMENTAL_DISTANCE_MODE, application));
		this.add(incrementalJButton,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 3,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, MainJPanel.GAP, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		this.add(new JLabel(" "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 4,
						/* gridwidth */ 2, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.BOTH,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		commandJPanel = new CommandJPanel(application);
		this.add(commandJPanel,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 5,
						/* gridwidth */ 2, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
	}
	
	public UIStatesHandler.UIStates getUIState() {
		return uiState;
	}

	public void setUIState(UIStatesHandler.UIStates uiState) {
		this.uiState = uiState;
		
		commandJPanel.setUIState(uiState);
		
		switch(uiState) {
		case READY:
		case FILE_OPENED:
			motorOnJButton.setEnabled(true);
			motorOffJButton.setEnabled(true);
			inchJButton.setEnabled(true);
			millimeterJButton.setEnabled(true);
			absoluteJButton.setEnabled(true);
			incrementalJButton.setEnabled(true);
			setHomeJButton.setEnabled(true);
			goHomeJButton.setEnabled(true);
			break;
		case PLAYING:
		case PAUSED:
		case WAITING:
			motorOnJButton.setEnabled(false);
			motorOffJButton.setEnabled(false);
			inchJButton.setEnabled(false);
			millimeterJButton.setEnabled(false);
			absoluteJButton.setEnabled(false);
			incrementalJButton.setEnabled(false);
			setHomeJButton.setEnabled(false);
			goHomeJButton.setEnabled(false);
			break;
		}
	}
		
}
