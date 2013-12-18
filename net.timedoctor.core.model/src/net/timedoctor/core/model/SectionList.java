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

import java.util.Collection;
import java.util.HashMap;

import net.timedoctor.core.model.SampleLine.LineType;

// MR class may be merged with TraceModel if no classes use this list directly without using the model
/**
 * Organizes all sections in the model into one coherent grouping, placing them
 * in a hash table using their type as a key. This allows for fast lookup and
 * easily checking whether a section exists yet.
 */
public class SectionList {

	/**
	 * HashMap to contain sections, keyed by line type.
	 */
	private HashMap < LineType, Section > sectionMap = new HashMap < LineType,
		Section > ();

	/**
	 * Adds the specified section to the SectionList, associating it with the
	 * given type.
	 * 
	 * @param key
	 *            the type to use as a lookup key
	 * @param section
	 *            the section to be inserted
	 */
	// MR change argument key into lineType
	public final void addSection(final LineType key, final Section section) {
		sectionMap.put(key, section);
	}

	/**
	 * Returns the section containing lines of the specified type.
	 * 
	 * @param key
	 *            the LineType of the section, used as a lookup key
	 * @return the associated section
	 */
	// MR change argument key into lineType
	public final Section getSection(final LineType key) {
		return sectionMap.get(key);
	}

	/**
	 * Returns a Collection of the values in the section map. Useful for for
	 * loops in which all existing sections need to be iterated over.
	 * 
	 * @return a collection of the values in the map
	 */
	public final Collection < Section > values() {
		return sectionMap.values();
	}
}
