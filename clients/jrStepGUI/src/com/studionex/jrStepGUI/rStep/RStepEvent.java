package com.studionex.jrStepGUI.rStep;

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

import java.util.EventObject;

import com.studionex.rStep.CommunicationException;
import com.studionex.rStep.ConnectionException;
import com.studionex.rStep.ProtocolException;

@SuppressWarnings("serial")
public class RStepEvent extends EventObject {
	public static enum Reason {MESSAGE, CONNECTION_EXCEPTION, COMMUNICATION_EXCEPTION, PROTOCOL_EXCEPTION, OTHER};

	private final Reason reason; 

	private final String message;
	private final ConnectionException connectionException;
	private final CommunicationException communicationException;
	private final ProtocolException protocolException;
	
	public RStepEvent(Object source) {
		this(source, Reason.OTHER, null, null, null, null);
	}
	
	public RStepEvent(Object source, String message) {
		this(source, Reason.MESSAGE, message, null, null, null);
	}
	
	public RStepEvent(Object source, ConnectionException connectionException) {
		this(source, Reason.CONNECTION_EXCEPTION, null, connectionException, null, null);
	}

	public RStepEvent(Object source, CommunicationException communicationException) {
		this(source, Reason.COMMUNICATION_EXCEPTION, null, null, communicationException, null);
	}

	public RStepEvent(Object source, ProtocolException protocolException) {
		this(source, Reason.PROTOCOL_EXCEPTION, null, null, null, protocolException);
	}

	protected RStepEvent(Object source,
			Reason reason,
			String message,
			ConnectionException connectionException,
			CommunicationException communicationException,
			ProtocolException protocolException) {
		super(source);
		this.reason = reason;
		this.message = message;
		this.connectionException = connectionException;
		this.communicationException = communicationException;
		this.protocolException = protocolException;
	}

	public Reason getReason() { return reason; }

	public String getMessage() { return message; }
	public ConnectionException getConnectionException() { return connectionException; }
	public CommunicationException getCommunicationException() { return communicationException; }
	public ProtocolException getProtocolException() { return protocolException; }

	@Override
	public String toString() {
		switch(getReason()) {
		case MESSAGE:
			return super.toString() + " " + getMessage();
		case CONNECTION_EXCEPTION:
			return super.toString() + " " + getConnectionException();
		case COMMUNICATION_EXCEPTION:
			return super.toString() + " " + getCommunicationException();
		case PROTOCOL_EXCEPTION:
			return super.toString() + " " + getProtocolException();
		}
		return super.toString();
	}
}
