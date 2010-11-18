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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;


@SuppressWarnings("serial")
public class MainJPanel extends JPanel implements UIStatesHandler {
	private UIStates uiState = UIStates.MANUAL;
	 
	public static final int GAP = 5;
	
	/**
	 * Delay of appearance of the statusJPanel messages
	 */
	public static final int MESSAGE_DELAY = 10000; // 10 seconds

	private XYZJPanel xyzJPanel;
	private StepJPanel stepJPanel;
	private AuxJPanel auxJPanel;
	private FileJPanel fileJPanel;
	private StatusJPanel statusJPanel;
	private ConsoleJTabbedPane consoleJTabbedPane;

	private Application application;
	
	private double stepSize = 0.5;
	
	public MainJPanel(Application application) {
		super();
	
		this.application = application;
		
		buildUI();
		loadPrefs();
	}
	
	private void buildUI() {
 		this.setLayout(new GridBagLayout());

 		xyzJPanel = new XYZJPanel(application, this);
 		xyzJPanel.setBorder(MainJPanel.createBorder());
		this.add(xyzJPanel,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.5, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.BOTH,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));

		stepJPanel = new StepJPanel(this);
		stepJPanel.setBorder(MainJPanel.createBorder());
		this.add(stepJPanel,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 1,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.5, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		auxJPanel = new AuxJPanel(application);
		auxJPanel.setBorder(MainJPanel.createBorder());
		this.add(auxJPanel,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 2,
						/* weightx */ 0.3, /* weighty */ 1.0,
						/* anchor */ GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.BOTH,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));

		fileJPanel = new FileJPanel(application, this);
		fileJPanel.setBorder(MainJPanel.createBorder());
		this.add(fileJPanel,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 2,
						/* gridwidth */ 2, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */ GridBagConstraints.SOUTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));

		statusJPanel = new StatusJPanel();
		statusJPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		this.add(statusJPanel,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 3,
						/* gridwidth */ 2, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */ GridBagConstraints.CENTER,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));

		consoleJTabbedPane = new ConsoleJTabbedPane(application);
		this.add(consoleJTabbedPane,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 4,
						/* gridwidth */ 2, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1000.0,
						/* anchor */ GridBagConstraints.CENTER,
						/* fill */ GridBagConstraints.BOTH,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
	}
	
	public static Border createBorder() {
		return
			BorderFactory.createCompoundBorder(
					BorderFactory.createEtchedBorder(),
					BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
	}
	
	public double getStepSize() {
		return stepSize;
	}

	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
	}

	public String getStatusText() {
		return statusJPanel.getText();
	}

	public void setStatusText(String text) {
		statusJPanel.setText(text);
	}

	public void setStatusText(String text, int delay) {
		statusJPanel.setText(text, delay);
	}
	
	public UIStates getUIState() {
		return uiState;
	}

	public void setUIState(UIStates uiState) {
		this.uiState = uiState;
		
		xyzJPanel.setUIState(uiState);
		fileJPanel.setUIState(uiState);
		auxJPanel.setUIState(uiState);
		
		switch(uiState) {
		case MANUAL:
			setStatusText("Manual mode", MESSAGE_DELAY);

			break;
		case FILE_OPENED:
			setStatusText("GCode file opened", MESSAGE_DELAY);

			break;
		case PLAYING:
			setStatusText("Playing GCode file", MESSAGE_DELAY);

			break;
		case PAUSED:
			setStatusText("Player paused", MESSAGE_DELAY);

			break;
//		case WAITING:
//
//			break;
		}
		
	}
	
	private void loadPrefs() {
		// TODO: set the open JFileChooser on the last opened directory
		// TODO: set the JFrame on its last position and size
	}

}
