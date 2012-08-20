/*******************************************************************************
 * Copyright (c) 2006-2012 TimeDoctor contributors.
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

public class NoteSampleInfo extends AbstractSampleInfo {
	private SampleLine line;
	
	public NoteSampleInfo(final SampleLine line, final ZoomModel zoom) {
		super(line, zoom);
		this.line = line;	
	}
	
	@Override
	protected void fillInfoString(StringBuilder sb, int index) {
		double startTime = line.getSample(index).time;
		
		sb.append("Note @ ");
		sb.append(timeToStr(startTime));
		
		String description = line.descrString(startTime);
		if (description != null) {
			sb.append(description);
		}
	}
}
