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

import java.io.File;
import java.util.Vector;

import com.studionex.event.EventManager;
import com.studionex.rStep.CommunicationException;
import com.studionex.rStep.ConnectionException;
import com.studionex.rStep.ProtocolException;

/**
 * This class is a wrapper around com.studionex.rStep.RStep. It catches the exceptions thrown and fires events instead.
 * This class also provides GCode file playing methods through delegation to a PlayThread instance.
 * 
 * @author Jean-Louis Paquelin
 * @see com.studionex.rStep.RStep
 * @see com.studionex.jrStepGUI.rStep.PlayThread
 */
public class RStep implements RStepEventListener {
	private EventManager<RStepEventListener, RStepEvent> eventManager = new EventManager<RStepEventListener, RStepEvent>();
	
	private com.studionex.rStep.RStep rStep;
	private PlayThread playThread;

	public RStep() {
		rStep = new com.studionex.rStep.RStep();
	}

	protected com.studionex.rStep.RStep getRStep() {
		return rStep;
	}

	public void connect(String serialPortName) {
		try {
			getRStep().connect(serialPortName);
		} catch (ConnectionException e) {
			fire(new RStepEvent(this, e));
		} catch (CommunicationException e) {
			fire(new RStepEvent(this, e));
		} catch (ProtocolException e) {
			fire(new RStepEvent(this, e));
		}
	}

	public boolean isConnected() {
		return getRStep().isConnected();
	}

	public void disconnect() {
		getRStep().disconnect();
	}

	public void reset() {
		try {
			getRStep().reset();
		} catch (ConnectionException e) {
			fire(new RStepEvent(this, e));
		} catch (CommunicationException e) {
			fire(new RStepEvent(this, e));
		} catch (ProtocolException e) {
			fire(new RStepEvent(this, e));
		}
	}

	/**
	 * This method sends command string to rStep.
	 * It splits the command on G and M codes and sends each one by one.
	 *  
	 * @param command
	 */
	public void sendExpectOk(String command) {
		// split command in single commands
		Vector<String> commands = new Vector<String>();
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
		
		if(commands.size() > 1)
			System.out.println("Splitting " + command);
		for(String c: commands) {
			sendExpectOkNoSplit(c);
		}
	}
	
	/**
	 * Sends the command to rStep providing some feedback on System.out and System.err.
	 * 
	 * @param command
	 */
	private void sendExpectOkNoSplit(String command) {
		System.out.print(command);
		System.out.flush();
		
		try {
			getRStep().sendExpectOk(command);
			System.out.println(" :: ok");
		} catch (CommunicationException e) {
			e.printStackTrace();
			System.out.println(" [an error occured while exchanging data with rStep]");
			// pause player
			if(playerIsPlaying())
				playerPause();
		} catch (ProtocolException e) {
			String reply = e.getProtocolError();
			System.err.println(e.getMessage());
			System.out.println(" :: " + (reply == null ? "no reply" : reply));
		}
	}

	public void openFile(File gcodeFile) {
		playThread = new PlayThread(gcodeFile, this, this);
	}
	
	public boolean hasPlayer() {
		return playThread != null;
	}
	
	public boolean playerIsPlaying() {
		return hasPlayer() && playThread.isPlaying();
	}
	
	public void playerPlay() {
		if(hasPlayer())
			playThread.play();
	}

	public void playerPause() {
		if(hasPlayer())
			playThread.pause();
	}
	
	public void playerAbort() {
		if(hasPlayer()) {
			playThread.abort();
			try {
				playThread.join();
			} catch (InterruptedException e) {
				// TODO: log this?
				//e.printStackTrace();
			}
			playThread = null;
		}
	}
	
	/**
	 * This method is called by the playThread.
	 * It is declared public to provide the RStepEventListener interface but it is used locally only.
	 * It forwards the playThreadEvents to the registered listeners.
	 * 
	 * @param event
	 */
	public void eventHandler(RStepEvent event) {
		if(event.getSource() == playThread)
			fire(event);
	}

	public void addEventListener(RStepEventListener eventListener) { eventManager.add(eventListener); }
	public void removeEventListener(RStepEventListener eventListener) { eventManager.remove(eventListener); }
	private void fire(RStepEvent event) { eventManager.fire(event); }

	@Override
	protected void finalize() throws Throwable {
	    try {
	    	if(playThread != null)
	    		playerAbort();
	    	if(isConnected())
	    		disconnect();
	    } finally {
	        super.finalize();
	    }
	}
}
