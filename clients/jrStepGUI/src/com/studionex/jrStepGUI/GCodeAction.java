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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.studionex.rStep.GCodes;

@SuppressWarnings("serial")
public class GCodeAction extends AbstractAction {
	private GCodes gCode;
	private Application application;
	
	// TODO: handle GCodes with parameters
	
	public GCodeAction(GCodes gCode, Application application) {
		super();
		
		this.gCode = gCode;
		this.application = application;
		
		//putValue(ACTION_COMMAND_KEY, gCode.name());
		putValue(NAME, gCode.title());
		putValue(SHORT_DESCRIPTION, gCode.comment() + " [" + gCode.code() + "]");
	}

	public void actionPerformed(ActionEvent actionEvent) {
		application.send(gCode.code());
	}
}
