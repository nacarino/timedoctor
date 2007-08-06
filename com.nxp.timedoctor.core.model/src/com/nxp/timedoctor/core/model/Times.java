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
package com.nxp.timedoctor.core.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Utility class containing methods for formatting time strings.
 */
// MR find more desctiptive name
public final class Times {

	/**
	 * Constant representing the precision value for time = 0.
	 */
	private static final double PREC_0 = 1.0E-12;

	/**
	 * Constant representing the precision value for seconds.
	 */
	private static final double PREC_S = 0.1;

	/**
	 * Constant representing the precision value for milliseconds.
	 */
	private static final double PREC_MS = 1.0E-4;
	
	/**
	 * Constant representing the precision value for microseconds.
	 */
	private static final double PREC_US = 1.0E-7;

	/**
	 * Constant representing the multiplier value for milliseconds.
	 */
	private static final double MULT_MS = 1.0E3;

	/**
	 * Constant representing the multiplier value for microseconds.
	 */
	private static final double MULT_US = 1.0E6;
	
	/**
	 * Constant representing the multiplier value for nanoseconds.
	 */
	private static final double MULT_NS = 1.0E9;

	/**
	 * Private constructor to prevent class instantiation.
	 * 
	 */
	private Times() {
	}

	/**
	 * Returns a properly formatted string representation of the given time,
	 * using the time itself as a precision value.
	 * 
	 * @param t
	 *            the time to be formatted
	 * @return a formatted string representing the time
	 */
	public static String timeToString(final double t) {
		return timeToString(t, t);
	}

	/**
	 * Returns a properly formatted string representation of the given time,
	 * using the supplied precision.
	 * 
	 * @param time
	 *            the time to be formatted
	 * @param precision
	 *            the accuracy to use in the final string representation
	 * @return a formatted string representing the time
	 */
	public static String timeToString(final double time,
			final double precision) {
	    
		String s;
		if (time < PREC_0) {
			s = "0s";
		} else if (precision >= PREC_S) {
			NumberFormat timeFormat = new DecimalFormat("0.###s");
			s = timeFormat.format(time);
		} else if (precision >= PREC_MS) {
			NumberFormat timeFormat = new DecimalFormat("0.###ms");
			s = timeFormat.format(time * MULT_MS);
		} else if(precision >= PREC_US) {
			NumberFormat timeFormat = new DecimalFormat("0.###us");
			s = timeFormat.format(time * MULT_US);
		} else {
			NumberFormat timeFormat = new DecimalFormat("0.###ns");
			s = timeFormat.format(time * MULT_NS);
		}
		return s;
	}
}
