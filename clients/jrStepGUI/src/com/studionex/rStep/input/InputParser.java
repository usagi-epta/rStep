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

import com.studionex.event.EventManager;

/**
 * 
 * @author Jean-Louis Paquelin
 *
 */
public class InputParser {
	private EventManager<InputEventListener, InputEvent> eventManager = new EventManager<InputEventListener, InputEvent>();

	protected void handleInput(String s) {
		parse_rStepOutput(s);
	}
	
	/**
	 * Parses s enforcing this grammar:
	 * 
	 * <rStep output> ::- ( <GCode reply> | <message> | 'START' )
	 * <GCode reply> ::-  'OK' | ( 'ERR ' <error type> )
	 * <error type> ::- 'INIT' | 'CUR' | 'STEP_TYPE' | 'ADDOBJ_FULL' | 'NOT_SUPPORTED'
	 * <message> ::- 'MSG ' <message content>
	 * <message content> ::- <step by inch message> | <max feed rate message> | <current message> | <stepping message> | <absolute mode message> | <coordinates message> | <debug message>
	 * <step by inch message> ::- 'SBI(' <NUMBER> ',' <NUMBER> ',' <NUMBER> ')'
	 * <max feed rate message> ::- 'MFR(' <NUMBER> ',' <NUMBER> ',' <NUMBER> ')'
	 * <current message> ::- 'Cur(' <NUMBER> ',' <NUMBER> ',' <NUMBER> ')'
	 * <stepping message> ::- 'Step(' <NUMBER> ')'
	 * <absolute mode message> ::- 'Abs(' <NUMBER> ')'
	 * <coordinates message> ::- 'Coord(' <NUMBER> ',' <NUMBER> ',' <NUMBER> ')'
	 * <debug message> ::- 'Debug(' <STRING> ')'
	 * 
	 * Fires ReplyEvent when s is valid input 
	 * @param s
	 */
	private void parse_rStepOutput(String s) {
		String sUp = s.trim().toUpperCase();
		if(sUp.matches("^OK$")) {
			fire(new ReplyEvent(this, ReplyEvent.Kind.OK));
		} else if(sUp.matches("^ERR .*$")) {
			String e = sUp.replaceFirst("^ERR *(.*)$", "$1");
			if(e.equals("INIT")) {
				fire(new ReplyEvent(this, ReplyEvent.Kind.INIT_ERROR));
			} else if(e.equals("CUR")) {
				fire(new ReplyEvent(this, ReplyEvent.Kind.CURRENT_ERROR));
			} else if(e.equals("STEP_TYPE")) {
				fire(new ReplyEvent(this, ReplyEvent.Kind.STEP_TYPE_ERROR));
			} else if(e.equals("ADDOBJ_FULL")) {
				fire(new ReplyEvent(this, ReplyEvent.Kind.ADDOBJ_FULL_ERROR));
			} else if(e.equals("NOT_SUPPORTED")) {
				fire(new ReplyEvent(this, ReplyEvent.Kind.GCODE_NOT_SUPPORTED_ERROR));
			} else
				fire(new SyntaxMessageEvent(this, s));
		} else if(sUp.matches("^MSG .*$")) {
			String e = sUp.replaceFirst("^MSG *(.*)$", "$1");
			Double[] v;
			if(e.matches("SBI *\\(.*\\)$") && (v = parse_numbers(3, e)) != null) {
				fire(new StepByInchMessageEvent(this, v[0].intValue(), v[1].intValue(), v[2].intValue()));
				
			} else if(e.matches("MFR *\\(.*\\)$") && (v = parse_numbers(3, e)) != null) {
				fire(new FeedRateMessageEvent(this, v[0], v[1], v[2]));
				
			} else if(e.matches("CUR *\\(.*\\)$") && (v = parse_numbers(3, e)) != null) {
				fire(new CurrentMessageEvent(this, v[0], v[1], v[2]));
				
			} else if(e.matches("STEP *\\(.*\\)$") && (v = parse_numbers(1, e)) != null) {
				fire(new SteppingMessageEvent(this, v[0].intValue()));
				
			} else if(e.matches("ABS *\\(.*\\)$") && (v = parse_numbers(1, e)) != null) {
				fire(new AbsoluteModeMessageEvent(this, !(v[0].intValue() == 0)));
				
			} else if(e.matches("COORD *\\(.*\\)$") && (v = parse_numbers(3, e)) != null) {
				fire(new CoordinatesMessageEvent(this, v[0], v[1], v[2]));

			} else if(e.matches("DEBUG *\\(.*\\)$")) {
				fire(new DebugMessageEvent(this, s.replaceFirst("^.*\\((.*)\\)$", "$1")));

			} else
				fire(new SyntaxMessageEvent(this, s));
		} else if(sUp.matches("^START$")) {
			fire(new StartEvent(this));
		} else
			fire(new SyntaxMessageEvent(this, s));
	}

	Double[] parse_numbers(int count, String input) {
		// get an array of numbers from an input like aText(number,number,number)
		String[] splittedOnCommas = input.replaceFirst("^.*\\((.*)\\)$", "$1").split(" *, *");
		if(splittedOnCommas.length == count) {
			Double[] numbers = new Double[count];
			for(int c = 0; c < count; c++) {
				try {
					numbers[c] = new Double(splittedOnCommas[c]);
				} catch(NumberFormatException e) {
					return null;
				}
			}
			return numbers;
		}
		return null;
	}
		
	public void add(InputEventListener eventListener) { eventManager.add(eventListener); }
	public void remove(InputEventListener eventListener) { eventManager.remove(eventListener); }
	public void fire(InputEvent event) { eventManager.fire(event); }	

	/**
	 * This method was written for test purposes.
	 * @param args
	 */
	public static void main(String[] args) {
		InputParser ip = new InputParser();
		InputEventListener eventListener = new InputEventListener() {

			public void eventHandler(InputEvent event) {
				System.out.println(event.toString());
			}
			
		};
		ip.add(eventListener);
		
		ip.parse_rStepOutput("START");
		ip.parse_rStepOutput("ERR CUR");
		ip.parse_rStepOutput("ERR something");
		ip.parse_rStepOutput("OK");
		ip.parse_rStepOutput("something OK");
		ip.parse_rStepOutput("MSG something");
		ip.parse_rStepOutput("MSG SBI (1.2, 3.4)");
		ip.parse_rStepOutput("MSG SBI (1.2, 3.4, 4.5)");
		ip.parse_rStepOutput("MSG SBI (1, 2, 4)");
		ip.parse_rStepOutput("MSG MFR (1.2, 3.4, 5.6)");
		ip.parse_rStepOutput("MSG Cur (1.2, 3.4, 5.6)");
		ip.parse_rStepOutput("MSG Step (1)");
		ip.parse_rStepOutput("MSG Step (1.2)");
		ip.parse_rStepOutput("MSG Step (4)");
		ip.parse_rStepOutput("MSG Abs(0)");
		ip.parse_rStepOutput("MSG Abs(0.0)");
		ip.parse_rStepOutput("MSG Abs(1)");
		ip.parse_rStepOutput("MSG Coord(1.2, 3.4, 5.6)");
		ip.parse_rStepOutput("MSG debug(The quick brown)");
	}
}
