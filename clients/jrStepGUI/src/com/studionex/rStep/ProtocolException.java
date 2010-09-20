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
 * Signals that a protocol exception has occurred on an RStep instance.
 *
 * @author  Jean-Louis Paquelin
 */
@SuppressWarnings("serial")
public class ProtocolException extends RStepException {
	private String protocolError;
	
    /**
     * Constructs an {@code ProtocolException} with the specified detail message.
     *        
     * @param protocolError
     *        The protocol error (which is saved for later retrieval
     *        by the {@link #getProtocolError()} method)
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     */
    public ProtocolException(String protocolError, String message) {
    	super(message);
    	this.protocolError = protocolError;
    }

    /**
     * Constructs an {@code ProtocolException} with the specified detail message
     * and cause.
     *
     * <p> Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated into this exception's detail
     * message.
     *        
     * @param protocolError
     *        The protocol error (which is saved for later retrieval
     *        by the {@link #getProtocolError()} method)
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     *
     * @param cause
     *        The cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A null value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     */
    public ProtocolException(String protocolError, String message, Throwable cause) {
        super(message, cause);
    	this.protocolError = protocolError;
    }

    /**
     * Constructs an {@code ProtocolException} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of {@code cause}).
     *        
     * @param protocolError
     *        The protocol error (which is saved for later retrieval
     *        by the {@link #getProtocolError()} method)
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     *        
     * @param cause
     *        The cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A null value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     */
    public ProtocolException(String protocolError, Throwable cause) {
        super(cause);
    	this.protocolError = protocolError;
    }

    /**
     * 
     * @return the protocol error causing the exception.
     */
	public String getProtocolError() {
		return protocolError;
	}
}
