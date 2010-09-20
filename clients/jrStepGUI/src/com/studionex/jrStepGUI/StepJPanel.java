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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class StepJPanel extends JPanel {
	private JTextField stepJTextField;
	private JSlider stepJSlider;

	private MainJPanel mainJPanel;

	public StepJPanel(MainJPanel mainJPanel) {
		super();
	
		this.mainJPanel = mainJPanel;
		
		buildUI();
	}
	
	private void buildUI() {
		this.setLayout(new GridBagLayout());

		stepJSlider = new JSlider(JSlider.HORIZONTAL);
		stepJSlider.addChangeListener(new ChangeListener() {
			// This method is called whenever the slider's value is changed
			public void stateChanged(ChangeEvent changeEvent) {
				JSlider slider = (JSlider)changeEvent.getSource();
				if (!slider.getValueIsAdjusting()) {
					double min = slider.getMinimum();
					mainJPanel.setStepSize((slider.getValue() - min) / (slider.getMaximum() - min));
					stepJTextField.setText(Double.toString(mainJPanel.getStepSize()));
				}
			}
		});
		this.add(stepJSlider,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.8, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));

		stepJTextField = new JTextField("0.0");
		stepJTextField.setHorizontalAlignment(JTextField.RIGHT);
		stepJTextField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent focusEvent) {
				mainJPanel.setStepSize(Double.parseDouble(stepJTextField.getText()));
				stepJTextField.setText(Double.toString(mainJPanel.getStepSize()));
				int min = stepJSlider.getMinimum();
				stepJSlider.setValue((int)(mainJPanel.getStepSize() * (stepJSlider.getMaximum() - min) - min)); 
			}
		});
		this.add(stepJTextField,
				new GridBagConstraints(
						/* gridx */ 1, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 0.2, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(0, 0, 0, 0),
						/* ipadx */ 0, /* ipady */ 0));
		
		// set JTextField and JSlider values from stepSize
		stepJTextField.setText(Double.toString(mainJPanel.getStepSize()));
		int min = stepJSlider.getMinimum();
		stepJSlider.setValue((int)(mainJPanel.getStepSize() * (stepJSlider.getMaximum() - min) - min)); 
	}
}
