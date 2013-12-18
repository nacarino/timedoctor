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
package net.timedoctor.ui.trace;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.ZoomModel;
import net.timedoctor.ui.trace.canvases.TraceCanvas;

/**
 * This class implements <code> MouseListener </code>. It will zoom the area
 * specified by the left mouse click and drag event.
 */
public class TraceZoomListener implements MouseListener, MouseMoveListener {
	private double drawStartTime = 0.0;
	private double drawEndTime = 0.0;
	private ZoomModel zoom;
	private TraceModel traceModel;
	private int mouseButton = 0;
	private Cursor zoomCursor = null;

	/**
	 * Constructor for TraceZoomListener class
	 * 
	 * @param zoomData
	 *            the observable model part containing zoom/scroll data
	 * @param traceModel
	 * 			  the {@link TraceModel} object	
	 */
	public TraceZoomListener(final ZoomModel zoomData, final TraceModel traceModel) {
		this.zoom = zoomData;
		this.traceModel = traceModel;
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
		mouseButton = e.button;
		if (mouseButton == 1) {
			drawStartTime = zoom.getTimeAtPosition(e.x, getWidth(e));			
		}
	}

	public void mouseMove(final MouseEvent e) {
		if (mouseButton == 1) {
			if (zoomCursor == null) {
				zoomCursor = new Cursor(e.display, SWT.CURSOR_SIZEE);
				e.widget.addDisposeListener(new DisposeListener(){
					public void widgetDisposed(DisposeEvent e) {
						if (!zoomCursor.isDisposed()) {
							zoomCursor.dispose();
							zoomCursor = null;
						}
					}
				});
			}
			((Control) e.widget).setCursor(zoomCursor);
		}		
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
		if ((mouseButton == 1) && (zoom.getTimeDisplayAccuracy() > traceModel.getMinTimeResolution())) {
			drawEndTime = zoom.getTimeAtPosition(e.x, getWidth(e));

			if (drawStartTime > drawEndTime) {
				double temp = drawStartTime;
				drawStartTime = drawEndTime;
				drawEndTime = temp;
			}

			if ((drawStartTime >= zoom.getStartTime())
					&& (drawEndTime > zoom.getStartTime())
					&& (drawStartTime != drawEndTime)) {
				zoom.pushZoom(drawStartTime, drawEndTime);
				zoom.setTimes(drawStartTime, drawEndTime);
			}

			((Control) e.widget).setCursor(null);
		}
		mouseButton = 0;
	}

	private int getWidth(final MouseEvent e) {
		final Canvas currentCanvas = (TraceCanvas) e.widget;
		final Composite parent1 = currentCanvas.getParent();
		final Composite parent2 = parent1.getParent();
		final Composite parent3 = parent2.getParent(); // ScrolledComposite
		return parent3.getBounds().width;
	}
}
