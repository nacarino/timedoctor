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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import net.timedoctor.core.model.SampleLine.LineType;

/**
 * The main TimeDoctor model class. Keeps track of all sections and cpus, as
 * well as having a registry of description names by id.
 */
public class TraceModel extends Observable {
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

	private HashMap<LineType, Double> maxValueMap = new HashMap<LineType, Double>();
	
	/**
	 * Hash table of description names keyed by description id.
	 */
 	private HashMap < Integer, String > descrNames = new HashMap < Integer, String > ();
 	
 	private double maxClockSpeed = 1;
 	private double minRoundedResolution = 1;

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
	 * Computes and sets the max value for each LineType
	 */
	public final void computeMaxValues() {
		for (LineType type:LineType.values()) {
			computeMaxSample(type);
		}
		
		computeMaxClockSpeed();
	}

	private void computeMaxClockSpeed() {
		for (SampleCPU cpu: cpus) {
			maxClockSpeed = Math.max(maxClockSpeed, cpu.getClocksPerSec());
		}
		
		final double timePeriod = 1.0 / maxClockSpeed;
		final String tpStr = Double.toString(timePeriod);
		final int index = tpStr.indexOf("E");
		
		String exponent = (index == -1)?"0":tpStr.substring(index + 1);
		minRoundedResolution = Math.pow(10.0, Double.parseDouble(exponent));
	}
	
	private void computeMaxSample(final LineType type) {
		double maxSample = 1;
		
		Section section = sections.getSection(type);
		if (section != null) {
			for (SampleLine line: section.getLines()) {
				maxSample = Math.max(line.getMaxSampleValue(), maxSample);
			}
		}
		
		maxValueMap.put(type, maxSample);
	}
	
	public double getMaxSampleValue(final LineType type) {
		return maxValueMap.get(type);
	}

	/* (non-Javadoc)
	 * @see java.util.Observable#setChanged()
	 */
	@Override
	public synchronized void setChanged() {
		super.setChanged();
		super.notifyObservers();
	}
	
	/**
	 * Returns the maximum clock speed of all the CPUs in this trace
	 * 
	 * @return
	 * 			The maximum clock speed
	 */
	public final double getMaxClockSpeed() {
		return maxClockSpeed;
	}
	
	/**
	 * Returns the minimum rounded time-resolution of all the CPUs in this trace
	 * The rounding is done towards the lesser power of ten.
	 * <p>
	 * Eg., if the max frequency in the trace is 200 Hz, then the time period is
	 * 1/200 = 0.005 or 5e-3. Because of the rounding, the value returned in 1.0e-3 
	 *  
	 * @return
	 * 		The rounded minimum time-resolution
	 */
	public final double getMinTimeResolution() {
		return minRoundedResolution;
	}
}
