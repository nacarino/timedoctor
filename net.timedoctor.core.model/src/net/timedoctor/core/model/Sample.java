/*******************************************************************************
 * Copyright (c) 2006 Royal Philips Electronics NV.
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
 * Sample for sample lines. Contains type, time, and value data.
 */
public class Sample {
	/**
	 * Enumerates the possible sample types, assigning them all typesafe
	 * constants.
	 */
	public enum SampleType {
	
		/**
		 * Sample type constants.
		 */
		START, STOP, SUSPEND, RESUME, EVENT, END
	}

	/**
	 * The type of the sample.
	 */
	// ignoring Checkstyle suggestion to make private, have accessor
	// methods.
	public SampleType type;

	/**
	 * The time at which the sample occurred.
	 */
	// ignoring Checkstyle suggestion to make private, have accessor
	// methods.
	public double time;

	/**
	 * The value associated with the sample, if any. Else use value -1.
	 */
	// ignoring Checkstyle suggestion to make private, have accessor
	// methods.
	public double val;

	/**
	 * Constructs a sample with the given type, time, and value.
	 * 
	 * @param type
	 *            the type of sample
	 * @param time
	 *            the time at which it occurred
	 * @param value
	 *            the value of the sample, if any
	 */
	public Sample(final SampleType type, final double time,
			final double value) {
		this.type = type;
		this.time = time;
		this.val = value;
	}
}
