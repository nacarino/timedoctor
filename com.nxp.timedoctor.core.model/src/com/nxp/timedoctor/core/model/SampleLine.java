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
package com.nxp.timedoctor.core.model;

import com.nxp.timedoctor.core.model.Description.DescrType;
import com.nxp.timedoctor.core.model.Sample.SampleType;
import com.nxp.timedoctor.core.model.lines.AgentSampleLine;
import com.nxp.timedoctor.core.model.lines.CyclesSampleLine;
import com.nxp.timedoctor.core.model.lines.EventSampleLine;
import com.nxp.timedoctor.core.model.lines.ISRSampleLine;
import com.nxp.timedoctor.core.model.lines.MemCyclesSampleLine;
import com.nxp.timedoctor.core.model.lines.NoteSampleLine;
import com.nxp.timedoctor.core.model.lines.PortSampleLine;
import com.nxp.timedoctor.core.model.lines.QueueSampleLine;
import com.nxp.timedoctor.core.model.lines.SemaphoreSampleLine;
import com.nxp.timedoctor.core.model.lines.TaskSampleLine;
import com.nxp.timedoctor.core.model.lines.ValueSampleLine;

/**
 * Abstract parent class for all sample lines. Contains the basic
 * implementations of most methods, which can be overridden by subclasses as
 * needed.
 */
public abstract class SampleLine {
	private static final int MAX_INIT = 5000;
	private static final int D_MAX_INIT = 100;

	/**
	 * Ordinals used to convert integers to LineTypes.
	 */
	private static final int QUEUE_ORDINAL = 3;
	private static final int EVENT_ORDINAL = 4;
	private static final int VALUE_ORDINAL = 5;
	private static final int CYCLES_ORDINAL = 6;
	private static final int NOTE_ORDINAL = 7;
	private static final int AGENT_ORDINAL = 8;
	private static final int MEM_CYCLES_ORDINAL = 9;
	/**
	 * Ordinal used to convert integers to LineTypes. This type is deprecated --
	 * use <code>QUEUE</code> instead.
	 */
	private static final int CHANNEL_ORDINAL = 10;
	private static final int PORT_INT_VALUE = 11;

	private static final int HASH_CONSTANT = 28;

	/**
	 * The minimum amount by which to increase the description 
	 * and sample arrays when they fill up.
	 */
	private static final int MIN_DESCR_INCREASE = 100;
	private static final int SAMPLE_ARRAY_INCREASE = 10000;

	private TraceModel model;
	private Section section = null;
	private SampleCPU cpu;
	private String name;
	private LineType type;
	private int id;

	private int sampleCount = 0;
	private int maxNrSamples = MAX_INIT;
	private Sample[] samples = new Sample[maxNrSamples];
	private double maxSampleValue = 0;
	private double maxSampleDuration;

	private int descCount = 0;
	private int maxNrDesc = D_MAX_INIT;
	private Description[] descriptions = new Description[maxNrDesc];

	private double timeCreate = 0;
	private double timeDelete = Double.MAX_VALUE;

	/**
	 * Enumerates the possible line types, and provides a static method to parse
	 * strings containing integers to the associated type (for use in file
	 * parsing).
	 */
	public enum LineType {
		TASK,
		ISR,
		SEMAPHORE,
		QUEUE,
		EVENT,
		VALUE,
		CYCLES,
		NOTE,
		AGENT,
		MEM_CYCLES,
		/**
		 * @deprecated use QUEUE instead
		 */
		CHANNEL,
		PORT;

		/**
		 * Parses strings containing integers to their associated line types.
		 * For use in file parsing.
		 * 
		 * @param type
		 *            TODO
		 * 
		 * @return the associated type, or null if none exists
		 */
		public static LineType parseType(final int type) {
			switch (type) {
			case 0:
				return TASK;
			case 1:
				return ISR;
			case 2:
				return SEMAPHORE;
			case QUEUE_ORDINAL:
				return QUEUE;
			case EVENT_ORDINAL:
				return EVENT;
			case VALUE_ORDINAL:
				return VALUE;
			case CYCLES_ORDINAL:
				return CYCLES;
			case NOTE_ORDINAL:
				return NOTE;
			case AGENT_ORDINAL:
				return AGENT;
			case MEM_CYCLES_ORDINAL:
				return MEM_CYCLES;
				// MR remove
			case CHANNEL_ORDINAL:
				return QUEUE;
			case PORT_INT_VALUE:
				return PORT;
			default:
				throw new IllegalArgumentException();
			}
		};
	}

	/**
	 * The constructor for the abstract class SampleLine, to be called by
	 * subclasses to perform type-independent initializations.
	 * 
	 * @param lineCpu
	 *            the cpu associated with the line
	 * @param lineId
	 *            the integer id of the line
	 */
	public SampleLine(final SampleCPU lineCpu, final int lineId) {
		this.model = lineCpu.getModel();
		this.cpu = lineCpu;
		this.id = lineId;
	}

	// MR maybe change into a separate factory class?
	/**
	 * Static factory method to return SampleLines of the appropriate type. If
	 * the type is invalid or no class exists, returns null.
	 * 
	 * @param cpu
	 *            the cpu with which the line is associated
	 * @param type
	 *            the type of line to be created
	 * @param id
	 *            the id of the line
	 * @param time
	 *            the creation time of the line
	 * @return the line created using the above parameters
	 */
	public static SampleLine createLine(final SampleCPU cpu,
			final LineType type, final int id, final double time) {
		SampleLine line = null;
		switch (type) {
		case AGENT:
			line = new AgentSampleLine(cpu, id);
			break;
		case CYCLES:
			line = new CyclesSampleLine(cpu, id);
			break;
		case EVENT:
			line = new EventSampleLine(cpu, id);
			break;
		case ISR:
			line = new ISRSampleLine(cpu, id);
			break;
		case MEM_CYCLES:
			line = new MemCyclesSampleLine(cpu, id);
			break;
		case NOTE:
			line = new NoteSampleLine(cpu, id);
			break;
		// MR remove
		case CHANNEL:
			line = new QueueSampleLine(cpu, id);
			break;
		case PORT: // ONLY TO BE USED if a port line is used in a command
			// without prior CRE command
			line = new PortSampleLine(cpu, id, null, null);
		case QUEUE:
			line = new QueueSampleLine(cpu, id);
			break;
		case SEMAPHORE:
			line = new SemaphoreSampleLine(cpu, id);
			break;
		case TASK:
			line = new TaskSampleLine(cpu, id);
			break;
		case VALUE:
			line = new ValueSampleLine(cpu, id);
			break;
		default:
			line = null;
			break;
		}

		if (line != null) {
			line.setTimeCreate(time);
			// MR ugly, this needs some major analysis and possibly redesign
			line.addToSection(type);
		}
		return line;
	}

	/**
	 * Sets the creation time of the task/queue/etc.
	 * 
	 * @param time
	 *            the creation time
	 */
	public final void setTimeCreate(final double time) {
		timeCreate = time;
	}

	/**
	 * Sets the deletion time of the task/queue/etc.
	 * 
	 * @param time
	 *            the deletion time
	 * @throws CheckedIllegalArgumentException
	 *             if the time is before or equal to the line's set create time
	 */
	public final void setTimeDelete(final double time)
			throws CheckedIllegalArgumentException {
		if (time > timeCreate) {
			timeDelete = time;
		} else {
			throw new CheckedIllegalArgumentException(
					"Illegal Argument: Delete time before create time");
		}
	}

	/**
	 * Returns a boolean value that is true if the time is a valid time (between
	 * <code>timeCreate</code> and <code>timeDelete</code>).
	 * 
	 * @param time
	 *            the time to be checked
	 * @return boolean value indicating validity
	 */
	public final boolean isValid(final double time) {
		boolean valid = (time >= timeCreate) && (time <= timeDelete);
		return valid;
	}

	/**
	 * Returns the type of the line.
	 * 
	 * @return the type of the line
	 */
	public final LineType getType() {
		return type;
	}

	/**
	 * Returns the id of the line.
	 * 
	 * @return the integer id of the line
	 */
	public final int getID() {
		return id;
	}

	/**
	 * Returns the cpu associated with this line.
	 * 
	 * @return the cpu associated with this line
	 */
	public final SampleCPU getCPU() {
		return cpu;
	}

	/**
	 * @return the name of the line
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name of the line.
	 * 
	 * @param lineName
	 *            the name to be set
	 */
	public final void setName(final String lineName) {
		this.name = lineName;
	}

	// MR add check on array bounds
	/**
	 * Return the sample at the given index.
	 * 
	 * @param i
	 *            the index of the desired sample
	 * @return the sample at that index
	 */
	public final Sample getSample(final int i) {
		return samples[i];
	}

	// MR improve name
	/**
	 * @return the count value for this line
	 */
	public final int getCount() {
		return sampleCount;
	}

	/**
	 * @return the max value for this line
	 */
	public final double getMaxSampleValue() {
		return maxSampleValue;
	}

	/**
	 * @return the max duration for this line
	 */
	public final double getMaxSampleDuration() {
		return maxSampleDuration;
	}

	/**
	 * Returns the time at which the first sample occurred, or
	 * <code>Double.MAX_VALUE</code> if no samples are present.
	 * 
	 * @return the start time for sampling
	 */
	public final double getStartTime() {
		if (sampleCount == 0) {
			return Double.MAX_VALUE;
			// MR remove
		} else if (samples[0].time == 0.0) { // this case seems unnecessary
			// to me
			return 0.0;
		} else {
			return samples[0].time;
		}
	}

	/**
	 * Returns the time at which the last sample occurred, or zero if no samples
	 * are present.
	 * 
	 * @return the end time for sampling
	 */
	public final double getEndTime() {
		if (sampleCount == 0) {
			return 0;
		} else {
			return samples[sampleCount - 1].time;
		}
	}

	/**
	 * Compensate for a time offset.
	 * 
	 * @param time
	 *            the offset
	 */
	public final void compensateStartTime(final double time) {
		int i;
		for (i = 0; i < sampleCount; i++) {
			samples[i].time -= time;
		}
		for (i = 0; i < descCount; i++) {
			Description desc = descriptions[i];
			desc.time -= time;
			if (desc.type == DescrType.CYCLES) {
				desc.value -= time;
			}
		}
		if (timeCreate > 0) {
			timeCreate -= time;
		}
		if (timeDelete < Double.MAX_VALUE) {
			timeDelete -= time;
		}
	}

	/**
	 * Allows the adding of a sample with no associated value. Calls the
	 * underlying addSample(type, time, value) method with an empty-value value.
	 * 
	 * @param sampleType
	 *            the SampleType of the sample
	 * @param time
	 *            the time at which it occurred
	 */
	public final void addSample(final SampleType sampleType, 
			final double time) {
		addSample(sampleType, time, -1d);
	}

	/**
	 * Adds a description to the descriptions array with a type, id, and text,
	 * but no value.
	 * 
	 * @param descType
	 *            the type of description
	 * @param descId
	 *            the integer id of the description
	 * @param txt
	 *            the text of the description
	 */
	public final void addDescription(final DescrType descType, final int descId,
			final String txt) {
		if (descType == DescrType.STRING) {
			addDescription(descType, descId, txt, 0);
		}
	}

	/**
	 * Adds a description to the descriptions array with a type, id, and value,
	 * but no text.
	 * 
	 * @param descType
	 *            the type of description
	 * @param descId
	 *            the id ofthe description
	 * @param value
	 *            the value associated with the description
	 */
	public final void addDescription(final DescrType descType, final int descId,
			final double value) {
		if (descType != DescrType.STRING) {
			addDescription(descType, descId, null, value);
		}
	}

	/**
	 * Adds a description to the descriptions array using the given parameters.
	 * 
	 * @param descType
	 *            the type of description
	 * @param descId
	 *            the id of the description
	 * @param txt
	 *            the text of the description (may be null)
	 * @param value
	 *            the value associated with the description (may be 0)
	 */
	public final void addDescription(final DescrType descType, final int descId,
			final String txt, final double value) {
		if (sampleCount == 0) {
			return;
		}
		// MR extract generic method to grow array
		if (descCount == maxNrDesc) {
			maxNrDesc += Math.max(maxNrDesc / 2, MIN_DESCR_INCREASE);
			
			Description[] tmp = new Description[maxNrDesc];
			System.arraycopy(descriptions, 0, tmp, 0, descCount);
			descriptions = tmp;
		}
		descriptions[descCount] = new Description(samples[sampleCount - 1].time, descId,
				descType, txt, value);
		descCount++;
	}

	/**
	 * Finds and formats the description string for a certain time. Returns null
	 * if the description is not of type <code>STRING</code>,
	 * <code>NUMBER</code>, or <code>cycles</code>.
	 * 
	 * @param time
	 *            the time at which to find descriptions
	 * @return a formatted string containing all the descriptions at that time
	 */
	public final String descrString(final double time) {
		/*
		 * Binary search for first description sample
		 */
		// MR extract generic method to do binary search on an array of time
		// elements
		int low = 0;
		int high = sampleCount;
		int pivot;
		while (low < (high - 1)) {
			pivot = (low + high) >> 1;
			if (samples[pivot].time < time) {
				low = pivot;
			} else if (samples[pivot].time >= time) {
				high = pivot;
			}
		}
		if (samples[low].time < time) {
			low++;
		}

		// MR move to description class
		/*
		 * Create description string
		 */
		String s = "";
		for (; low < descCount && descriptions[low].time == time; low++) {
			switch (descriptions[low].type) {
			case STRING:
				s += "\n" + model.findDescrName(descriptions[low].id) + "= "
						+ descriptions[low].text;
				break;
			case NUMBER:
				s += "\n" + model.findDescrName(descriptions[low].id) + "= "
						+ descriptions[low].value;
				break;
			case CYCLES:
				s += "\n" + model.findDescrName(descriptions[low].id) + "= "
						+ Times.timeToString(descriptions[low].value);
			default:
				s = null;
			}
		}
		return s;
	}

	/**
	 * Binary search for first sample in window, starting at time 0.
	 * 
	 * @param time
	 *            the end time of the window
	 * @return the index of the sample
	 */
	// MR extract generic method to do binary search on array of time samples
	public final int binarySearch(final double time) {
		int low = 0;
		int high = sampleCount;
		int pivot;
		while (low < (high - 1)) {
			pivot = (low + high) >> 1;
			if (samples[pivot].time < time) {
				low = pivot;
			} else if (samples[pivot].time >= time) {
				high = pivot;
			}
		}
		return low;
	}

	/**
	 * The initial C# code utilized the <code>out</code> modifier on the time
	 * and value parameters. Since this is Java, what was returned stored in
	 * <code>time</code> will be at index 0 of the return array, what was
	 * stored in value will be at index 1.
	 * 
	 * @param from
	 *            from
	 * @param to
	 *            to
	 * @param time
	 *            time
	 * @param value
	 *            value
	 * @return an array containing the modified values of time and value
	 */
	// MR add description of what the function does/is used for
	public final double[] getCounterDifference(final double from,
			final double to, final double time, final double value) {
		double[] result = new double[2];
		int ff = binarySearch(from);
		int tt = binarySearch(to);
		if (samples[tt].time <= to && tt < sampleCount) {
			tt++;
		}
		double newTime = samples[tt].time - samples[ff].time;
		double newVal = samples[tt].val - samples[ff].val;
		result[0] = newTime;
		result[1] = newVal;
		return result;
	}

	// MR remove, should be private for subclasses if (possible?)
	// MR change into getIndex() and getValue() functions
	/**
	 * Needs to be overridden by channel and queue lines.
	 * 
	 * @param value
	 *            the value to be converted
	 * @return the associated index
	 */
	// ignoring checkstyle request to make final, abstract, or empty--method
	// needs to be overridden
	public int sampleValToIndex(final double value) {
		return (int) value;
	}

	/**
	 * Adds samples to the sample array. Can be overridden by subclasses for
	 * type-specific behaviour.
	 * 
	 * @param sampleType
	 *            the type of sample to add
	 * @param time
	 *            the time at which the event took place
	 * @param value
	 *            the value associated with the sample
	 */
	// ignoring checkstyle request to make final, abstract, or empty--method
	// needs to be overridden
	public void addSample(final SampleType sampleType, final double time,
			final double value) {
		// MR why task specific code here? remove
		if (sampleType != SampleType.SUSPEND && sampleType != SampleType.RESUME) {
			addOneSample(sampleType, time, value);
		}
	}

	/**
	 * Determines whether or not the line has samples within the given time
	 * window.
	 * 
	 * @param from
	 *            the start time
	 * @param to
	 *            the end time
	 * @return boolean indicating whether or not there are samples
	 */
	// ignoring checkstyle request to make final, abstract, or empty--method
	// needs to be overridden
	public boolean hasSamples(final double from, final double to) {
		int i = binarySearch(from);
		int j = binarySearch(to);
		return (i != j);
	}

	/**
	 * Empty method to be overridden by subclasses.
	 * 
	 * @param endTime
	 *            the end time for the calculation
	 */
	// MR improve name
	public abstract void calculate(double endTime);

	/**
	 * Returns the string containing the name of the line. Overrides
	 * <code>toString()</code> in <code>java.lang.Object</code>.
	 * 
	 * @return the name of the line
	 */
	@Override
	public final String toString() {
		return name;
	}

	/**
	 * Overrides hashCode method in Object to supply a more useful hashCode for
	 * our application.
	 * 
	 * @return the hash code for the line, created using the cpu and line ids.
	 */
	@Override
	public final int hashCode() {
		int hash = (cpu.getID() << HASH_CONSTANT) | id;
		return hash;
	}

	/**
	 * Sets the section this line is a member of.
	 * 
	 * @param lineSection
	 *            the new section
	 */
	public final void setSection(final Section lineSection) {
		section = lineSection;
	}

	/**
	 * Sets the maximum value of the line.
	 * 
	 * @param value
	 *            the maximum value
	 */
	protected final void setMaxSampleValue(final double value) {
		maxSampleValue = value;
	}

	/**
	 * Sets the maximum duration for this line.
	 * 
	 * @param duration
	 *            the new maximum duration
	 */
	protected final void setMaxSampleDuration(final double duration) {
		maxSampleDuration = duration;
	}

	/**
	 * Sets the type of the line.
	 * 
	 * @param lineType
	 *            the type of the line
	 */
	protected final void setType(final LineType lineType) {
		this.type = lineType;
	}

	// MR move to the section class (addLine)
	// MR refactor in parser
	/**
	 * Adds the line to the proper section, creating the section if it doesn't
	 * exist.
	 * 
	 * @param lineType
	 *            the line type of the section to add the line to
	 */
	public final void addToSection(final LineType lineType) {
		Section s = model.getSections().getSection(type);
		if (s == null) {
			s = new Section(model, type);
			s.addLine(this);
			model.getSections().addSection(type, s);
		} else {
			s.addLine(this);
		}
		section = s;
	}

	/**
	 * Returns the current number of descriptions associated with the line.
	 * 
	 * @return the description count
	 */
	protected final int getDescCount() {
		return descCount;
	}

	/**
	 * Returns the description at index i in the array.
	 * 
	 * @param i
	 *            the index of the description
	 * @return the description at that index.
	 */
	protected final Description getDescription(final int i) {
		return descriptions[i];
	}

	/**
	 * Adds the sample with the specified characteristics to the samples array.
	 * 
	 * @param sampleType
	 *            the type of sample
	 * @param time
	 *            the time at which it occurred
	 * @param value
	 *            the value associated with the sample
	 */
	// ignoring checkstyle request to disallow overriding
	protected final void addOneSample(final SampleType sampleType,
			final double time, final double value) {
		if (sampleCount == maxNrSamples) {
			// MR extract to generic code to grow array
			maxNrSamples += SAMPLE_ARRAY_INCREASE;
			Sample[] tmp = new Sample[maxNrSamples];
			System.arraycopy(samples, 0, tmp, 0, sampleCount);
			samples = tmp;
		}
		samples[sampleCount] = new Sample(sampleType, time, value);
		if (sampleCount > 0) {
			maxSampleValue = Math.max(maxSampleValue, samples[sampleCount].val
					- samples[sampleCount - 1].val);
		}
		sampleCount++;
	}
}
