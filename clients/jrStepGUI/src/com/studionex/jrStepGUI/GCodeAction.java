package com.studionex.jrStepGUI;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.studionex.rStep.GCodes;

@SuppressWarnings("serial")
public class GCodeAction extends AbstractAction {
	private GCodes gCode;
	private Application application;
	
	public GCodeAction(GCodes gCode, Application application) {
		super();
		
		this.gCode = gCode;
		this.application = application;
		
		//putValue(ACTION_COMMAND_KEY, gCode.name());
		putValue(NAME, gCode.title());
		putValue(SHORT_DESCRIPTION, gCode.comment() + " [" + gCode.code() + "]");
	}

	public void actionPerformed(ActionEvent actionEvent) {
		application.getRStep().sendExpectOk(gCode.code());
	}
}
