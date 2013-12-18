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
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;

import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.ZoomModel;
import net.timedoctor.ui.trace.TraceCursorFactory.CursorType;

/**
 * Specific <code>MouseMoveListener</code> and <code>MouseTrackListener</code>.
 */
public class TraceCursorListener implements MouseMoveListener, MouseTrackListener, MouseListener{
	private TraceCursorFactory traceCursorFactory;
	private TimeLine traceCursor;
	private TimeLine baseLine;
	private ZoomModel timeModel;
	private int mouseButton = 0;
	private double scrollTimeIncrement = 0;
	private double traceEndTime;
	private Cursor scrollCursor = null;
	
	/**
	 * Constructor.
	 */
	public TraceCursorListener(final TraceCursorFactory traceCursorFactory,
			final TimeLine traceCursor,
			final TimeLine baseLine,
			final TraceModel traceModel,
			final ZoomModel timeModel) {
		this.traceCursorFactory = traceCursorFactory;
		this.traceCursor = traceCursor;
		this.baseLine = baseLine;
		this.traceEndTime = traceModel.getEndTime();
		this.timeModel = timeModel;
	}

	/**
	 * Set the mouse cursor to a 1-pixel wide vertical line an arrow at the
	 * location of the cursor.
	 * Scroll the trace view with the cursor if the right mouse button is pressed.
	 * 
	 * @param e
	 *            an event containing information about the mouse move
	 */
	public final void mouseMove(final MouseEvent e) {
		if (mouseButton == 3) {
			if (scrollCursor == null) {
				scrollCursor = new Cursor(e.display, SWT.CURSOR_HAND);
				e.widget.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						if (!scrollCursor.isDisposed()) {
							scrollCursor.dispose();
							scrollCursor = null;
						}
					}
				});
			}
			((Control) e.widget).setCursor(scrollCursor);
			
			scrollToCursor(e.x);
		}
		else {
			traceCursor.setCursor(e.x);
		}
	}

	public void mouseEnter(final MouseEvent e) {
	}

	public void mouseExit(final MouseEvent e) {
	}

	public void mouseHover(final MouseEvent e) {
	}

	public void mouseDoubleClick(final MouseEvent e) {
		if (e.button == 1) {
			TimeLine marker = traceCursorFactory.createTraceCursor(CursorType.MARKER);
			marker.setCursor(e.x);
			marker.setVisible(true);
		}
	}

	public void mouseDown(final MouseEvent e) {
		mouseButton = e.button;	// Store for later use in MouseMove
		if (mouseButton == 1) {
			baseLine.setCursor(e.x);
		
			// Baseline time is stored to be used in timeModel actions, etc.
			timeModel.setSelectTime(baseLine.getTime());
		}
		else if (mouseButton == 3) {
			traceCursor.setCursor(e.x);
		}
	}

	public void mouseUp(final MouseEvent e) {
		if (mouseButton == 3) {
			((Control)e.widget).setCursor(null);
		}
		mouseButton = 0;
		
	}

	/**
	 * Scroll the trace view with the cursor
	 * 
	 * @param x horizontal pixel position to scroll to
	 */
	private void scrollToCursor(final int x) {
		double startTime = timeModel.getStartTime();
		double endTime = timeModel.getEndTime();
		double cursorTime = traceCursor.getTime();
		double newCursorTime = traceCursor.getTime(x);
		
		// Only update scrollTimeIncrement if the cursor is within the trace window
		// Otherwise, use the previous scrollTimeIncrement to keep scrolling
		// whenever the user moves the mouse
		if ((newCursorTime >= startTime) && (newCursorTime <= endTime)) {
			scrollTimeIncrement = cursorTime - newCursorTime;
		}

		// Keep scrolling until you reach the beginning or end of the trace
		if (startTime + scrollTimeIncrement < 0) {
			scrollTimeIncrement = 0 - startTime;
		}
		else if (endTime + scrollTimeIncrement > traceEndTime) {
			scrollTimeIncrement = traceEndTime - endTime;
		}
		double newStartTime = startTime + scrollTimeIncrement;
		double newEndTime = endTime + scrollTimeIncrement;
		timeModel.setTimes(newStartTime, newEndTime);
	}
}
