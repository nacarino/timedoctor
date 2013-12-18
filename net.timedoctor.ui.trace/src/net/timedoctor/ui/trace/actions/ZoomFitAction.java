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

/**
 * This class fits the trace models window with in the trace editor.
 */
public class ZoomFitAction extends TraceAction {
	public static final String ID = "net.timedoctor.ui.actions.ZoomFit";

	/**
	 * Constructor
	 * @param label
	 * 			   Name of the action
	 */
	public ZoomFitAction(final String label) {
		super(label);
	}
		
	@Override
	public void run() {
		double oldStart = zoomModel.getStartTime();
		double oldEnd = zoomModel.getEndTime();
		zoomModel.pushZoom(oldStart, oldEnd);
		double endTime = traceModel.getEndTime();
		zoomModel.setTimes(0, endTime);
	}
}
