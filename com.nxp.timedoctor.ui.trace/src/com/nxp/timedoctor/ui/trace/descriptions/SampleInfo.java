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
package com.nxp.timedoctor.ui.trace.descriptions;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.Times;
import com.nxp.timedoctor.core.model.SampleLine.LineType;

public class SampleInfo {
	protected static final double ACCURACY = 0.001d;
	
	private double clockFrequency;
	
	protected SampleInfo(SampleLine line) {
		if (line.getType() == LineType.MEM_CYCLES) {
			this.clockFrequency = line.getCPU().getMemClocksPerSec();
		}
		else {
			this.clockFrequency = line.getCPU().getClocksPerSec();
		}
	}
	
	public String getInfoStr(int index) {
		return null;
	}
	
	protected String timeBoundsToStr(double startTime, double endTime) {
		String text = Times.timeToString(startTime, ACCURACY);
		text += " - ";
		text += Times.timeToString(endTime, ACCURACY);
		return text;
	}
	
	protected String timeIntervalToStr(double startTime, double endTime) {
		double timeInterval = endTime - startTime;
		return Times.timeToString(timeInterval, ACCURACY);		
	}
	
	protected String timeIntervalToCyclesStr(double startTime, double endTime) {
		double cycles = (endTime - startTime) * clockFrequency;
		String text = doubleToIntStr(cycles) + " cycles";
		return text;
	}	
	
	protected String cyclesToFrequencyStr(double cycles, double totalCycles) {
		double frequency = cycles /(1000000 * clockFrequency);
		return Times.timeToString(frequency) + " MCy/s";
	}
	
	protected String cyclesToPercentageStr(double cycles, double totalCycles) {
		double percentage = 100 * cycles / (totalCycles * clockFrequency);
		return String.format("%.3f", percentage) + "%";
	}
	
	protected String doubleToIntStr(final double val) {
		return String.valueOf((int) val);
	}
}
