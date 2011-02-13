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
import com.nxp.timedoctor.core.model.Sample.SampleType;

public class EventSampleInfo extends AbstractSampleInfo {
	private SampleLine line;
	
	public EventSampleInfo(final SampleLine line, final ZoomModel zoom) {
		super(line, zoom);
		this.line = line;	
	}
	
	@Override
	protected void fillInfoString(StringBuilder sb, int index) {
		double startTime = line.getSample(index).time;
		double value = line.getSample(index).val;
		
		sb.append(line.getSample(index).type == SampleType.START ? "Send @ "
				: "Receive @ ");
		sb.append(timeToStr(startTime));
		sb.append("\nSeq. Number = ");
		sb.append(doubleToIntStr(value));
		
		String description = line.descrString(startTime);
		if (description != null) {
			sb.append(description);
		}
	}
}
