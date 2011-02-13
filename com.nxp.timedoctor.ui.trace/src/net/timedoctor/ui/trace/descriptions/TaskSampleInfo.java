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
import net.timedoctor.core.model.ZoomModel;

public class TaskSampleInfo extends AbstractSampleInfo {
	private SampleLine line;
	
	public TaskSampleInfo(final SampleLine line, final ZoomModel zoom) {
		super(line, zoom);
		this.line = line;	
	}
	
	@Override
	protected void fillInfoString(StringBuilder sb, int index) {
		double startTime = line.getSample(index).time;
		double endTime = line.getSample(index + 1).time;

		sb.append(timeBoundsToStr(startTime, endTime) + "\n");
		sb.append(timeIntervalToStr(startTime, endTime));
		sb.append(" / ");
		sb.append(timeIntervalToCyclesStr(startTime, endTime));
		
		String description = line.descrString(startTime);
		description += line.descrString(endTime);
		if (description != null) {
			sb.append(description);
		}
	}
}
