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

import org.eclipse.swt.widgets.Composite;

import net.timedoctor.core.model.ZoomModel;

/**
 * Draw a marker line and time label 
 * as composites above all other
 * widgets. 
 */
public class TraceCursorFactory {
	private ZoomModel zoom;
	private Composite rulerPane;
	private Composite tracePane;

	public enum CursorType {
		CURSOR,
        BASELINE,
        MARKER
	};
	
	public TraceCursorFactory(final ZoomModel zoom) {
		this.zoom = zoom;
	}
	
	public void setRulerPane(final Composite rulerPane) {
		this.rulerPane = rulerPane;
	}
	
	public void setTracePane(final Composite tracePane) {
		this.tracePane = tracePane;
	}
	
	public TimeLine createTraceCursor(final CursorType type) {
		TimeLine timeLine = null;
		switch (type) {
		case CURSOR:
			timeLine = new TimeCursor(rulerPane, tracePane, zoom);
			break;
		case BASELINE:
			timeLine = new TimeBaseLine(rulerPane, tracePane, zoom);
			break;
		case MARKER:
			timeLine = new TimeMarker(rulerPane, tracePane, zoom);
			break;
		}		
		return timeLine;
	}
}