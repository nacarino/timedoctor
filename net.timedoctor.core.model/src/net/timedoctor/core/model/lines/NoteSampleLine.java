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
package net.timedoctor.core.model.lines;

import net.timedoctor.core.model.SampleCPU;
import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.Sample.SampleType;

/**
 * Sample line for notes.
 */
public class NoteSampleLine extends SampleLine {

	/**
	 * Constructs a note sample line using the given cpu and integer id. Adds it
	 * to the right section of the model.
	 * 
	 * @param cpu
	 *            the cpu associated with the line
	 * @param id
	 *            the integer id of the line
	 */
	public NoteSampleLine(final SampleCPU cpu, final int id) {
		super(cpu, id);
		setType(LineType.NOTES);
	}

	/**
	 * Implements inherited abstract method.
	 * 
	 * @param endTime
	 *            the time to which to perform calculation
	 */
	@Override
	public final void calculate(final double endTime) {
		if (getName() == null) {
			setName(String.format("Notes 0x%x", getID()));
		}
		if (getCount() > 0) {
			addSample(SampleType.END, endTime, getSample(getCount() - 1).val);
		} else {
			addSample(SampleType.END, endTime);
		}
	}
}
