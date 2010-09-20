package com.studionex.misc.ui;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class TimedJLabel extends JLabel {
	private Timer timer;
	
	public TimedJLabel() { super(); }
	public TimedJLabel(Icon icon, int horizontalAlignment) { super(icon, horizontalAlignment); }
	public TimedJLabel(Icon icon) { super(icon); }
	public TimedJLabel(String text, Icon icon, int horizontalAlignment) { super(text, icon, horizontalAlignment); }
	public TimedJLabel(String text, int horizontalAlignment) { super(text, horizontalAlignment); }
	public TimedJLabel(String text) { super(text); }

	public void setText(String text, int delay) {
		setText(text);

		if(timer == null) {
			timer = new Timer(delay, new TimerListener(this));
			timer.setRepeats(false);
			timer.start();
		} else {
			timer.setInitialDelay(delay);
			timer.restart();
		}
	}
	
	private static class TimerListener implements ActionListener {
		private TimedJLabel timedJLabel;

		public TimerListener(TimedJLabel jLabel) {
			this.timedJLabel = jLabel;
		}
		
    	public void actionPerformed(ActionEvent e) {
    		timedJLabel.setText(" ");
    	}
    }
}
