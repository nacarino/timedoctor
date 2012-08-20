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
 * This class performs zoomIn operation
 */
public class ZoomInAction extends TraceAction {
	public final static String ID = "net.timedoctor.ui.actions.ZoomIn";
    
	/**
	 * Constructor
	 * @param label
	 * 			   Name of the action
	 */
	public ZoomInAction(final String label) {
		super(label);
	}
		
 	@Override
	public void run() {
		double startTime = zoomModel.getStartTime();
		double endTime = zoomModel.getEndTime();
		double interval = endTime - startTime;
		zoomModel.pushZoom(startTime, endTime);
		double newInterval = interval / 2;
		double change = (interval - newInterval) / 2;
		startTime += change;
		endTime -= change;
		
		final double time = zoomModel.getSelectTime();
		
		if (time > 0) {
			startTime = Math.max(0, time - change);
			endTime = startTime + newInterval;
			zoomModel.setTimes(startTime, endTime);
		} else {
			zoomModel.setTimes(startTime, endTime);
		}
	}
}
