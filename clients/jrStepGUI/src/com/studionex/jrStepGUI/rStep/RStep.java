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

import java.io.PrintStream;
import java.util.Vector;

import org.bushe.swing.event.EventBus;

import com.studionex.rStep.CommunicationException;
import com.studionex.rStep.ConnectionException;
import com.studionex.rStep.input.InputEvent;
import com.studionex.rStep.input.InputEventListener;

/**
 * This class is a wrapper around com.studionex.rStep.RStep.
 * This class also provides GCode file playing methods through delegation to a PlayThread instance.
 * 
 * @author Jean-Louis Paquelin
 * @see com.studionex.rStep.RStep
 * @see com.studionex.jrStepGUI.rStep.PlayThread
 */
public class RStep implements InputEventListener {
	private com.studionex.rStep.RStep rStep;
	
	public RStep() {
		rStep = new com.studionex.rStep.RStep();

		// listen to the rStep outputs
		rStep.add(this);
	}

	private com.studionex.rStep.RStep getRStep() {
		return rStep;
	}
	
	public void setSerialMonitor(PrintStream monitorStream) {
		getRStep().setSerialMonitor(monitorStream);
	}

	public boolean isTimestamping() {
		return getRStep().isTimestamping();
	}

	public void setTimestamping(boolean timestamping) {
		getRStep().setTimestamping(timestamping);
	}

	public boolean connect(String serialPortName) {
		try {
			getRStep().connect(serialPortName);
		} catch (ConnectionException e) {
			// TODO: log this?
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean isConnected() {
		return getRStep().isConnected();
	}

	public void disconnect() {
		getRStep().disconnect();
	}

	public boolean reset() {
		try {
			getRStep().reset();
		} catch (ConnectionException e) {
			// TODO: log this?
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * This method sends command string to rStep.
	 * It splits the command on G and M codes and sends each one by one.
	 *  
	 * @param command
	 * @param waitCompletion if true the caller will wait for the rStep reply
	 */
	public void send(final String command, final boolean waitCompletion) {
		// split command in single commands
		final Vector<String> commands = new Vector<String>();
		String[] splittedCommand = command.split(" |\t"); 
		
		String singleCommand = "";
		for(String partialCommand: splittedCommand) {
			if(singleCommand.isEmpty())
				singleCommand = partialCommand;
			else if(partialCommand.startsWith("G") || partialCommand.startsWith("M")) {
				// it's the beginning of a new single command
				commands.add(singleCommand);
				singleCommand = partialCommand;
			} else
				singleCommand += " " + partialCommand;
		}

		// send the rest of the command if it exists
		if(!singleCommand.isEmpty())
			commands.add(singleCommand);
		
		if(!commands.isEmpty()) {
			EventBus.publish("RStep Send", command);

			if(commands.size() > 1)
				System.out.println("Splitting " + command);
			
			Thread sendThread = new Thread() {
				public void run() {
					for(String c: commands) {
						try {
							getRStep().sendAndWaitReply(c);
						} catch (CommunicationException e) {
							// TODO: log this?
							e.printStackTrace();
							EventBus.publish("RStep CommunicationException", command);
						}
					}
					if(!waitCompletion)
						// if waitCompletion the event will be published after the thread finishes
						// see the join() call below
						EventBus.publish("RStep Sent", command);
				}
			};
			
			sendThread.start();
			
			if(waitCompletion)
				try {
					sendThread.join();
					EventBus.publish("RStep Sent", command);
				} catch (InterruptedException e) {
					// TODO: log this?
					e.printStackTrace();
				}
		}
	}

	/**
	 * Low level events aren't suited to be used there as it isn't allowed to invoke any Swing method
	 * outside of the Swing thread. So, This method receives the events from the low level RStep instance
	 * and adapt them to the Swing framework by using an eventbus.
	 * @see http://www.eventbus.org/
	 */
	public void eventHandler(InputEvent inputEvent) {
		EventBus.publish("RStep " + inputEvent.toString(), inputEvent);
	}
}
