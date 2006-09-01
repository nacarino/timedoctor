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
 * Skeleton file for canvas to draw sample lines of type <code>NOTE</code>.
 * Should instantiate the correct paint listener.
 */
public class NoteCanvas extends TraceCanvas {

	/**
	 * Creates a new NoteCanvas.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param line
	 *            <code>SampleLine</code> to be drawn
	 * @param data
	 *            <code>ZoomModel</code> containing zoom and scroll data
	 */
	public NoteCanvas(final Composite parent, final SampleLine line,
			final ZoomModel data) {
		super(parent, data);
	}

}
