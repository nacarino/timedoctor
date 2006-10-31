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
import com.nxp.timedoctor.core.model.Sample.SampleType;

public class SemaphoreSampleInfo extends SampleInfo {
	private SampleLine line;
	
	public SemaphoreSampleInfo(SampleLine line) {
		super(line);
		this.line = line;	
	}
	
	public String getInfoStr(int index) {
		double startTime = line.getSample(index).time;
		double value = line.getSample(index).val;
		
		String text = line.getSample(index).type == SampleType.START ? "Acquire @ "
				: "Release @ ";
		text += Times.timeToString(startTime, ACCURACY);
		text += "\nCount = ";
		text += doubleToIntStr(value);
		
		String description = line.descrString(startTime);
		if (description != null) {
			text += description;
		}

		return text;
	}
}
