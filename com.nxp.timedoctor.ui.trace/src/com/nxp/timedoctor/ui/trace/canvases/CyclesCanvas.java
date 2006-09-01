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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.ZoomModel;

/**
 * Canvas for graphical display of <code>SampleLine</code>s of type
 * <code>CYCLES</code>. Handles its own drawing through a
 * <code>CyclesPaintListener</code>.
 */
public class CyclesCanvas extends TraceCanvas {

	/**
	 * The initial height of the canvas. Will probably be removed, handled at a
	 * higher level.
	 */
	private static final int INITIAL_HEIGHT = 50;

	/**
	 * Constructs a new canvas for the display of cycles.
	 * 
	 * @param parent
	 *            the parent composite in which to create this canvas
	 * @param line
	 *            the line of type <code>CYCLES</code> containing the data to
	 *            be displayed
	 * @param data
	 *            model part containing zoom/scroll data
	 */
	public CyclesCanvas(final Composite parent, final SampleLine line,
			final ZoomModel data) {
		super(parent, data);

		setSize(computeSize(SWT.DEFAULT, INITIAL_HEIGHT));

		addPaintListener(new CyclesPaintListener(getDisplay().getSystemColor(
				SWT.COLOR_CYAN), getDisplay().getSystemColor(
				SWT.COLOR_DARK_CYAN), line, data));
	}

}
