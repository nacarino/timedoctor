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
package net.timedoctor.core.model.lines;

import net.timedoctor.core.model.SampleCPU;
import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.Sample.SampleType;

/**
 * Sample line for events, with type-specific behaviour.
 */
public class EventSampleLine extends SampleLine {

	/**
	 * Constructs a line with the given cpu and id, setting the type and adding
	 * it to the appropriate section.
	 * 
	 * @param cpu the cpu associated with the line
	 * @param id the integer id of the line
	 */
	public EventSampleLine(final SampleCPU cpu, final int id) {
		super(cpu, id);
		setType(LineType.EVENTS);
	}

	/**
	 * Implements abstract method from superclass.
	 * @param endTime the time to which to calculate sample data
	 */
	@Override
	public final void calculate(final double endTime) {
		if (getName() == null) {
			setName(String.format("Event 0x%x", getID()));
		}
		/*
		 * Value on start/stop is event sequence number.
		 */
		for (int i = 0, n = 0; i < getCount(); i++) {
			if (getSample(i).type == SampleType.START) {
				n++;
				getSample(i).val = n;
			} else if (getSample(i).type == SampleType.STOP) {
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
