package com.studionex.rStep;

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

/**
 * @see http://linuxcnc.org/handbook/gcode/g-code.html
 * @see http://www.syil.ca/index.php?option=com_content&view=article&id=41&Itemid=45
 */
public enum GCodes {
	FEEDRATE								("F", -1, "", "feedrate", true),

	RAPID_POSITIONING						("G",  0, "", 				  "rapid positioning", true),
	LINEAR_INTERPOLATION					("G",  1, "", 				  "linear interpolation", true),
	CLOCKWISE_CIRCULAR_INTERPOLATION		("G",  2, "", 				  "clockwise circular interpolation", true),
	COUNTERCLOCKWISE_CIRCULAR_INTERPOLATION	("G",  3, "", 				  "counterclockwise circular interpolation", true),
	DWELL									("G",  4, "", 				  "dwell", true),
	INCH_SYSTEM_SELECTION					("G", 20, "inch", 			  "inch system selection", true), 
	MILLIMETER_SYSTEM_SELECTION				("G", 21, "millimeter", 	  "millimeter system selection", true),
	RETURN_TO_HOME							("G", 28, "go home", 		  "return to home", true),
	SECONDARY_RETURN_TO_HOME				("G", 30, "2nd go home", 	  "secondary return to home", true),
	EXACT_STOP_CHECK_MODE					("G", 61, "",				  "Exact stop check mode", false),
	DRILLING_CANNED_CYCLE					("G", 81, "", 				  "drilling canned cycle", true),
	ABSOLUTE_DISTANCE_MODE					("G", 90, "absolute mode", 	  "absolute distance mode", true), 
	INCREMENTAL_DISTANCE_MODE				("G", 91, "incremental mode", "incremental distance mode", true), 
	OFFSET_COORDINATE_SYSTEMS				("G", 92, "set home", 		  "offset coordinate systems (set home)", true), 
	INVERSE_TIME_FEED_MODE					("G", 93, "", 				  "inverse time feed mode", false),
	FEED_PER_MINUTE_MODE					("G", 94, "", 				  "feed per minute mode", false),
	
	PROGRAM_END								("M",   2, "", 			"program end", false),
	TURN_SPINDLE_CLOCKWISE					("M",   3, "motor on",	"turn spindle", true), // turn spindle clockwise
	TURN_SPINDLE_COUNTERCLOCKWISE			("M",   4, "motor on",	"turn spindle", true), // turn spindle counterclockwise
	STOP_SPINDLE_TURNING					("M",   5, "motor off",	"stop spindle turning", true),
	M80										("M",  80, "", 			"", true),
	M81										("M",  81, "", 			"", true),
	M82										("M",  82, "", 			"", true),
	M90										("M",  90, "", 			"", true),
	M98										("M",  98, "", 			"", true),
	M100									("M", 100, "", 			"specify currents in AMPS", true),
	M101									("M", 101, "", 			"specify steps/inch", true),
	M102									("M", 102, "", 			"specify max feedrate", true),
	M103									("M", 103, "",			"set stepping mode", true),
	M200									("M", 200, "", 			"save config", true),
	M201									("M", 201, "", 			"dump config", true);
	
	private final String codeLetter;
	private final int codeNumber;
	private final String title;
	private final String comment;

	GCodes(String codeLetter, int codeNumber, String title, String comment, boolean supported) {
        this.codeLetter = codeLetter;
        this.codeNumber = codeNumber;
        this.title = title;
        this.comment = comment;
    }
	
    public String code() {
    	if(codeNumber >= -1)
        	return codeLetter + codeNumber;
    	else
    		return codeLetter;
    }
    
    public String title() {
    	return title;
    }
    
    public String comment() {
    	return comment;
    }
    
    /**
     * 
     * @param input
     * @return the GCode found in the input or null otherwise
     */
    public static GCodes tokenize(String input) {
    	return null;
    }
}
