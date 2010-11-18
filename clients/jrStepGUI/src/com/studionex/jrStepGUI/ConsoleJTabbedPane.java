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

import java.io.IOException;

import javax.swing.JTabbedPane;

import com.studionex.misc.ui.Console;

@SuppressWarnings("serial")
public class ConsoleJTabbedPane extends JTabbedPane {
	private Application application;

	public ConsoleJTabbedPane(Application application) {
		super();
		
		this.application = application;
	
		buildUI();
	}
	
	private void buildUI() {
		Console outConsole = null;
		try {
			outConsole = new Console(Console.SystemStreams.OUT);
		} catch (IOException e) {
			// TODO: log this?
			e.printStackTrace();
		}
		if(outConsole != null)
			this.addTab("Monitoring", outConsole);

		Console errConsole = null;
		try {
			errConsole = new Console(Console.SystemStreams.ERR);
		} catch (IOException e) {
			// TODO: log this?
			e.printStackTrace();
		}
		if(errConsole != null)
			this.addTab("Messages", errConsole);
		
		this.addTab("Serial I/O", application.getSerialJPanelOutputStream().getJPanel());
	}
}
