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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.bushe.swing.event.EventBus;


public class PlayThread extends Thread {
	public static enum PlayerStates {FILE_OPENED, PAUSED, PLAYING, ABORTED, FINISHED};
	public PlayerStates changeStateTo;

	private final File gcodeFile;
	private final RStep rStep;
	
	private GCodeFileHandler gcodeFileHandler = null;
	
	public PlayThread(File gcodeFile, RStep rStep) {
		super();
		this.gcodeFile = gcodeFile;
		this.rStep = rStep;
		
		try {
			gcodeFileHandler = new GCodeFileHandler(gcodeFile);
			changeStateTo = PlayerStates.FILE_OPENED;
			EventBus.publish("PlayThread " + changeStateTo, gcodeFile);
			start();
		} catch (IOException e) {
			// input file problem
			EventBus.publish("PlayThread IOException", e);
		}
	}

	public synchronized void play() {
		changeStateTo = PlayerStates.PLAYING;
		this.notify();
	}

	public synchronized boolean isPlaying() {
		return changeStateTo == PlayerStates.PLAYING;
	}

	public synchronized void pause() {
		changeStateTo = PlayerStates.PAUSED;
		this.notify();
		// notifying the thread isn't really necessary
		// but this avoids that a listener stays stucked waiting the PAUSE event
	}
	
	public synchronized void abort() {
		changeStateTo = PlayerStates.ABORTED;
		this.notify();
	}

	public void run() {
		String line;
		PlayerStates state = null;

		while(gcodeFileHandler != null) {
			line = null;

			synchronized(this) {
				switch(changeStateTo) {
				case PLAYING:
					try {
						line = gcodeFileHandler.getLine();
					} catch (IOException e) {
						EventBus.publish("PlayThread IOException", e);
					}
					if(line == null) {
						// no more line available
						System.out.println("end of " + gcodeFile + " reached");
						changeStateTo = PlayerStates.FINISHED;
					}
					if(changeStateTo != state) {
						EventBus.publish("PlayThread " + changeStateTo, null);
						state = changeStateTo;
					}
					break;
				case FILE_OPENED:
				case PAUSED:
					if(changeStateTo != state) {
						EventBus.publish("PlayThread " + changeStateTo, null);
						state = changeStateTo;
					}
					try { 
						this.wait();
					} catch(Exception e) {
						// TODO: log this?
						e.printStackTrace();
					}
					break;
				case ABORTED:
				case FINISHED:
					gcodeFileHandler.close();
					gcodeFileHandler = null;
					EventBus.publish("PlayThread " + changeStateTo, null);
					break;
				}
			}
			
			if(line != null) {
				// remove unnecessary spaces
				String cleanLine = line.trim();
				// remove comments (within parenthesis)
				cleanLine = cleanLine.replaceAll("\\(.*\\)", "");
				// remove comments {within curly brace}
				cleanLine = cleanLine.replaceAll("\\{.*\\}", "");
				
				if(cleanLine.length() > 0) {
					// send the available line to rStep
					rStep.send(cleanLine, true);
				} else {
					System.out.println("skip [" + line + "]");
				}
			}
		}
	}
	
	private static class GCodeFileHandler {
		private BufferedReader in;
		
		public GCodeFileHandler(File gcodeFile) throws FileNotFoundException {
			this.in = new BufferedReader(new FileReader(gcodeFile));
		}

		public String getLine() throws IOException {
			return in.readLine();
		}
		
		public void close() {
			if(in != null) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					// TODO: log this?
					e.printStackTrace();
				}
			}
		}
		
		@Override
		protected void finalize() throws Throwable {
		    try {
		    	close();
		    } finally {
		        super.finalize();
		    }
		}
	}

}
