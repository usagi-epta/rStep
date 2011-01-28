package com.studionex.rStep;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.PrintStream;
import java.util.TooManyListenersException;

import com.studionex.rStep.input.InputEvent;
import com.studionex.rStep.input.InputEventListener;
import com.studionex.rStep.input.InputParser;
import com.studionex.rStep.input.ReplyEvent;
import com.studionex.rStep.input.Serial;

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
public class RStep implements InputEventListener {
	private PrintStream monitorStream;
	private boolean timestamping = false;

	protected Serial serialPort;
	protected InputParser inputParser;
	
	private boolean lastCommandReplied;
	
	public RStep() {
		inputParser = new InputParser();
		inputParser.add(this);
	}

	public void connect(String serialPortName) throws ConnectionException {
		disconnect();
				
		try {
			serialPort = new Serial(serialPortName, inputParser);
			serialPort.setMonitor(monitorStream);
			serialPort.setTimestamping(timestamping);
		} catch (PortInUseException e) {
			throw new ConnectionException(e);
		} catch (IOException e) {
			throw new ConnectionException(e);
		} catch (TooManyListenersException e) {
			throw new ConnectionException(e);
		} catch (UnsupportedCommOperationException e) {
			throw new ConnectionException(e);
		}
	}

	protected Serial getSerialPort() {
		return serialPort;
	}

	public boolean isConnected() {
		return (getSerialPort() != null) && getSerialPort().isOpen();
	}
	
	public void disconnect() {
		if(isConnected())
    		getSerialPort().close();
	}

	public void resetConnection() throws ConnectionException {
		if(isConnected()) {
			String serialPortName = getSerialPort().getPortName();
			
			disconnect();
			
			try {
				Thread.sleep(100); // 100 msec
			} catch (InterruptedException e) {
				// TODO: log this?
				// e.printStackTrace();
			}
			
			connect(serialPortName);
		}
	}
	
	// TODO: add a reset() method that resets the Arduino without closing the connection
	
	public void setSerialMonitor(PrintStream monitorStream) {
		this.monitorStream = monitorStream;
		if(isConnected())
			getSerialPort().setMonitor(monitorStream);
	}
	
	public boolean isTimestamping() {
		if(isConnected())
			return getSerialPort().isTimestamping();
		else
			return timestamping;
	}

	public void setTimestamping(boolean timestamping) {
		this.timestamping = timestamping;
		if(isConnected())
			getSerialPort().setTimestamping(timestamping);
	}

	public void send(String command) throws CommunicationException {
		try {
			getSerialPort().send(command);
		} catch (IOException e) {
			throw new CommunicationException(e);
		}
	}
	
	public synchronized void sendAndWaitReply(String command) throws CommunicationException {
		lastCommandReplied = false;
		send(command);
		while(!lastCommandReplied)
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO: log this?
				e.printStackTrace();
			}
	}
	
	public synchronized void eventHandler(InputEvent inputEvent) {
		if(inputEvent instanceof ReplyEvent) {
			lastCommandReplied = true;
			this.notify();
		}		
	}

	public void add(InputEventListener eventListener) {
		inputParser.add(eventListener);
	}

	public void remove(InputEventListener eventListener) {
		inputParser.remove(eventListener);
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