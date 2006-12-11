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
package com.nxp.timedoctor.ui.trace;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.ui.trace.canvases.TraceCanvas;

/**
 * This class implements <code> MouseListener </code>. It will zoom the area
 * specified by the left mouse click and drag event.
 */
public class TraceZoomListener implements MouseListener {
	private double drawStartTime = 0.0;

	private double drawEndTime = 0.0;

	private ZoomModel zoom;

	private boolean mouseDownCalled = false;

	/**
	 * Constructor for TraceZoomListener class
	 * 
	 * @param zoomData
	 *            the observable model part containing zoom/scroll data
	 */
	public TraceZoomListener(final ZoomModel zoomData) {
		this.zoom = zoomData;
	}

	public void mouseDoubleClick(final MouseEvent e) {
	}

	/**
	 * Records the mouse click position which will be used for zoom operation.
	 * 
	 * @param e
	 *            an event containing information about the mouse position.
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDown(final MouseEvent e) {
		drawStartTime = zoom.getTimeAtPosition(e.x, getWidth(e));

		mouseDownCalled = true;
	}

	/**
	 * Records the mouse release position and performs the zoom opeartion.
	 * 
	 * @param e
	 *            an event containing information about the mouse position.
	 * 
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseUp(final MouseEvent e) {
		if (!mouseDownCalled) {
			// A boolean-guard against mouseUp events that are not associated
			// with selection-zoom
			return;
		}

		drawEndTime = zoom.getTimeAtPosition(e.x, getWidth(e));

		if (drawStartTime > drawEndTime) {
			double temp = drawStartTime;
			drawStartTime = drawEndTime;
			drawEndTime = temp;
		}

		if ((drawStartTime > zoom.getStartTime())
				&& (drawEndTime > zoom.getStartTime())
				&& (drawStartTime != drawEndTime)) {
			zoom.pushZoom(drawStartTime, drawEndTime);
			zoom.setTimes(drawStartTime, drawEndTime);
		}

		mouseDownCalled = false; // Reset flag
	}

	private int getWidth(final MouseEvent e) {
		final Canvas currentCanvas = (TraceCanvas) e.widget;
		final Composite parent1 = currentCanvas.getParent();
		final Composite parent2 = parent1.getParent();
		final Composite parent3 = parent2.getParent(); // ScrolledComposite
		return parent3.getBounds().width;
	}
}
