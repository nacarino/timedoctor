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
package net.timedoctor.ui.trace.descriptions;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.Times;
import net.timedoctor.core.model.ZoomModel;
import net.timedoctor.core.model.SampleLine.LineType;

public abstract class AbstractSampleInfo {
	private ZoomModel zoomModel;
	private double clockFrequency;
	
	protected AbstractSampleInfo(final SampleLine line, final ZoomModel zoomModel) {
		if (line.getType() == LineType.MEM_CYCLES) {
			this.clockFrequency = line.getCPU().getMemClocksPerSec();
		}
		else {
			this.clockFrequency = line.getCPU().getClocksPerSec();
		}
		this.zoomModel = zoomModel;
	}
	
	public final String getInfoStr(final int index) {
		StringBuilder sb = new StringBuilder();
		fillInfoString(sb, index);
		return sb.toString();
	}
	
	protected abstract void fillInfoString(StringBuilder sb, int index);

	protected String timeToStr(final double time) {
		return Times.timeToString(time, zoomModel.getTimeDisplayAccuracy());
	}
	
	protected String timeBoundsToStr(final double startTime, final double endTime) {
		String text = Times.timeToString(startTime, zoomModel.getTimeDisplayAccuracy());
		text += " - ";
		text += Times.timeToString(endTime, zoomModel.getTimeDisplayAccuracy());
		return text;
	}
	
	protected String timeIntervalToStr(final double startTime, final double endTime) {
		double timeInterval = endTime - startTime;
		return Times.timeToString(timeInterval, zoomModel.getTimeDisplayAccuracy());		
	}
	
	protected String timeIntervalToCyclesStr(final double startTime, final double endTime) {
		double cycles = (endTime - startTime) * clockFrequency;
		return doubleToIntStr(cycles) + " cycles";
	}	
	
	protected String cyclesToFrequencyStr(final double cycles, final double timeInterval) {
		double frequency = cycles /(1000000 * timeInterval);
		return doubleToIntStr(frequency) + " MCy/s";
	}
	
	protected String cyclesToPercentageStr(final double cycles, final double totalCycles) {
		double percentage = 100 * cycles / (totalCycles * clockFrequency);
		return String.format("%.3f", percentage) + "%";
	}
	
	protected String doubleToIntStr(final double val) {
		return String.valueOf((int) val);
	}
}
