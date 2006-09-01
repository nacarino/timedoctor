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

// TODO handle enable/disable depending on zoom stack status
public class ZoomBackAction extends Action implements IWorkbenchAction {
	public static final String ID = "com.nxp.timedoctor.ui.actions.ZoomBack";

	private ZoomModel zoomData;
	
	public ZoomBackAction(final ZoomModel zoomData) {
		this.zoomData = zoomData;
	}

	public void run() {
		double[] zoom = zoomData.popZoom();
		double startTime = zoom[0];
		double endTime = zoom[1];
		zoomData.setTimes(startTime, endTime);
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
