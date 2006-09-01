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
package com.nxp.timedoctor.ui.trace.canvases;

import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.ZoomModel;

/**
 * Skeleton file for canvas to handle lines of type <code>EVENT</code>.
 */
public class EventCanvas extends TraceCanvas {

	/**
	 * Creates a new EventCanvas.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param line
	 *            the line to draw
	 * @param data
	 *            the <code>ZoomModel</code> instance containing zoom and
	 *            scroll data
	 */
	public EventCanvas(final Composite parent, final SampleLine line,
			final ZoomModel data) {
		super(parent, data);
	}

}
