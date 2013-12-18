/*******************************************************************************
 * Copyright (c) 2006-2013 TimeDoctor contributors.
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
import net.timedoctor.core.model.ZoomModel;
import net.timedoctor.core.model.Sample.SampleType;

public class QueueSampleInfo extends AbstractSampleInfo {
	private SampleLine line;
	
	public QueueSampleInfo(final SampleLine line, final ZoomModel zoom) {
		super(line, zoom);
		this.line = line;	
	}
	
	@Override
	protected void fillInfoString(StringBuilder sb, int index) {
		final int LOW_MASK = 0x00000000ffffffff;
		SampleType type = line.getSample(index).type;
		long value = (long) line.getSample(index).val;
		int size = (int) (value >> 32);
		int ii = (int) (value & LOW_MASK);

		double startTime = line.getSample(index).time;
		double endTime = line.getSample(ii).time;

		sb.append("Size = " + size);
		sb.append(((type == SampleType.START) ? "\nSend @ " : "\nReceive @ "));
		sb.append(timeToStr(startTime));
		sb.append(((type == SampleType.START) ? "\nReceived @ " : "\nSent @ "));
		sb.append(timeToStr(endTime));
		sb.append("\nDelay = " + timeIntervalToStr(startTime, endTime));

		String description = line.descrString(startTime);
		if (description != null) {
			sb.append(description);
		}
	}
}
