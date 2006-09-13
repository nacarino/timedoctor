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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.nxp.timedoctor.core.model.SampleLine.LineType;

/**
 * Represents a collection of trace lines of a single type. The section itself
 * has no knowledge of what that type is.
 */
public class Section {
// MR change name, little more clear what a "key" is
	/**
	 * Constant equal to 28 for creating lines' lookups keys.
	 */
	private static final int KEY_CONSTANT = 28;

	/**
	 * The model that contains this section.
	 */
	private TraceModel model;

	/**
	 * ArrayList of SampleLines for storing the lines in this section, in the
	 * order in which they were added.
	 */
	// checkstyle seems to not be updated for J2SE5 type parameterization.
	private ArrayList < SampleLine > lines = new ArrayList < SampleLine > ();

	/**
	 * HashMap for easy lookup of lines. Uses the integer hash code of the line
	 * (constructed from line and CPU ids) as the lookup key.
	 */
	// checkstyle seems to not be updated for J2SE5 type parameterization.
	private HashMap < Integer, ArrayList < SampleLine > > hash = new HashMap < Integer, ArrayList < SampleLine > > ();

	/**
	 * The type of lines contained in this section.
	 */
	private LineType type;

	/**
	 * Section constructor, takes a model, a LineType (eventually try to
	 * abstract this out) and a name.
	 * 
	 * @param model
	 *            the model of which this section is a part
	 * @param type
	 *            the type of lines in this section
	 */
	public Section(final TraceModel model, final LineType type) {
		this.model = model;
		this.type = type;
	}

	/**
	 * Returns the ArrayList of lines. For use in iterable for loops.
	 * 
	 * @return ArrayList of lines
	 */
	public final ArrayList < SampleLine > getLines() {
		return lines;
	}

	// MR UGLY, see if you can remove this. Responsibility of this class is not to hold the model
	/**
	 * @return the model associated with this section
	 */
	public final TraceModel getModel() {
		return model;
	}

	/**
	 * @return the number of lines in the section
	 */
	public final int getNrLines() {
		return lines.size();
	}

	/**
	 * Adds a line to the section and registers it.
	 * 
	 * @param line
	 *            the line to be added
	 */
	public final void addLine(final SampleLine line) {
		lines.add(line);
		// MR can this not be done during line creation, as constructor argument?
		line.setSection(this);	
		registerLine(line);
	}

	/**
	 * Registers a line in this section's HashMap for quick line lookup.
	 * 
	 * @param line
	 *            the line to be registered
	 */
	public final void registerLine(final SampleLine line) {
		int key = line.hashCode();
		ArrayList < SampleLine > lineList = hash.get(key);
		if (lineList == null) {
			lineList = new ArrayList < SampleLine > ();
			hash.put(key, lineList);
		}
		lineList.add(line);
	}

	/**
	 * Returns the line with the specified cpu and id, active at the given time.
	 * If no such line exists, returns null.
	 * 
	 * @param cpu
	 *            the cpu associated with the line
	 * @param id
	 *            the id of the line
	 * @param time
	 *            a time at which the line was active
	 * @return the line, or null if it does not exist
	 */
	public final SampleLine getLine(final SampleCPU cpu, final int id,
			final double time) {
		int key = (cpu.getID() << KEY_CONSTANT) | id;
		ArrayList < SampleLine > lineList = hash.get(key);
		// MR hash.get will never return null but create a new entry if the key does not exist?
		SampleLine activeLine = null;
		if (lineList != null) {
			// MR add comment explaining this
			for (SampleLine line : lineList) {
				if (line.isValid(time)) {
					activeLine = line;
					break;
				}
			}
		}
		return activeLine;
	}

	/**
	 * Returns an iterator over the lines in the section. Replaces the
	 * GetEnumerator function in the original C# implementation.
	 * 
	 * @return an iterator over the <code>lines</code> ArrayList
	 */
	public final Iterator getIterator() {
		return lines.iterator();
	}

	/**
	 * Returns the type of line contained in this section.
	 * 
	 * @return the line type of this section
	 */
	public final LineType getType() {
		return type;
	}

}
