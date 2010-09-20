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

import java.io.IOException;


@SuppressWarnings("serial")
public class RStepPlayerEvent extends RStepEvent {
	public static enum PlayerReason {PLAYER_STATE, IO_EXCEPTION};

	private final PlayerReason playerReason; 

	private final PlayThread.PlayerStates state;
	private final IOException ioException;

	public RStepPlayerEvent(Object source, PlayThread.PlayerStates state) {
		this(source, PlayerReason.PLAYER_STATE, state, null);
	}
	
	public RStepPlayerEvent(Object source, IOException ioException) {
		this(source, PlayerReason.IO_EXCEPTION, null, ioException);
	}
		
	protected RStepPlayerEvent(Object source, PlayerReason playerReason, PlayThread.PlayerStates state, IOException ioException) {
		super(source);
		this.playerReason = playerReason;
		this.state = state;
		this.ioException = ioException;
	}

	public PlayerReason getPlayerReason() { return playerReason; }

	public PlayThread.PlayerStates getState() { return state; }
	public IOException getIOException() { return ioException; }

	@Override
	public String toString() {
		switch(getPlayerReason()) {
		case PLAYER_STATE:
			return super.toString() + " " + getState();
		case IO_EXCEPTION:
			return super.toString() + " " + getIOException();
		}
		return super.toString();
	}
}
