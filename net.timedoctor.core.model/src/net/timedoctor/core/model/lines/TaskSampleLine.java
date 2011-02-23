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
import net.timedoctor.core.model.Description.DescrType;
import net.timedoctor.core.model.Sample.SampleType;

/**
 * Sample line for tasks. Overrides much of the SampleLine implementation for
 * type-specific functionality.
 */
public class TaskSampleLine extends SampleLine {

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
	 * Constructs a task sample line using the given cpu and id, and adds it to
	 * the tasks section of the model (creating it if it does not exist).
	 * 
	 * @param cpu
	 *            the cpu associated with the line
	 * @param id
	 *            the integer id of the line.
	 */
	public TaskSampleLine(final SampleCPU cpu, final int id) {
		super(cpu, id);
		setType(LineType.TASKS);
	}

	/**
	 * Implements abstract superclass method addSample with type-specific
	 * behavior. Adds the sample described by the type, time, and value provided
	 * to the samples array.
	 * 
	 * @param type
	 *            the type of sample
	 * @param time
	 *            the time at which it occurred
	 * @param val
	 *            the value associated with the sample
	 */
	@Override
	public final void addSample(final SampleType type, final double time,
			final double val) {
		if (getCount() == 0 && type == SampleType.STOP) {
			return;
		}
		switch (type) {
		case START:
			if (startCount > 0) {
				return;
			}
			addOneSample(type, time, val);
			startCount = 1;
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
			startCount = 0;
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

	// MR explain what it does!
	/**
	 * Implements the abstract calculate function from superclass.
	 * 
	 * @param endTime
	 *            the time at which to end calculation
	 */
	@Override
	public final void calculate(final double endTime) {
		int[] clr = new int[getCount() + 1];
		int[] st = new int[getCount() + 1];
		// MR extract (generic) method
		if (getName() == null) {
			setName(String.format("Task 0x%x", getID()));
		}
		
		// MR add comments, extract sub methods
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
					getSample(i).val = clr[n];
					if (getSample(i).type == SampleType.STOP) {
						setMaxSampleDuration(Math.max(getMaxSampleDuration(),
								getSample(i).time - getSample(j).time));
					}
				}
			}
		}
		
		// MR extract method
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
	 * Determines whether or not the line has samples within the given time
	 * window.
	 * 
	 * @param startTime
	 *            The start time of the TraceView
	 * @param endTime
	 *            The end time of the TraceView
	 * @return boolean returns a boolean value indicating whether a sample is
	 *         present or not.
	 */
	@Override
	public final boolean hasSamples(final double startTime, final double endTime) {
		if (getCount() <= 1) {
			// no samples are present in the sample line.
			return false;
		}
		final int startIndex = binarySearch(startTime);
		final int endIndex = binarySearch(endTime);

		if (startIndex != endIndex) {
			return true;
		} else {
			// startIndex is same as endIndex
			if (getSample(startIndex).type == SampleType.STOP
					|| getSample(startIndex).type == SampleType.END) {
				// No samples in between and the last sample before startTime is
				// a stop
				return false;
			}

			double startIndexTime = getSample(startIndex).time;
			double nextIndexTime;
			try {
				nextIndexTime = getSample(startIndex + 1).time;
			} catch (IndexOutOfBoundsException e) {
				return false;
			}
			if (startTime < nextIndexTime && endTime > startIndexTime) {
				// The specified times overlap with the sample times
				return true;
			}
		}
		return false;
	}
}
