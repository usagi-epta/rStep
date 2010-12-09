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
public class ReplyEvent extends InputEvent {
	public static enum Kind {OK, CURRENT_ERROR, GCODE_NOT_SUPPORTED_ERROR, INIT_ERROR, STEP_TYPE_ERROR, ADDOBJ_FULL_ERROR, CHECKSUM};

	private final Kind kind;

	public ReplyEvent(Object source, Kind kind) {
		super(source);
		
		this.kind = kind;
	}

	public boolean isOk() { return getKind() == Kind.OK; }
	
	public Kind getKind() { return kind; }

	@Override
	public String toString() {
		return "Reply: " + getKind().toString();
	}
}
