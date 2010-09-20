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

import javax.swing.JPanel;

import com.studionex.misc.ui.TimedJLabel;

@SuppressWarnings("serial")
public class StatusJPanel extends JPanel {
	private TimedJLabel messageTimedJLabel;

	public StatusJPanel() {
		super();
	
		buildUI();
	}
	
	private void buildUI() {
		this.setLayout(new GridBagLayout());

		messageTimedJLabel = new TimedJLabel(" ");
		this.add(messageTimedJLabel,
				new GridBagConstraints(
						/* gridx */ 0, /* gridy */ 0,
						/* gridwidth */ 1, /* gridheight */ 1,
						/* weightx */ 1.0, /* weighty */ 1.0,
						/* anchor */GridBagConstraints.EAST,
						/* fill */ GridBagConstraints.HORIZONTAL,
						/* insets */ new Insets(MainJPanel.GAP, MainJPanel.GAP, MainJPanel.GAP, MainJPanel.GAP),
						/* ipadx */ 0, /* ipady */ 0));
	}

	public String getText() {
		return messageTimedJLabel.getText();
	}

	public void setText(String text) {
		messageTimedJLabel.setText(text);
	}

	public void setText(String text, int delay) {
		messageTimedJLabel.setText(text, delay);
	}
}
