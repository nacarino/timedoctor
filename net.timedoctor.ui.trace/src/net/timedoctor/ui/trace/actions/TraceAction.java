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
package net.timedoctor.ui.trace.actions;

import org.eclipse.jface.action.Action;

import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.ZoomModel;

public abstract class TraceAction extends Action {
	protected ZoomModel zoomModel;
	protected TraceModel traceModel;

	protected TraceAction(final String label) {
		setText(label);
	}
	
	protected void gotoTime(final double gotoTime) {
		double time = gotoTime;
		time = Math.min(time, traceModel.getEndTime());
		time = Math.max(0, time);
		
		double startTime = zoomModel.getStartTime();		
		double endTime = zoomModel.getEndTime();
		
		final double initialStartTime = startTime;
		final double initialEndTime = endTime;
		
		double timeDifference = endTime - startTime;
		
		if ((time > timeDifference + startTime) || (time < startTime)) {
			// Set scrollbar such that selected time is in the middle of the page
			startTime = Math.max(0, time - (timeDifference/2));
			endTime = startTime + timeDifference;
			
			zoomModel.pushZoom(initialStartTime, initialEndTime);
			zoomModel.setTimes(startTime, endTime);
		}		
		zoomModel.setSelectTime(time);
	}

	/**
	 * Holds the current active editors trace aan zoom models
	 * 
	 * @param traceModel
	 * 		The current editor's traceModel 
	 * @param zoomModel
	 * 		The current editor's zoomModel 
	 */
	public void updateModel(final TraceModel traceModel, final ZoomModel zoomModel) {
		this.traceModel = traceModel;
		this.zoomModel = zoomModel;
	}
}
