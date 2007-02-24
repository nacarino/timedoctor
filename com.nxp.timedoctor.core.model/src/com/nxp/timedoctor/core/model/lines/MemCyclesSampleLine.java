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
package com.nxp.timedoctor.core.model.lines;

import com.nxp.timedoctor.core.model.SampleCPU;
import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.Sample.SampleType;

/**
 * Sample line for memory cycles.
 */
public class MemCyclesSampleLine extends SampleLine {
	
	/**
	 * Constant for use in overflow calculations.
	 */
	private static final long OVERFLOW_CONSTANT = 0x100000000L;

	/**
	 * Constructs a sample line for mem cycles using the given cpu and id. Adds
	 * the line to the MemCycles section of the model.
	 * 
	 * @param cpu the cpu associated with the line
	 * @param id the integer id of the line
	 */
	public MemCyclesSampleLine(final SampleCPU cpu, final int id) {
		super(cpu, id);
		setType(LineType.MEM_CYCLES);
	}

	/**
	 * Implements inherited abstract method.
	 * @param endTime the time to which to perform calculations
	 */
	@Override
	public final void calculate(final double endTime) {
		if (getName() == null) {
			setName(String.format("MemCycles 0x%x", getID()));
		}
		double val = 0;
		double baseVal = 0;

		/*
		 * Correct for 32-bit overflows (is this necessary in Java?)
		 */
		for (int i = 0; i < getCount(); i++) {
			getSample(i).val += baseVal;
			if (getSample(i).val < val) {
				baseVal += (double) OVERFLOW_CONSTANT;
				getSample(i).val += (double) OVERFLOW_CONSTANT;
			}
			val = getSample(i).val;
		}
		if (getCount() > 0) {
			addSample(SampleType.END, endTime, getSample(getCount() - 1).val);
		} else {
			addSample(SampleType.END, endTime);
		}
	}

}
