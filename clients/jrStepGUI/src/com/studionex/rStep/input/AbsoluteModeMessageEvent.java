package com.studionex.rStep.input;

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

@SuppressWarnings("serial")
public class AbsoluteModeMessageEvent extends InputEvent {
	private final boolean absoluteMode;

	public AbsoluteModeMessageEvent(Object source, boolean absoluteMode) {
		super(source);

		this.absoluteMode = absoluteMode;
	}

	protected boolean getAbsoluteMode() { return absoluteMode; }

	@Override
	public String toString() {
		return "Absolute mode: " + getAbsoluteMode();
	}
}
