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

public class QueueSampleInfo extends SampleInfo {
	private SampleLine line;
	
	public QueueSampleInfo(final SampleLine line) {
		super(line);
		this.line = line;	
	}
	
	@Override
	public String getInfoStr(final int index) {
		final int LOW_MASK = 0x00000000ffffffff;
		SampleType type = line.getSample(index).type;
		long value = (long) line.getSample(index).val;
		int size = (int) (value >> 32);
		int ii = (int) (value & LOW_MASK);

		double startTime = line.getSample(index).time;
		double endTime = line.getSample(ii).time;

		String text = "Size = " + size;
		text += ((type == SampleType.START) ? "\nSend @ " : "\nReceive @ ");
		text += Times.timeToString(startTime, ACCURACY);
		text += ((type == SampleType.START) ? "\nReceived @ " : "\nSent @ ");
		text += Times.timeToString(endTime, ACCURACY);
		text += "\nDelay = " + timeIntervalToStr(startTime, endTime);

		String description = line.descrString(startTime);
		if (description != null) {
			text += description;
		}

		return text;
	}
}
