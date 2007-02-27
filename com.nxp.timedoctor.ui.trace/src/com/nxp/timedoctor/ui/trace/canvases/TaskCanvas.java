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
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.ui.trace.Colors;
import com.nxp.timedoctor.ui.trace.descriptions.TaskSampleInfo;

/**
 * Canvas to draw tasks.
 */
public class TaskCanvas extends TraceCanvas {

	/**
	 * Constructs a new TaskCanvas in the given composite. TaskCanvases
	 * automatically use style <code>SWT.DOUBLE_BUFFERED</code> to smooth
	 * redrawing.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param line
	 *            the line of samples to draw
	 * @param zoom
	 *            contains zoom and scroll data
	 * @param model
	 *            contains all trace data
	 */
	public TaskCanvas(final Composite parent, 
			final SampleLine line,
			final ZoomModel zoom, 
			final TraceModel model) {
		super(parent, zoom, new TaskSampleInfo(line));
		
		addPaintListener(new TaskPaintListener(Colors.getColorRegistry().get(Colors.DARK_BLUE), line, zoom, model));
	}
}
