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

@SuppressWarnings("serial")
public class CoordinatesJPanel extends JPanel implements EventTopicSubscriber<CoordinatesMessageEvent> {
	private static final Locale LOCALE = null;
	private static final String FORMAT = "%.5f";
	
	private JTextField xJTextField;
	private JTextField yJTextField;
	private JTextField zJTextField;

	public CoordinatesJPanel() {
		super();
		
		buildUI();
		
		// listen to coordinates rStep outputs
		EventBus.subscribe(Pattern.compile("Coordinates:.*"), this);
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
		
		xJTextField = coordinateJTextFieldFactory();
		xJTextField.setToolTipText("position on X axis");
		this.add(xJTextField,
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
		
		yJTextField = coordinateJTextFieldFactory();
		yJTextField.setToolTipText("position on Y axis");
		this.add(yJTextField,
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
		
		zJTextField = coordinateJTextFieldFactory();
		zJTextField.setToolTipText("position on Z axis");
		this.add(zJTextField,
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
						/* gridx */ 0, /* gridy */ 3,
						/* gridwidth */ 3, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.NORTH,
						/* fill */ GridBagConstraints.BOTH,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
	}

	public void onEvent(String topic, CoordinatesMessageEvent data) {
		xJTextField.setText(String.format(LOCALE, FORMAT, data.getX()));
		yJTextField.setText(String.format(LOCALE, FORMAT, data.getY()));
		zJTextField.setText(String.format(LOCALE, FORMAT, data.getZ()));
		
	}
	
	private static JTextField coordinateJTextFieldFactory() {
		JTextField jTextField = new JTextField();
		jTextField.setEditable(false);
		jTextField.setHorizontalAlignment(JTextField.RIGHT);
		jTextField.setText(String.format(LOCALE, FORMAT, 0.0));
		
		return jTextField;
	}
}
