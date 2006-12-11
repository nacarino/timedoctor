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

public class NoteSampleInfo extends SampleInfo {
	private SampleLine line;
	
	public NoteSampleInfo(final SampleLine line) {
		super(line);
		this.line = line;	
	}
	
	@Override
	public String getInfoStr(final int index) {
		double startTime = line.getSample(index).time;
		
		String text = "Note @ ";
		text += Times.timeToString(startTime, ACCURACY);
		
		String description = line.descrString(startTime);
		if (description != null) {
			text += description;
		}

		return text;
	}
}
