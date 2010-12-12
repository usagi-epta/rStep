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
import java.util.Locale;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;

import com.studionex.misc.ui.VerticalLabelUI;
import com.studionex.rStep.input.CoordinatesMessageEvent;
import com.studionex.rStep.input.InputEvent;
import com.studionex.rStep.input.StepByInchMessageEvent;

@SuppressWarnings("serial")
public class InfoJPanel extends JPanel implements EventTopicSubscriber<InputEvent> {
	private static final Locale LOCALE = null;
	private static final String DOUBLE_FORMAT = "%.5f";
	private static final String INT_FORMAT = "%d";
	
	private JTextField xCoordinateJTextField;
	private JTextField yCoordinateJTextField;
	private JTextField zCoordinateJTextField;

	private JTextField xSBIJTextField;
	private JTextField ySBIJTextField;
	private JTextField zSBIJTextField;

	public InfoJPanel() {
		super();
		
		buildUI();
		
		// listen to coordinates rStep outputs
		EventBus.subscribe(Pattern.compile("RStep Coordinates:.*"), this);
		EventBus.subscribe(Pattern.compile("RStep Step by inch:.*"), this);
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
		
		this.add(new JLabel("X: "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 3,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.NONE,
						/* insets */ new Insets(MainJPanel.GAP, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		xSBIJTextField = coordinateJTextFieldFactory("?");
		xSBIJTextField.setToolTipText("Step by inch on X axis");
		this.add(xSBIJTextField,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 3,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHWEST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(MainJPanel.GAP, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		this.add(new JLabel("Y: "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 4,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.NONE,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		ySBIJTextField = coordinateJTextFieldFactory("?");
		ySBIJTextField.setToolTipText("Step by inch on Y axis");
		this.add(ySBIJTextField,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 4,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHWEST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		this.add(new JLabel("Z: "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 5,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.NONE,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		zSBIJTextField = coordinateJTextFieldFactory("?");
		zSBIJTextField.setToolTipText("Step by inch on Z axis");
		this.add(zSBIJTextField,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 5,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHWEST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		JLabel SBIJLabel = new JLabel("Step by inch");
		SBIJLabel.setUI(new VerticalLabelUI(true));
		this.add(SBIJLabel,
				new GridBagConstraints(
						/* gridx */ 2, /* gridy */ 3,
						/* gridwidth */ 1, /* gridheight */ 3,
						/* weightx */ 0.0, /* weighty */ 0.0,
						/* anchor */GridBagConstraints.NORTHEAST,
						/* fill */ GridBagConstraints.VERTICAL,
						/* insets */ new Insets(0, 0, 0, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
		
		this.add(new JLabel(" "),
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 6,
						/* gridwidth */ 3, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.BOTH,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
	}

	public void onEvent(String topic, InputEvent inputEvent) {
		if(inputEvent instanceof CoordinatesMessageEvent) {
			CoordinatesMessageEvent coordinatesMessageEvent = (CoordinatesMessageEvent)inputEvent;
			xCoordinateJTextField.setText(String.format(LOCALE, DOUBLE_FORMAT, coordinatesMessageEvent.getX()));
			yCoordinateJTextField.setText(String.format(LOCALE, DOUBLE_FORMAT, coordinatesMessageEvent.getY()));
			zCoordinateJTextField.setText(String.format(LOCALE, DOUBLE_FORMAT, coordinatesMessageEvent.getZ()));
		} else if(inputEvent instanceof StepByInchMessageEvent) {
			StepByInchMessageEvent stepByInchMessageEvent = (StepByInchMessageEvent)inputEvent;
			xSBIJTextField.setText(String.format(LOCALE, INT_FORMAT, stepByInchMessageEvent.getX()));
			ySBIJTextField.setText(String.format(LOCALE, INT_FORMAT, stepByInchMessageEvent.getY()));
			zSBIJTextField.setText(String.format(LOCALE, INT_FORMAT, stepByInchMessageEvent.getZ()));
		}
		
	}
	
	private static JTextField coordinateJTextFieldFactory(String initialValue) {
		JTextField jTextField = new JTextField();
		jTextField.setEditable(false);
		jTextField.setHorizontalAlignment(JTextField.RIGHT);
		jTextField.setText(initialValue);
		
		return jTextField;
	}
}
