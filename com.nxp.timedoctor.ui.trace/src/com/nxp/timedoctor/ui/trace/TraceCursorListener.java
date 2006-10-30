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
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;

import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.ui.trace.TraceCursorFactory.CursorType;

/**
 * Specific <code>MouseMoveListener</code> and <code>MouseTrackListener</code>.
 */
public class TraceCursorListener implements MouseMoveListener, MouseTrackListener, MouseListener{
	private TraceCursorFactory traceCursorFactory;
	private TimeLine traceCursor;
	private TimeLine baseLine;
	private ZoomModel zoom;
	/**
	 * Constructor.
	 */
	public TraceCursorListener(TraceCursorFactory traceCursorFactory,
			TimeLine traceCursor,
			TimeLine baseLine,
			ZoomModel zoom) {
		this.traceCursorFactory = traceCursorFactory;
		this.traceCursor = traceCursor;
		this.baseLine = baseLine;
		this.zoom = zoom;
	}

	/**
	 * Set the mouse cursor to a 1-pixel wide vertical line an arrow at the
	 * location of the cursor.
	 * 
	 * @param e
	 *            an event containing information about the mouse move
	 */
	public final void mouseMove(final MouseEvent e) {
		traceCursor.setCursor(e.x);		
	}

	public void mouseEnter(MouseEvent e) {
		traceCursor.setVisible(true);
	}

	public void mouseExit(MouseEvent e) {
		traceCursor.setVisible(false);		
	}

	public void mouseHover(MouseEvent e) {
	}

	public void mouseDoubleClick(MouseEvent e) {
		TimeLine marker = traceCursorFactory.createTraceCursor(CursorType.MARKER);
		marker.setCursor(e.x);
		marker.setVisible(true);
	}

	public void mouseDown(MouseEvent e) {
		baseLine.setCursor(e.x);
		baseLine.setVisible(true);
		
		// Baseline time is stored to be used in zoom actions, etc.
		zoom.setSelectTime(baseLine.getTime());
	}

	public void mouseUp(MouseEvent e) {
	}
}
