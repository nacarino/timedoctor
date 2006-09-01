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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;

//TODO handle enable/disable depending on zoom out/in/back
public class ZoomFitAction extends Action implements IWorkbenchAction {
	public static final String ID = "com.nxp.timedoctor.ui.actions.ZoomFit";

	private TraceModel model;
	private ZoomModel zoomData;
		
	public ZoomFitAction(final TraceModel model,
			final ZoomModel zoomData) {
		this.model = model;
		this.zoomData = zoomData;
	}

	public void run() {
		double oldStart = zoomData.getStartTime();
		double oldEnd = zoomData.getEndTime();
		zoomData.pushZoom(oldStart, oldEnd);
		double endTime = model.getEndTime();
		zoomData.setTimes(0, endTime);
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
