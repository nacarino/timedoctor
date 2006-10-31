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
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.ui.trace.Colors;
import com.nxp.timedoctor.ui.trace.descriptions.ValueSampleInfo;

/**
 * Canvas to draw counters for numeric values.
 */
public class ValueCanvas extends TraceCanvas {

	/**
	 * Constructs a new ValueCanvas in the given composite.
	 * 
	 * @param parent 
     *          the parent composite
	 * @param line 
     *          <code>SampleLine</code> to be drawn
     * @param zoom
     *          <code>ZoomModel</code> containing zoom and scroll data
	 */
	public ValueCanvas(final Composite parent, 
			final SampleLine line, 
			final ZoomModel zoom) {
		super(parent, zoom, new ValueSampleInfo(line, zoom));
		
		addPaintListener(new CounterPaintListener(new Color(getDisplay(),
				Colors.DARK_CYAN), new Color(getDisplay(), Colors.LIGHT_CYAN),
				line, zoom));
	}
}
