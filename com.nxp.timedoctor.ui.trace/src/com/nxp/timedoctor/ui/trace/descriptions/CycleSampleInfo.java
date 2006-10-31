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
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.model.lines.CyclesSampleLine;
import com.nxp.timedoctor.core.model.lines.MemCyclesSampleLine;

public class CycleSampleInfo extends SampleInfo {
	private SampleLine line;
	private ZoomModel zoom;
	
	public CycleSampleInfo(SampleLine line, ZoomModel zoom) {
		super(line);
		this.zoom = zoom;
		this.line = line;	
	}
	
	public String getInfoStr(int index) {
		double startTime = line.getSample(index).time;
		double endTime = line.getSample(index + 1).time;
		double timeInterval = endTime - startTime; // based on sample times
		// value stored is a cycle count
		double valueDifference = line.getSample(index + 1).val
				- line.getSample(index).val;

		// Average values
		double[] result = line.getCounterDifference(zoom.getStartTime(), 
				zoom.getEndTime());
		double overallTimeInterval = result[0];
		double avgValueDifference = result[1];
		
		String text = timeBoundsToStr(startTime, endTime);
		text += " ( " + timeIntervalToStr(startTime, endTime) + ")\n";
		text += doubleToIntStr(valueDifference) + " cycles";
		text += " / ";
		text = cyclesToPercentageStr(valueDifference, timeInterval);
		text += " / ";
		text += cyclesToFrequencyStr(valueDifference, timeInterval);
		text += "\nAvg: " + doubleToIntStr(avgValueDifference) + " cycles";
		text += " / ";
		text += cyclesToPercentageStr(avgValueDifference, overallTimeInterval);
		text += " / ";
		text += cyclesToFrequencyStr(avgValueDifference, overallTimeInterval);
				
		String description = line.descrString(startTime);
		if (description != null) {
			text += description;
		}
		
		return text;
	}
}
