/*******************************************************************************
 * Copyright (c) 2006-2013 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.core.model;

/**
 * Checked exception for use in methods where behavior of the caller must be
 * modified if an illegal argument has been passed in.
 */
// MR change name (a bit shorter)
// MR move to parser??
public class CheckedIllegalArgumentException extends Exception {
// MR what is this?
	/**
	 * 
	 */
	private static final long serialVersionUID = -347494349910812598L;

	/**
	 * Constructs an exception with a null message.
	 * 
	 */
	public CheckedIllegalArgumentException() {
		super();
	}

	/**
	 * Constructs an exception with the given message.
	 * 
	 * @param msg a message containing further information about the exception
	 */
	public CheckedIllegalArgumentException(final String msg) {
		super(msg);
	}

}
