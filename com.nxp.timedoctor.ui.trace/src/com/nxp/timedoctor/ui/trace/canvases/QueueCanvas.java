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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.ui.trace.Colors;

/**
 * Canvas that adds to itself a paint listener to correctly handle the drawing
 * of queues.
 */
public class QueueCanvas extends TraceCanvas {
	/**
	 * The line of trace samples.
	 */
	private final SampleLine line;

	/**
	 * Constructs a new QueueCanvas in the given composite. QueueCanvases
	 * automatically use style <code>SWT.DOUBLE_BUFFERED</code> to smooth
	 * redrawing.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param sampleLine
	 *            the line of samples to draw
	 * @param data
	 *            model part containing zoom and scroll data
	 * @param model
	 *            model containing all trace information
	 */
	public QueueCanvas(final Composite parent, final SampleLine sampleLine,
			final ZoomModel data, final TraceModel model) {
		super(parent, data);
		line = sampleLine;

		addPaintListener(new QueuePaintListener(new Color(getDisplay(),
				Colors.DARK_RED), line, data, model));
	}
}
