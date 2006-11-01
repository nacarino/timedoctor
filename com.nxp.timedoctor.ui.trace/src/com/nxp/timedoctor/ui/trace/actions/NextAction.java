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
package com.nxp.timedoctor.ui.trace.actions;

import com.nxp.timedoctor.core.model.SampleLine;

/**
 * This class performs displacement of the baseLine to the next event in the
 * selected trace line
 * 
 */
public class NextAction extends TraceAction {
	public static final String ID = "com.nxp.timedoctor.ui.actions.Next";

	/**
	 * Constructor
	 * 
	 * @param label
	 *            Name of the action
	 */
	public NextAction(String label) {
		super(label);
	}

	public void run() {
		final double baselineTime = zoomModel.getSelectTime();
		final SampleLine selectedLine = zoomModel.getSelectedLine();

		if (selectedLine != null) {
			int index = selectedLine.binarySearch(baselineTime);

			if ((selectedLine.getSample(index).time == baselineTime)
					&& (index < selectedLine.getCount() - 2)) {
				index++;
			} else {
				while (index < selectedLine.getCount() - 2
						&& selectedLine.getSample(index).time < baselineTime) {
					index++;
				}
			}

			double nextSampleTime = selectedLine.getSample(index).time;
			gotoTime(nextSampleTime);
		}
	}
}
