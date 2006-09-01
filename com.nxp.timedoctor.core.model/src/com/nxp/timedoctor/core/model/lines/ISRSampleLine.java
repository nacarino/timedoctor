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
import com.nxp.timedoctor.core.model.Description.DescrType;
import com.nxp.timedoctor.core.model.Sample.SampleType;

/**
 * Sample line to represent ISRs.
 */
public class ISRSampleLine extends SampleLine {

	/**
	 * Variable for use in adding samples, to track the state of the sample
	 * array.
	 */
	private int startCount = 0;
	/**
	 * Number of times the line has been suspended since the last start/resume.
	 */
	private int suspendCount = 0;

	/**
	 * Constructs a sample line to represent an ISR with the given cpu and
	 * integer id, and adds the line to the ISR section of the model.
	 * 
	 * @param cpu
	 *            the cpu associated with the line
	 * @param id
	 *            the integer id of the line
	 */
	public ISRSampleLine(final SampleCPU cpu, final int id) {
		super(cpu, id);
		setType(LineType.ISR);
	}

	/**
	 * Overrides parent method for type-specific behavior.
	 * 
	 * @param type
	 *            the type of sample to add
	 * @param time
	 *            the time at which the sample occured
	 * @param value
	 *            does nothing in an ISR
	 */
	@Override
	public final void addSample(final SampleType type, final double time,
			final double val) {
		if (getCount() == 0 && type == SampleType.STOP) {
			return;
		} else {
			switch (type) {
			case START:
				if (suspendCount > 0) {
					addOneSample(SampleType.RESUME, time, val);
					suspendCount = 0;
				}
				addOneSample(type, time, val);
				startCount++;
				break;
			case STOP:
				if (startCount == 0) {
					return;
				}
				if (suspendCount > 0) {
					addOneSample(SampleType.RESUME, time, val);
					suspendCount = 0;
				}
				addOneSample(type, time, val);
				startCount--;
				break;
			case SUSPEND:
				if (startCount == 0) {
					return;
				}
				if (suspendCount == 0) {
					addOneSample(type, time, val);
				}
				suspendCount++;
				break;
			case RESUME:
				if (suspendCount == 0) {
					return;
				}
				suspendCount--;
				if (suspendCount == 0) {
					addOneSample(type, time, val);
				}
				break;
			default:
				if (suspendCount > 0) {
					addOneSample(SampleType.RESUME, time, val);
					suspendCount = 0;
				}
				addOneSample(type, time, val);
				break;
			}
		}
	}

	/**
	 * Implements abstract method from superclass.
	 * 
	 * @param endTime
	 *            the time to which to calculate sample data
	 */
	@Override
	public final void calculate(final double endTime) {
		int[] clr = new int[getCount() + 1];
		int[] st = new int[getCount() + 1];
		if (getName() == null) {
			setName(String.format("Isr 0x%x", getID()));
		}
		int n = 0;
		setMaxSampleDuration(0);
		for (int i = 0, ii = 0; i < getCount(); i++) {
			if (getSample(i).type == SampleType.START) {
				clr[n] = -1;
				for (; ii < getDescCount(); ii++) {
					if (getDescription(ii).time >= getSample(i).time) {
						break;
					}
				}
				for (; ii < getDescCount(); ii++) {
					if (getDescription(ii).time == getSample(i).time) {
						if (getDescription(ii).type == DescrType.COLOR) {
							clr[n] = (int) getDescription(ii).value;
						}
					} else {
						break;
					}
				}
				st[n++] = i;
			} else if (getSample(i).type == SampleType.SUSPEND) {
				if (n > 0) {
					clr[n] = clr[n - 1];
				} else {
					clr[n] = -1;
				}
				st[n++] = i;
			} else if (getSample(i).type == SampleType.STOP
					|| getSample(i).type == SampleType.RESUME) {
				if (n > 0) {
					n--;
					int j = st[n];
					getSample(j).val = i;
					getSample(i).val = (double) clr[n];
					if (getSample(i).type == SampleType.STOP) {
						setMaxSampleDuration(Math.max(getMaxSampleDuration(),
								getSample(i).time - getSample(j).time));
					}
				}
			}
		}
		/*
		 * Add samples at end time to conclude still open task
		 */
		for (n--; n >= 0; n--) {
			if (getSample(st[n]).type == SampleType.START) {
				addSample(SampleType.STOP, endTime);
			} else {
				while (suspendCount > 0) {
					addSample(SampleType.RESUME, endTime);
				}
			}
			getSample(st[n]).val = getCount() - 1;
		}
		if (getCount() > 0) {
			addSample(SampleType.END, endTime, getSample(getCount() - 1).val);
		} else {
			addSample(SampleType.END, endTime);
		}
	}

	/**
	 * Overrides superclass method for type-specific behavior.
	 * 
	 * @param from
	 *            the beginning time of the search window
	 * @param to
	 *            the end time of the search window
	 * @return whether or not the line has samples in the given window
	 */
	@Override
	public final boolean hasSamples(final double from, final double to) {
		int i = binarySearch(from);
		int j = binarySearch(to);
		if (i != j) {
			return true;
		} else if (getCount() <= 1) {
			return false;
		} else if (getSample(i).time > to || getSample(i).time < from) {
			return false;
		} else if (getSample(i).type == SampleType.STOP) {
			return false;
		} else {
			return true;
		}
	}

}
