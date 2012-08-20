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

/**
 * Class representing a CPU on which tasks, etc., are being executed. Keeps
 * track of speed and memory speed data, and has a link back to the model.
 */
public class SampleCPU {
	/**
	 * TraceModel to which this cpu belongs.
	 */
	private TraceModel model;

	/**
	 * The name of the cpu. If the file was a TDII file, this will always be
	 * null.
	 */
	private String name;

	/**
	 * The speed of the processor in clocks per second. Default value is 1.
	 */
	private double clocksPerSec = 1;

	/**
	 * The memory speed in clocks per second. Default value is 1.
	 */
	private double memClocksPerSec = 1;

	/**
	 * Boolean singleMem (look up what this does?).
	 */
	private boolean singleMem = true;

	/**
	 * Current visibility state of the cpu.
	 */
	private boolean visible = true;

	/**
	 * The integer id of the cpu.
	 */
	private int id = 0;

	/**
	 * Constructor for a new CPU.
	 * 
	 * @param model
	 *            the model with which the cpu is associated
	 * @param id
	 *            the integer id of the cpu
	 * @param name
	 *            the name of the cpu
	 * @param memClocksPerSec
	 *            memory clocks per second
	 */
	public SampleCPU(final TraceModel model, final int id, final String name,
			final double memClocksPerSec) {
		this.model = model;
		this.name = name;		
		this.id = id;
		this.memClocksPerSec = memClocksPerSec;
	}

	// MR where needed? Ugly, remove if you can. CPU class is not responsible for holding the model
	/**
	 * @return the model with which the cpu is associated.
	 */
	public final TraceModel getModel() {
		return model;
	}

	/**
	 * @return the id of the cpu
	 */
	public final int getID() {
		return id;
	}

	// MR when needed? Can we not only set the ID during construction?
	// Remove if you can
	/**
	 * Sets the id of the cpu.
	 * 
	 * @param  id
	 *            the id of the cpu
	 */
	public final void setID(final int id) {
		this.id = id;
	}

	/**
	 * @return the name of the CPU
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the clocks-per-second value of the cpu.
	 * 
	 * @return the clocks-per-second value of the cpu
	 */
	public final double getClocksPerSec() {
		return clocksPerSec;
	}

	/**
	 * Sets the clocks-per-second value of the cpu.
	 * 
	 * @param cps
	 *            the new clocks-per-second value
	 */
	public final void setClocksPerSec(final double cps) {
		clocksPerSec = cps;
	}

	/**
	 * @return the mem clocks per second value
	 */
	public final double getMemClocksPerSec() {
		return memClocksPerSec;
	}

	/**
	 * Set the mem clocks per second value, setting the single mem boolean if
	 * necessary.
	 * 
	 * @param mcps
	 *            the new value
	 */
	public final void setMemClocksPerSec(final double mcps) {
		if (memClocksPerSec > 1) {
			singleMem = false;
		}
		memClocksPerSec = mcps;
	}

	/**
	 * Returns the single mem value.
	 * 
	 * @return single mem
	 */
	public final boolean getSingleMemSpeed() {
		return singleMem;
	}
	
	/**
	 * Returns the visibility state of the cpu.
	 * 
	 * @return boolean visibility state
	 */
	public final boolean getVisible() {
		return visible;
	}

	/**
	 * Set the visibility state of the cpu.
	 * 
	 * @param visibility
	 *            the new visibility state
	 */
	public final void setVisible(final boolean visibility) {
		this.visible = visibility;
	}

}
