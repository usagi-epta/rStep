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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class XYZJPanel extends JPanel implements UIStatesHandler {
	private UIStatesHandler.UIStates uiState;

	public static enum XYZCmd {X_MINUS, X_PLUS, Y_MINUS, Y_PLUS, Z_MINUS, Z_PLUS};

	private JButton xPlusJButton;
	private JButton xMinusJButton;
	private JButton yPlusJButton;
	private JButton yMinusJButton;
	private JButton zPlusJButton;
	private JButton zMinusJButton;

	private Application application;
	private MainJPanel mainJPanel;

	public XYZJPanel(Application application, MainJPanel mainJPanel) {
		super();
	
		this.application = application;
		this.mainJPanel = mainJPanel;
		
		buildUI();
		setUIState(UIStatesHandler.UIStates.WAITING);
	}
	
	private void buildUI() {
		int rows = 3;
		int columns = 5;
		int buttonSize = 60;
		this.setMinimumSize(new Dimension(columns * buttonSize, rows * buttonSize));
		this.setPreferredSize(this.getMinimumSize());
		this.setMaximumSize(this.getMinimumSize());
		
		this.setLayout(new GridLayout(rows, columns, MainJPanel.GAP, MainJPanel.GAP));

		// 1st row
		this.add(new JLabel());
		yPlusJButton = new JButton(new Action(XYZCmd.Y_PLUS));
		this.add(yPlusJButton);
		this.add(new JLabel());
		this.add(new JLabel());
		zPlusJButton = new JButton(new Action(XYZCmd.Z_PLUS));
		this.add(zPlusJButton);

		// 2nd row
		xMinusJButton = new JButton(new Action(XYZCmd.X_MINUS));
		this.add(xMinusJButton);
		this.add(new JLabel());
		xPlusJButton = new JButton(new Action(XYZCmd.X_PLUS));
		this.add(xPlusJButton);
		this.add(new JLabel());
		this.add(new JLabel());
		
		// 3rd row
		this.add(new JLabel());
		yMinusJButton = new JButton(new Action(XYZCmd.Y_MINUS));
		this.add(yMinusJButton);
		this.add(new JLabel());
		this.add(new JLabel());
		zMinusJButton = new JButton(new Action(XYZCmd.Z_MINUS));
		this.add(zMinusJButton);

	}
	
	private class Action extends AbstractAction {
		public Action(XYZCmd xyz) {
			super();
			putValue(ACTION_COMMAND_KEY, xyz.name());
			switch(xyz) {
			case X_MINUS:
				putValue(NAME, "-X");
				putValue(SHORT_DESCRIPTION, "decrease X value");
				break;
			case X_PLUS:
				putValue(NAME, "+X");
				putValue(SHORT_DESCRIPTION, "increase X value");
				break;
			case Y_MINUS:
				putValue(NAME, "-Y");
				putValue(SHORT_DESCRIPTION, "decrease Y value");
				break;
			case Y_PLUS:
				putValue(NAME, "+Y");
				putValue(SHORT_DESCRIPTION, "increase Y value");
				break;
			case Z_MINUS:
				putValue(NAME, "-Z");
				//putValue(SMALL_ICON, new ImageIcon(getClass().getResource("icons/Z.png")));
				putValue(SHORT_DESCRIPTION, "decrease Z value");
				break;
			case Z_PLUS:
				putValue(NAME, "+Z");
				putValue(SHORT_DESCRIPTION, "increase Z value");
				break;
			}
		}

		public void actionPerformed(ActionEvent actionEvent) {
			if(mainJPanel.getStepSize() > 0) {
				// TODO: move GCode to RStep class
				if(actionEvent.getActionCommand() == XYZCmd.X_MINUS.name()) {
					application.send("G0 X-" + Double.toString(mainJPanel.getStepSize()));
				} else if(actionEvent.getActionCommand() == XYZCmd.X_PLUS.name()) {
					application.send("G0 X" + Double.toString(mainJPanel.getStepSize()));
				} else if(actionEvent.getActionCommand() == XYZCmd.Y_MINUS.name()) {
					application.send("G0 Y-" + Double.toString(mainJPanel.getStepSize()));
				} else if(actionEvent.getActionCommand() == XYZCmd.Y_PLUS.name()) {
					application.send("G0 Y" + Double.toString(mainJPanel.getStepSize()));
				} else if(actionEvent.getActionCommand() == XYZCmd.Z_MINUS.name()) {
					application.send("G0 Z-" + Double.toString(mainJPanel.getStepSize()));
				} else if(actionEvent.getActionCommand() == XYZCmd.Z_PLUS.name()) {
					application.send("G0 Z" + Double.toString(mainJPanel.getStepSize()));
				} else 
					System.out.println("Unknown button " + actionEvent.getActionCommand());
			}
		}
	}
	
	public UIStatesHandler.UIStates getUIState() {
		return uiState;
	}

	public void setUIState(UIStatesHandler.UIStates uiState) {
		this.uiState = uiState;
		switch(uiState) {
		case READY:
		case FILE_OPENED:
			xPlusJButton.setEnabled(true);
			xMinusJButton.setEnabled(true);
			yPlusJButton.setEnabled(true);
			yMinusJButton.setEnabled(true);
			zPlusJButton.setEnabled(true);
			zMinusJButton.setEnabled(true);

			break;
		case PLAYING:
		case PAUSED:
		case WAITING:
			xPlusJButton.setEnabled(false);
			xMinusJButton.setEnabled(false);
			yPlusJButton.setEnabled(false);
			yMinusJButton.setEnabled(false);
			zPlusJButton.setEnabled(false);
			zMinusJButton.setEnabled(false);

			break;
		}
	}
		
}
