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
package net.timedoctor.ui.trace.actions;

/**
 * This class performs the zoom out operation.
 */
public class ZoomOutAction extends TraceAction {
	public static final String ID = "net.timedoctor.ui.actions.ZoomOut";
	
	/**
	 * Constructor
	 * @param label
	 * 			   Name of the action
	 */
	public ZoomOutAction(final String label) {
		super(label);
	}
	
	@Override
	public void run() {
		double startTime = zoomModel.getStartTime();
		double endTime = zoomModel.getEndTime();
		zoomModel.pushZoom(startTime, endTime);
		double interval = endTime - startTime;
		double newInterval = interval * 2;
		double change = (newInterval - interval) / 2;
		startTime -= change;
		endTime += change;
		if (startTime < 0) {
			endTime += -startTime;
			startTime = 0;
		}
		zoomModel.setTimes(startTime, endTime);
	}
}
