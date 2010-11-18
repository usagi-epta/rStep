package com.studionex.rStep;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.PrintStream;
import java.util.TooManyListenersException;

import com.studionex.event.EventManager;
import com.studionex.jrStepGUI.rStep.RStepEvent;
import com.studionex.jrStepGUI.rStep.RStepEventListener;
import com.studionex.rStep.input.AbsoluteModeMessageEvent;
import com.studionex.rStep.input.CoordinatesMessageEvent;
import com.studionex.rStep.input.CurrentMessageEvent;
import com.studionex.rStep.input.DebugMessageEvent;
import com.studionex.rStep.input.FeedRateMessageEvent;
import com.studionex.rStep.input.InputEvent;
import com.studionex.rStep.input.InputEventListener;
import com.studionex.rStep.input.InputParser;
import com.studionex.rStep.input.StepByInchMessageEvent;
import com.studionex.rStep.input.SteppingMessageEvent;
import com.studionex.rStep.input.ReplyEvent;
import com.studionex.rStep.input.Serial;
import com.studionex.rStep.input.StartEvent;
import com.studionex.rStep.input.SyntaxMessageEvent;


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
	public static final long START_TIMEOUT = 10000; // 5 seconds

	protected Serial serialPort;
	protected InputParser inputParser;
	protected PrintStream monitorStream;
	
	private boolean hasStarted = false;
	
	private Boolean gotStart = false;
	private Boolean gotOk = false;
	
	public void connect(String serialPortName) throws ConnectionException, CommunicationException, ProtocolException {
		if(isConnected())
			disconnect();
		
		inputParser = new InputParser();
		inputParser.add(this);
		
		try {
			serialPort = new Serial(serialPortName, inputParser);
			serialPort.setMonitor(monitorStream);
		} catch (PortInUseException e) {
			throw new ConnectionException(e);
		} catch (IOException e) {
			throw new ConnectionException(e);
		} catch (TooManyListenersException e) {
			throw new ConnectionException(e);
		} catch (UnsupportedCommOperationException e) {
			throw new ConnectionException(e);
		}

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(isSerialConnected()) {
			synchronized(this) {
				try {
					while(!gotStart) {
					this.wait(START_TIMEOUT);
					}
				} catch (InterruptedException e) {
					// TODO: log this?
					//e.printStackTrace();
				}
				if(!gotStart)
					throw new ProtocolException("no START", "ain't got START");
				else
					hasStarted = true;
			}
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
	
	public void setSerialMonitor(PrintStream monitorStream) {
		this.monitorStream = monitorStream;
		if(getSerialPort() != null)
			getSerialPort().setMonitor(monitorStream);
	}
	
	public void sendExpectOk(String command) throws CommunicationException, ProtocolException {
		try {
			getSerialPort().send(command);
			synchronized(this) {
				try {
					this.wait(START_TIMEOUT);
				} catch (InterruptedException e) {
					// TODO: log this?
					//e.printStackTrace();
				}
				if(!gotOk)
					throw new ProtocolException("no Ok", "while sending [" + command + "] ain't got [OK]");
				else
					gotOk = false;
			}
		} catch (IOException e) {
			throw new CommunicationException(e);
		}
	}
	
	public void add(InputEventListener eventListener) {
		inputParser.add(eventListener);
	}

	public void remove(InputEventListener eventListener) {
		inputParser.remove(eventListener);
	}

	public void eventHandler(InputEvent inputEvent) {
		if(inputEvent instanceof ReplyEvent) {
			ReplyEvent replyEvent = (ReplyEvent)inputEvent;
			
			if(replyEvent.isOk())
				synchronized(this) {
					gotOk = true;
					this.notify();
				}
		} else if(inputEvent instanceof CoordinatesMessageEvent) {
			CoordinatesMessageEvent messageEvent = (CoordinatesMessageEvent)inputEvent;
			System.out.println(messageEvent);
			
		} else if(inputEvent instanceof DebugMessageEvent) {
			DebugMessageEvent messageEvent = (DebugMessageEvent)inputEvent;
			System.err.println(messageEvent);
			
		} else if(inputEvent instanceof AbsoluteModeMessageEvent) {
			AbsoluteModeMessageEvent messageEvent = (AbsoluteModeMessageEvent)inputEvent;
			
		} else if(inputEvent instanceof CurrentMessageEvent) {
			CurrentMessageEvent messageEvent = (CurrentMessageEvent)inputEvent;
			
		} else if(inputEvent instanceof FeedRateMessageEvent) {
			FeedRateMessageEvent messageEvent = (FeedRateMessageEvent)inputEvent;
			
		} else if(inputEvent instanceof StepByInchMessageEvent) {
			StepByInchMessageEvent messageEvent = (StepByInchMessageEvent)inputEvent;
			
		} else if(inputEvent instanceof SteppingMessageEvent) {
			SteppingMessageEvent messageEvent = (SteppingMessageEvent)inputEvent;
			
		} else if(inputEvent instanceof StartEvent) {
			synchronized(this) {
				gotOk = false;
				gotStart = true;
				this.notify();
			}
		} else if(inputEvent instanceof SyntaxMessageEvent) {
			SyntaxMessageEvent messageEvent = (SyntaxMessageEvent)inputEvent;
			System.err.println(messageEvent);
			
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
