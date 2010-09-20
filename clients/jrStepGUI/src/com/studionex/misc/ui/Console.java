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

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

/**
 * @see http://www.exampledepot.com/egs/javax.swing.text/ta_Console.html
 */
@SuppressWarnings("serial")
public class Console extends JPanel {
	public static final int MAX_LENGTH = 10000;
	
	public enum Streams {OUT, ERR, OUTERR};
	private Streams stream;

//	private PipedInputStream piOut;
//	private PipedInputStream piErr;
//	private PipedOutputStream poOut;
//	private PipedOutputStream poErr;
	private JTextArea textArea;

	public Console(Streams stream) throws IOException {
		// Add a scrolling text area
		textArea = new JTextArea();
		textArea.setEditable(false);
		this.setLayout(new GridLayout(1, 1));
		this.add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
		setVisible(true);

		this.stream = stream;
		connectPipes();
    }
	
	private void connectPipes() throws IOException {
		if(stream != Streams.ERR) {
			// Set up System.out
			PipedInputStream pipedInputStream = new PipedInputStream();
			System.setOut(new PrintStream(new PipedOutputStream(pipedInputStream), true));
			new ReaderThread(pipedInputStream).start();
		}
		
		if(stream != Streams.OUT) {
			// Set up System.err
			PipedInputStream pipedInputStream = new PipedInputStream();
			System.setErr(new PrintStream(new PipedOutputStream(pipedInputStream), true));
			new ReaderThread(pipedInputStream).start();
		}
	}

	private class ReaderThread extends Thread {
		BufferedReader pi;

		ReaderThread(PipedInputStream pi) {
			this.pi = new BufferedReader(new InputStreamReader(pi));
		}

		public void run() {
			try {
				while(true) {
					final String line = pi.readLine();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							textArea.append(line + '\n');

							try {
								int documentLength = textArea.getDocument().getLength();
								if(documentLength >= MAX_LENGTH) {
									// search the end position of the first line
									int p = 0;
									while((p < documentLength) && !textArea.getDocument().getText(p, 1).equals("\n"))
										p++;
									if(p < documentLength) {
										// remove the first line
										textArea.getDocument().remove(0, p + 1);
									}
								}
							} catch (BadLocationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							// Make sure the last line is always visible
							textArea.setCaretPosition(textArea.getDocument().getLength());
						}
					});
				}
			} catch (IOException e) {
				if(e.getMessage() == "Pipe broken") {
					// TODO: stronger test (w/o using getMessage())
					try {
						connectPipes();
					} catch (IOException e1) {
						// TODO: log this?
						e1.printStackTrace();
					}
				} else {
					// TODO: log this?
					e.printStackTrace();
				}
			}
		}
	}
}
