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

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.ZoomModel;

/**
 * Listener to handle the selection of labels.
 */
public class TraceLineSelectListener implements MouseListener {

	private TraceLineViewer traceLineViewer;

	private SampleLine line = null;
	
	private ZoomModel zoom;
	
	public TraceLineSelectListener(final TraceLineViewer traceLineViewer, 
			final SampleLine line, 
			final ZoomModel zoom) {
		this.traceLineViewer = traceLineViewer;		
		this.line = line;
		this.zoom = zoom;
	}
	
	/**
	 * Label does nothing on double-click.
	 * 
	 * @param e
	 *            MouseEvent containing detailed information about the event
	 */
	public final void mouseDoubleClick(final MouseEvent e) {
	}

	/**
	 * Label does nothing on mouse up.
	 * 
	 * @param e
	 *            MouseEvent containing detailed information about the event
	 */
	public final void mouseUp(final MouseEvent e) {
	}
	
	/**
	 * Selects the label of a mouseDown event.
	 * 
	 * @param e
	 *            MouseEvent containing detailed information about the event
	 */
	public final void mouseDown(final MouseEvent e) {
		if (e.button == 1) {
			zoom.setSelectedLine(line); 
			traceLineViewer.selectLine(e.display);
		}
	}
}
