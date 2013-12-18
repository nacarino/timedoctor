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
package net.timedoctor.ui.trace.actions;

import net.timedoctor.core.model.SampleLine;

/**
 * This class performs displacement of the baseLine to the previous event in the
 * selected trace line
 * 
 */
public class PreviousAction extends TraceAction {
	public static final String ID = "net.timedoctor.ui.actions.Previous";

	/**
	 * Constructor 
	 * @param label
	 *            Name of the action
	 */
	public PreviousAction(final String label) {
		super(label);
	}

	@Override
	public void run() {
		final double baselineTime = zoomModel.getSelectTime();
		final SampleLine selectedLine = zoomModel.getSelectedLine();
		
		if (selectedLine != null) {
			int index = selectedLine.binarySearch(baselineTime);

			if ((selectedLine.getSample(index).time == baselineTime)
					&& (index > 0)) {
				index--;
			} else {
				while ((index > 0)
						&& (selectedLine.getSample(index).time > baselineTime)) {
					index--;
				}
			}

			double prevSampleTime = selectedLine.getSample(index).time;
			gotoTime(prevSampleTime);
		}
	}
}
