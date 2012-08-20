/*******************************************************************************
 * Copyright (c) 2006-2012 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.core.model;


// MR explain what this class is for
/**
 * Contains a description attached to a sample in a SampleLine.
 */
public class Description {

	/**
	 * Defines the description types, and supplies a static method to parse
	 * types froom strings containing integers (used in file parsing).
	 */
	public enum DescrType {
	
		/**
		 * Description type constants for string, number, cycles, and color
		 * descriptions.
		 */
		STRING, NUMBER, CYCLES, COLOR;
	
		/**
		 * Parses strings containing integers and returns the associated type
		 * constant. For use in file parsing.
		 * 
		 * @param type
		 *            the string to be parsed
		 * @return the associated type, or null if none exists
		 */
		public static DescrType parseString(final String type) {
			switch (Integer.parseInt(type)) {
			case 0:
				return STRING;
			case 1:
				return NUMBER;
			case 2:
				return CYCLES;
			case COLOR_ORDINAL:
				return COLOR;
			default:
				return null;
			}
		}
	}

	/**
	 * Ordinal used to convert integers to SampleTypes.
	 */
	private static final int COLOR_ORDINAL = 3;

	/**
	 * The time associated with the description.
	 */
	// ignoring Checkstyle suggestion to make private, have accessor
	// methods.
	public double time;

	/**
	 * The integer id of the description.
	 */
	// ignoring Checkstyle suggestion to make private, have accessor
	// methods.
	public int id;

	/**
	 * The type of the description.
	 */
	// ignoring Checkstyle suggestion to make private, have accessor
	// methods.
	public DescrType type;

	/**
	 * The text of the description, if any. Null otherwise.
	 */
	// ignoring Checkstyle suggestion to make private, have accessor
	// methods.
	public String text;

	/**
	 * The value associated with the description, if any.
	 */
	// ignoring Checkstyle suggestion to make private, have accessor
	// methods.
	public double value;

	/**
	 * Constructs a description with the given parameters.
	 * 
	 * @param time
	 *            the time associated with the description
	 * @param id
	 *            the id of the description
	 * @param type
	 *            the type of description
	 * @param text
	 *            the text of the description
	 * @param value
	 *            the value associated with the description
	 */
	public Description(final double time, final int id,
			final DescrType type, final String text,
			final double value) {
		this.time = time;
		this.id = id;
		this.type = type;
		this.text = text;
		this.value = value;
	}
}
