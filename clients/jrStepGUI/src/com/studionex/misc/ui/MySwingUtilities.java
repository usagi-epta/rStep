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
 * Contact info: jlp@studionex.com
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MySwingUtilities {
	
	public static void setNativeLookAndFeel() {
		// Get the native look and feel class name
		String nativeLF = UIManager.getSystemLookAndFeelClassName();
		// Install the look and feel
		try {
			UIManager.setLookAndFeel(nativeLF);
		} catch (ClassNotFoundException e) {
			// TODO: log this?
			//e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO: log this?
			//e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO: log this?
			//e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO: log this?
			//e.printStackTrace();
		}
	}
	
	public static void displayCentered(Component component) {
		// move component to the screen center
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		component.setLocation(
				(screenDimension.width - component.getSize().width) / 2,
				(screenDimension.height - component.getSize().height) / 2); 
	}
	
}
