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

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import com.studionex.misc.ui.MySwingUtilities;
import com.studionex.rStep.input.Serial;

@SuppressWarnings("serial")
public class SerialJDialog extends JDialog {
	private JList serialJList;
	private JButton connectJButton;

	private Application application;
	
	public SerialJDialog(Application application) {
		super(application, "Choose a serial port", true);
			// this is a modal JDialog
		
		this.application = application;
		
		buildUI();

		this.setSize(320, 240);
		
		// move this to the screen center
		MySwingUtilities.displayCentered(this); 
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	private void buildUI() {
		serialJList = new JList(Serial.getAvailablePorts());
		// add a listener for mouse clicks
		serialJList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					connectJButton.doClick();
				}
			}
		});
		getContentPane().add(new JScrollPane(serialJList), BorderLayout.CENTER);

		connectJButton = new JButton(new ConnectAction(this));
		getContentPane().add(connectJButton, BorderLayout.SOUTH);
	}
	
	private class ConnectAction extends AbstractAction {
		Window window;
		
		public ConnectAction(Window window) {
			super();
			
			this.window = window;
			
			putValue(NAME, "connect");
			putValue(SHORT_DESCRIPTION, "connect serial port to rStep");
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			if(!serialJList.isSelectionEmpty()) {
				String portName = (String)serialJList.getSelectedValue();
				if(application.connect(portName))
					window.dispose();
			}
		}
	}
}
