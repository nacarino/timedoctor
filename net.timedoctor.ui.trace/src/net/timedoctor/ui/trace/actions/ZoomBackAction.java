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

/**
 * This class switch backs to the previous state of the zoom action.
 */
public class ZoomBackAction extends TraceAction {
	public static final String ID = "net.timedoctor.ui.actions.ZoomBack";

	/**
	 * Constructor
	 * @param label
	 * 			   Name of the action
	 */
	public ZoomBackAction(final String label) {
		super(label);
	}
	
	@Override
	public void run() {
		double[] zoom = zoomModel.popZoom();
		
		if ( zoom == null ) {
			//No zoom in/out performed
			return;
		}
		
		double startTime = zoom[0];
		double endTime = zoom[1];
		zoomModel.setTimes(startTime, endTime);
	}
}
