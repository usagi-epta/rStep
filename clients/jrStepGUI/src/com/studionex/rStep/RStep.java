package com.studionex.rStep;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.TooManyListenersException;


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

/**
 * This class is an abstraction of an Arduino running rStep.
 * It offers connection and data handling.
 *
 * @author  Jean-Louis Paquelin
 */
public class RStep {
	public static final long START_TIMEOUT = 5000; // 5 seconds

	protected Serial serialPort;
	
	private boolean hasStarted = false;
	
	public void connect(String serialPortName) throws ConnectionException, CommunicationException, ProtocolException {
		if(isConnected())
			disconnect();
		
		try {
			serialPort = new Serial(serialPortName);
		} catch (PortInUseException e) {
			throw new ConnectionException(e);
		} catch (IOException e) {
			throw new ConnectionException(e);
		} catch (TooManyListenersException e) {
			throw new ConnectionException(e);
		} catch (UnsupportedCommOperationException e) {
			throw new ConnectionException(e);
		}

		if(isSerialConnected()) {
			try {
				expect("start", START_TIMEOUT);
			} catch (CommunicationException e) {
				throw e;
			} catch (ProtocolException e) {
				throw e;
			}
			hasStarted = true;
		}
	}

	protected Serial getSerialPort() {
		return serialPort;
	}

	protected boolean isSerialConnected() {
		return (getSerialPort() != null) && getSerialPort().isOpen();
	}
	
	public boolean isConnected() {
		return isSerialConnected() && hasStarted;
	}
	
	public void disconnect() {
		if(isConnected())
    		getSerialPort().close();
		hasStarted = false;
	}

	public void reset() throws ConnectionException, CommunicationException, ProtocolException {
		if(isConnected()) {
			String serialPortName = getSerialPort().getPortName();
			
			disconnect();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO: log this?
				// e.printStackTrace();
			}
			
			connect(serialPortName);
		}
	}
	
	protected void expect(String expected, long timeout) throws CommunicationException, ProtocolException {
		try {
			String reply = getSerialPort().waitForInput(timeout);
			if((reply == null) || !reply.equals(expected))
				throw new ProtocolException(reply, "got [" + reply + "] instead of [" + expected + "]");
		} catch (IOException e) {
			throw new CommunicationException(e);
		}
	}

	public void sendExpectOk(String command) throws CommunicationException, ProtocolException {
		try {
			String expected = "ok";
			getSerialPort().send(command);
			try {
				expect(expected, 0); // no timeout, wait the reply for ever
			} catch (ProtocolException e) {
				throw new ProtocolException(e.getProtocolError(), 
						"while sending [" + command + "] got [" + e.getProtocolError() + "] instead of [" + expected + "]");
			}
		} catch (IOException e) {
			throw new CommunicationException(e);
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
	    try {
	    	disconnect();
	    } finally {
	        super.finalize();
	    }
	}
}
