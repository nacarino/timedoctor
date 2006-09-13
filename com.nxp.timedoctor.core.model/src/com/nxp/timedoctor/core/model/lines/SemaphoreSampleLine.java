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
 * Class to represent sample lines of semaphores. Inherits almost all of its
 * functionality directly from <code>SampleLine</code>.
 */
public class SemaphoreSampleLine extends SampleLine {

	/**
	 * Constructs a sample line of type semaphore using the given cpu and id.
	 * 
	 * @param cpu the cpu associated with the line
	 * @param id the integer id of the line
	 */
	public SemaphoreSampleLine(final SampleCPU cpu, final int id) {
		super(cpu, id);
		setType(LineType.SEMAPHORES);
	}

	/**
	 * Implements abstract superclass method.
	 * @param endTime the time at which to end calculations.
	 */
	@Override
	public final void calculate(final double endTime) {
		if (getName() == null) {
			setName(String.format("Semaphore 0x%x", getID()));
		}
		/*
		 * Value on start/stop is number of open semaphores
		 */
		for (int i = 0, n = 0; i < getCount(); i++) {
			if (getSample(i).type == SampleType.START) {
				n++;
				getSample(i).val = n;
			} else if (getSample(i).type == SampleType.STOP) {
				n = Math.max(0, n - 1);
				getSample(i).val = n;
			}
		}
		if (getCount() > 0) {
			addSample(SampleType.END, endTime, getSample(getCount() - 1).val);
		} else {
			addSample(SampleType.END, endTime);
		}
	}

}
