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


public class PlayThread extends Thread {
	public static enum PlayerStates {READY, PAUSED, PLAYING, ABORTED, FINISHED};
	public PlayerStates changeStateTo;

	private final File gcodeFile;
	private final RStep rStep;
	private final RStepEventListener rStepEventListener;
	
	private GCodeFileHandler gcodeFileHandler = null;
	
	public PlayThread(File gcodeFile, RStep rStep, RStepEventListener rStepEventListener) {
		super();
		this.gcodeFile = gcodeFile;
		this.rStep = rStep;
		this.rStepEventListener = rStepEventListener;
		
		try {
			gcodeFileHandler = new GCodeFileHandler(gcodeFile);
			changeStateTo = PlayerStates.READY;
		} catch (IOException e) {
			// input file problem
			fire(new RStepPlayerEvent(this, e));
		}
		start();
	}

	public void play() {
		synchronized(this) {
			changeStateTo = PlayerStates.PLAYING;
			this.notify();
		}
	}

	public boolean isPlaying() {
		synchronized(this) {
			return changeStateTo == PlayerStates.PLAYING;
		}
	}

	public void pause() {
		synchronized(this) {
			changeStateTo = PlayerStates.PAUSED;
			this.notify();
			// notifying the thread isn't really necessary
			// but this avoids that a listener stays stucked waiting the PAUSE event
		}
	}
	
	public void abort() {
		synchronized(this) {
			changeStateTo = PlayerStates.ABORTED;
			this.notify();
		}
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
						fire(new RStepPlayerEvent(this, e));
					}
					if(line == null) {
						// no more line available
						System.err.println("end of " + gcodeFile + " reached");
						changeStateTo = PlayerStates.FINISHED;
					}
					if(changeStateTo != state) {
						fire(new RStepPlayerEvent(this, changeStateTo));
						state = changeStateTo;
					}
					break;
				case READY:
				case PAUSED:
					if(changeStateTo != state) {
						fire(new RStepPlayerEvent(this, changeStateTo));
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
					fire(new RStepPlayerEvent(this, changeStateTo));
					break;
				}
			}
			
			if(line != null) {
				// remove unnecessary spaces
				String cleanLine = line.trim();
				// remove comments (within parenthesis)
				cleanLine = cleanLine.replaceAll("\\(.*\\)", "");
				
				if(cleanLine.length() > 0) {
					// send the available line to rStep
					rStep.sendExpectOk(cleanLine);
				} else {
					System.err.println("skip [" + line + "]");
				}
			}
		}
	}
	
	private synchronized void fire(RStepEvent event) {
		if(rStepEventListener != null) {
			rStepEventListener.eventHandler(event);
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
