package com.studionex.event;

/*
 * Copyright 2010 Jean-Louis Paquelin
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact info: jrstepgui@studionex.com
 */

import java.util.EventObject;
import java.util.Vector;

public class EventManager<L extends EventListener<E>, E extends EventObject> {
	private Vector<L> listeners;
	
	public EventManager() {
		listeners = new Vector<L>();
	}
	
	public synchronized void add(L eventListener) {
		listeners.add(eventListener);
	}
	
	public synchronized void remove(L eventListener) {
		listeners.remove(eventListener);
	}
	
	public synchronized void fire(E event) {
		for(L eventListener: listeners) {
			eventListener.eventHandler(event);
		}
	}
}
