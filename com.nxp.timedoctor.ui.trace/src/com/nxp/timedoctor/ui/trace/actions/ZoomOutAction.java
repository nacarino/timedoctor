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

import com.nxp.timedoctor.core.model.ZoomModel;

//TODO handle enable/disable depending on zoom back/out/fit
public class ZoomOutAction extends Action implements IWorkbenchAction {
	public static final String ID = "com.nxp.timedoctor.ui.actions.ZoomOut";
	
	private ZoomModel zoomData;
	
	public ZoomOutAction(final ZoomModel zoomData) {
		this.zoomData = zoomData;
	}

	public void run() {
		double startTime = zoomData.getStartTime();
		double endTime = zoomData.getEndTime();
		zoomData.pushZoom(startTime, endTime);
		double interval = endTime - startTime;
		double newInterval = interval * 2;
		double change = (newInterval - interval) / 2;
		startTime -= change;
		endTime += change;
		if (startTime < 0) {
			endTime += -startTime;
			startTime = 0;
		}
		zoomData.setTimes(startTime, endTime);
	}

	public void dispose() {
		// TODO Auto-generated method stub
	}
}
