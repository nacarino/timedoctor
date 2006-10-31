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

public class TaskSampleInfo extends SampleInfo {
	private SampleLine line;
	
	public TaskSampleInfo(SampleLine line) {
		super(line);
		this.line = line;	
	}
	
	public String getInfoStr(int index) {
		double startTime = line.getSample(index).time;
		double endTime = line.getSample(index + 1).time;

		String text = timeBoundsToStr(startTime, endTime) + "\n";
		text += timeIntervalToStr(startTime, endTime);
		text += " / ";
		text += timeIntervalToCyclesStr(startTime, endTime);
		
		String description = line.descrString(startTime);
		if (description != null) {
			text += description;
		}
		
		return text;
	}
}
