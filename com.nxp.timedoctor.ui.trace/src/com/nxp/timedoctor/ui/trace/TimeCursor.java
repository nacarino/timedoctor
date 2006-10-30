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
package com.nxp.timedoctor.ui.trace;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.Times;
import com.nxp.timedoctor.core.model.ZoomModel;

/**
 * Draw a marker line and time label 
 * as composites above all other
 * widgets. 
 */
public class TimeCursor extends TimeLine {
	private static final int OFFSET = 0;
	
	public TimeCursor(final Composite rulerPane, 
			final Composite tracePane, 
			final ZoomModel zoom) {
		super(rulerPane, tracePane, zoom, SWT.COLOR_RED, OFFSET);
	}
	
	protected void setTimeLabel(final double time) {
		double accuracy = zoom.getTimeDisplayAccuracy();
		String timeString = Times.timeToString(time, accuracy);
		
		// If zoom select time (from baseline) is set,
		// show current time plus difference from baseline
		double selectTime = zoom.getSelectTime();
		double delta = time - selectTime;
		if (selectTime >= 0 && delta != 0) {
			timeString += " (" + ((delta > 0) ? "+" : "-") + Times.timeToString(Math.abs(delta),Math.abs(delta)) + ")";
		}
		cursorLabel.setText(timeString);
		cursorLabel.setToolTipText(timeString);

		// Update label width with the new text
		// Must be called after layout
		cursorLabel.pack(false);
	}
}
