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

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.ui.trace.Colors;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**
 * Canvas for drawing ISRs.
 */
public class IsrCanvas extends TraceCanvas {

	/**
	 * Creates a new ISR canvas with the proper paint listener.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param line
	 *            <code>SampleLine</code> to be drawn
	 * @param data
	 *            <code>ZoomModel</code> containing all zoom and scroll
	 *            information
	 * @param model
	 *            model containing information on the full trace
	 */
	public IsrCanvas(final Composite parent, final SampleLine line,
			final ZoomModel data, final TraceModel model) {
		super(parent, data);

		addPaintListener(new TaskPaintListener(new Color(getDisplay(),
				Colors.DARK_GREEN), line, data, model));
	}

}
