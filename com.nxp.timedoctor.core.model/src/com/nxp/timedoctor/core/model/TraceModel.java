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

/**
 * The main TimeDoctor model class. Keeps track of all sections and cpus, as
 * well as having a registry of description names by id.
 */
public class TraceModel {
	/**
	 * The end time of all activity on all cpus in the model.
	 */
	private double endTime;

	/**
	 * The number of ticks per second in the time base used in the file.
	 */
	private double ticksPerSec = 1;

	/**
	 * The list of sections in the model.
	 */
	private SectionList sections = new SectionList();

	/**
	 * ArrayList of all cpus in the model.
	 */
	// Checkstyle not updated for type parameterization in J2SE5
	private ArrayList < SampleCPU > cpus = new ArrayList < SampleCPU > ();

	// MR what does this do? Why not in the queue class?
	/**
	 * Maximum queue sample.
	 */
	private double maxQueuesSample = 1;

	// MR what does this do? Improve comment or refactor
	/**
	 * Maximum value sample.
	 */
	private double maxValuesSample = 1;

	/**
	 * Hash table of description names keyed by description id.
	 */
 	private HashMap < Integer, String > descrNames = new HashMap < Integer, String > ();

	// MR improve comment
	/**
	 * Returns the file's end time.
	 * 
	 * @return the end time
	 */
	public final double getEndTime() {
		return endTime;
	}

	/**
	 * Calculates and sets the model's end time.
	 * 
	 */
	public final void setEndTime() {
		for (Section section : sections.values()) {
			for (SampleLine line : section.getLines()) {
				endTime = Math.max(line.getEndTime(), endTime);
			}
		}
	}

	// MR factor out into DescriptionList class (or at least do this
	// consistently for all lists in the model)
	/**
	 * Adds a description name to the hash table, using its integer id as the
	 * lookup key.
	 * 
	 * @param id
	 *            description id
	 * @param name
	 *            description name
	 */
	public final void addDescrName(final int id, final String name) {
		descrNames.put(id, name);
	}

	/**
	 * Returns the description name associated with the given id, or null the id
	 * is not in the HashMap.
	 * 
	 * @param id
	 *            the description id to search for
	 * @return the associated name
	 */
	public final String findDescrName(final int id) {
		return descrNames.get(id);
	}

	/**
	 * @return the list of sections in this model
	 */
	public final SectionList getSections() {
		return sections;
	}

	// MR Factor out into CpuList class, including getCpu and multiCpu functions
	/**
	 * Returns the ArrayList of cpus.
	 * 
	 * @return ArrayList of cpus
	 */
	public final ArrayList < SampleCPU > getCPUs() {
		return cpus;
	}

	/**
	 * Returns the cpu with the given id from the ArrayList of cpus.
	 * 
	 * @param id
	 *            the id to use in the search
	 * @return the associated cpu
	 */
	public final SampleCPU getCPU(final int id) {
		for (SampleCPU cpu : cpus) {
			if (cpu.getID() == id) {
				return cpu;
			}
		}
		return null;
	}

	/**
	 * @return true if there are multiple cpus, false if there are not
	 */
	public final boolean multiCPU() {
		return (cpus.size() > 1);
	}

	// MR move to CpuLIst class
	/**
	 * Adds the given cpu to the list of cpus.
	 * 
	 * @param cpu
	 *            the cpu to be added
	 */
	public final void addCPU(final SampleCPU cpu) {
		cpus.add(cpu);
	}

	/**
	 * Returns the number of ticks per second.
	 * 
	 * @return number of ticks per second
	 */
	public final double getTicksPerSec() {
		return ticksPerSec;
	}

	/**
	 * Sets the number of ticks per second. If this method is never called, the
	 * value is 1.
	 * 
	 * @param tps
	 *            the number of ticks per second
	 */
	public final void setTicksPerSec(final double tps) {
		this.ticksPerSec = tps;
	}

	/**
	 * Computes and sets the internal max queue sample and max value sample
	 * variables.
	 */
	public final void computeMaxValues() {
		Section queues = sections.getSection(SampleLine.LineType.QUEUES);
		Section values = sections.getSection(SampleLine.LineType.VALUES);
		// MR move functionality to section class, genneric implementation
		if (queues != null) {
			for (SampleLine line : queues.getLines()) {
				maxQueuesSample = Math.max(line.getMaxSampleValue(),
						maxQueuesSample);
			}
		}
		if (values != null) {
			for (SampleLine line : values.getLines()) {
				maxValuesSample = Math.max(line.getMaxSampleValue(),
						maxValuesSample);
			}
		}
	}
}
