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
 * This sample line type is never actually visualized. Exists for organizational
 * and functional reasons, to streamline the adding of samples to and
 * visualization of the associated queue.
 */
public class PortSampleLine extends SampleLine {

	/**
	 * The line producing the samples on the port.
	 */
	private SampleLine prodLine;

	/**
	 * The line consuming the samples from the port.
	 */
	private SampleLine consLine;

	/**
	 * The queue associated with this port. Can be the producing line, the
	 * consuming line, or null.
	 */
	private SampleLine channelLine;

	/**
	 * Constructs a port sample line with the given cpu, integer id, producing
	 * line, and consuming line. Adds itself to the ports section of the model.
	 * 
	 * @param cpu
	 *            the cpu associated with the port
	 * @param id
	 *            the integer id of the port
	 * @param prod
	 *            the line producing samples
	 * @param cons
	 *            the line consuming samples
	 */
	public PortSampleLine(final SampleCPU cpu, final int id,
			final SampleLine prod, final SampleLine cons) {
		super(cpu, id);
		setType(LineType.PORTS);
		prodLine = prod;
		consLine = cons;
		if (prodLine != null && prodLine.getType() == LineType.QUEUES) {
			channelLine = prodLine;
		} else if (consLine != null && consLine.getType() == LineType.QUEUES) {
			channelLine = consLine;
		} else {
			channelLine = null;
		}
	}

	/**
	 * Implements inherited abstract method.
	 * 
	 * @param endTime
	 *            the time to which to perform calculations
	 */
	@Override
	public final void calculate(final double endTime) {
		if (getName() == null) {
			setName(String.format("Port 0x%x", getID()));
		}
		if (getCount() > 0) {
			addSample(SampleType.END, endTime, getSample(getCount() - 1).val);
		} else {
			addSample(SampleType.END, endTime);
		}
	}

	/**
	 * Returns the port's channel line, or null if none exists.
	 * 
	 * @return the port's channel line
	 */
	public final SampleLine getChannelLine() {
		return channelLine;
	}

	/**
	 * Overrides parent method for type-specific functionality. If it has a
	 * channel line, inserts samples into that. If it doesn't, adds them to
	 * itself.
	 * 
	 * @param type
	 *            the type of sample
	 * @param time
	 *            the time at which it occurred
	 * @param size
	 *            the amount of data transferred (if applicable)
	 */
	@Override
	public final void addSample(final SampleType type, final double time,
			final double size) {
		if (channelLine != null) {
			channelLine.addSample(type, time, size);
		} else {
			super.addSample(type, time, size);
		}
	}

}
