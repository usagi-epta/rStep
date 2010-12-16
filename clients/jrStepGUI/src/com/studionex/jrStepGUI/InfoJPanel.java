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
import java.util.Locale;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;

import com.studionex.misc.ui.VerticalLabelUI;
import com.studionex.rStep.input.CoordinatesMessageEvent;
import com.studionex.rStep.input.InputEvent;

@SuppressWarnings("serial")
public class InfoJPanel extends JPanel implements EventTopicSubscriber<InputEvent>, UIStatesHandler {
	private UIStatesHandler.UIStates uiState;
	private static final Locale LOCALE = null;
	private static final String DOUBLE_FORMAT = "%.5f";
	
	private JTextField xCoordinateJTextField;
	private JTextField yCoordinateJTextField;
	private JTextField zCoordinateJTextField;
	
	private JButton hardwareConfigureJButton;

	private Application application;

	public InfoJPanel(Application application) {
		super();
		
		this.application = application;

		buildUI();
		
		// listen to coordinates rStep outputs
		EventBus.subscribe(Pattern.compile("RStep Coordinates:.*"), this);
	}
	
	private void buildUI() {
		this.setLayout(new GridBagLayout());
		
		this.add(new JLabel("X: "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.NONE,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		xCoordinateJTextField = coordinateJTextFieldFactory("0.00000");
		xCoordinateJTextField.setToolTipText("position on X axis");
		this.add(xCoordinateJTextField,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHWEST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		this.add(new JLabel("Y: "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 1,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.NONE,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		yCoordinateJTextField = coordinateJTextFieldFactory("0.00000");
		yCoordinateJTextField.setToolTipText("position on Y axis");
		this.add(yCoordinateJTextField,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 1,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHWEST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		this.add(new JLabel("Z: "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 2,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.NONE,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		zCoordinateJTextField = coordinateJTextFieldFactory("0.00000");
		zCoordinateJTextField.setToolTipText("position on Z axis");
		this.add(zCoordinateJTextField,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 2,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHWEST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		JLabel coordinatesJLabel = new JLabel("Coordinates");
		coordinatesJLabel.setUI(new VerticalLabelUI(true));
		this.add(coordinatesJLabel,
				new GridBagConstraints(
						/* gridx */ 2, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 3,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.VERTICAL,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		this.add(new JLabel(" "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 4,
						/* gridwidth */ 3, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.BOTH,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		hardwareConfigureJButton = new JButton(new AbstractAction("Configure") {
			public void actionPerformed(ActionEvent event) {
				HardwareConfigJDialog hardwareConfigJDialog = new HardwareConfigJDialog(application);
				hardwareConfigJDialog.setVisible(true);
			}});
		this.add(hardwareConfigureJButton,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 5,
						/* gridwidth */ 3, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.SOUTH,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
	}

	public void onEvent(String topic, InputEvent inputEvent) {
		if(inputEvent instanceof CoordinatesMessageEvent) {
			CoordinatesMessageEvent coordinatesMessageEvent = (CoordinatesMessageEvent)inputEvent;
			xCoordinateJTextField.setText(String.format(LOCALE, DOUBLE_FORMAT, coordinatesMessageEvent.getX()));
			yCoordinateJTextField.setText(String.format(LOCALE, DOUBLE_FORMAT, coordinatesMessageEvent.getY()));
			zCoordinateJTextField.setText(String.format(LOCALE, DOUBLE_FORMAT, coordinatesMessageEvent.getZ()));
		}
		
	}
	
	private static JTextField coordinateJTextFieldFactory(String initialValue) {
		JTextField jTextField = new JTextField();
		jTextField.setEditable(false);
		jTextField.setHorizontalAlignment(JTextField.RIGHT);
		jTextField.setText(initialValue);
		
		return jTextField;
	}
	
	public UIStatesHandler.UIStates getUIState() {
		return uiState;
	}

	public void setUIState(UIStatesHandler.UIStates uiState) {
		this.uiState = uiState;
		switch(uiState) {
		case READY:
		case FILE_OPENED:
			hardwareConfigureJButton.setEnabled(true);
			break;

		case PLAYING:
		case PAUSED:
		case WAITING:
		case STARTUP:
			hardwareConfigureJButton.setEnabled(false);
			break;
		}
	}
}
