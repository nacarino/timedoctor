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
 * Skeleton file for a canvas to draw lines of type <code>MEM_CYCLES</code>.
 * Should instantiate and add a CyclesPaintListener with the correct color.
 */
public class MemCyclesCanvas extends TraceCanvas {

	/**
	 * Creates a new canvas to draw lines of type <code>MEM_CYCLES</code>.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param line
	 *            <code>SampleLine</code> to draw
	 * @param data
	 *            <code>ZoomModel</code> containing all zoom and scroll data
	 */
	public MemCyclesCanvas(final Composite parent, final SampleLine line,
			final ZoomModel data) {
		super(parent, data);
	}

}
