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
 * Sample line for queues.
 */
public class QueueSampleLine extends SampleLine {

	/**
	 * Static field to hold the constant for fifo calculations. Said constant is
	 * a magic number.
	 */
	private static final long FIFO_CONSTANT = 0x100000000L;

	/**
	 * QueueSampleLine constructor. Calls the parent constructor to set cpu and
	 * id values, then sets its type.
	 * 
	 * @param cpu
	 *            the cpu associated with the line
	 * @param id
	 *            the unique integer id of the queue
	 */
	public QueueSampleLine(final SampleCPU cpu, final int id) {
		super(cpu, id);
		setType(LineType.QUEUES);
	}

	/**
	 * Overrides method in super for type-specific behavior.
	 * 
	 * @param val
	 *            the value to be converted
	 * @return the index produced by the value
	 */
	@Override
	public final int sampleValToIndex(final double val) {
		long index = (long) val;
		index = index & 0x00000000ffffffff;
		return (int) index;
	}

	/**
	 * Implements abstract method from superclass. Comments copied from
	 * SampleLine.cs
	 * 
	 * @param endTime
	 *            the time to which to perform calculation
	 */
	@Override
	public final void calculate(final double endTime) {
		int[] st = new int[getCount() + 1];
		if (getName() == null) {
			setName(String.format("Queue 0x%x", getID()));
		}
		setMaxSampleDuration(0);
		/*
		 * Value on start/stop is number of matching queue commands and
		 * queue size after comment
		 */
		setMaxSampleValue(0);
		for (int i = 0, nr = 0, nw = 0, m = 0; i < getCount(); i++) {

			/*
			 * m = current fifo of the fifo. nr = number of reads so far. nw =
			 * number of writes so far. Fifo value m is coded. Value *
			 * 1,000,000,000 is the fifo filling. The lower bits are used to set
			 * the index of the sample with the write that corresponds to the
			 * current read.
			 */
			if (getSample(i).type == SampleType.START) {
				m += (int) getSample(i).val;
				setMaxSampleValue(Math.max(m, getMaxSampleValue()));
				st[nw] = i;
				nw = (nw + 1) % getCount();
				getSample(i).val = m * (double) FIFO_CONSTANT;
			} else if (getSample(i).type == SampleType.STOP) {
				m = Math.max(0, m - (int) getSample(i).val);
				if (nr == nw) {
					getSample(i).val = m * (double) FIFO_CONSTANT;
				} else {
					int j = st[nr]; // index of sample of write action that
					// corresponds to this read
					nr = (nr + 1) % getCount();
					getSample(i).val = m * (double) FIFO_CONSTANT + j;
					getSample(j).val += i;
				}
			}
		}
		if (getCount() > 0) {
			addSample(SampleType.END, endTime, getSample(getCount() - 1).val);
		} else {
			addSample(SampleType.END, endTime);
		}
	}

}
