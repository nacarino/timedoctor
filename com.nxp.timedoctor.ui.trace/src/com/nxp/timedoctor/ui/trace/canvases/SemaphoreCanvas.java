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
import com.nxp.timedoctor.ui.trace.Colors;
import com.nxp.timedoctor.ui.trace.descriptions.SemaphoreSampleInfo;

/**
 * Skeleton class for a canvas to display lines of type <code>SEMAPHORE</code>.
 */
public class SemaphoreCanvas extends TraceCanvas {

    /**
     * Creates a new semaphore canvas. Should instantiate and add the proper
     * paint listener to handle drawing.
     * 
     * @param parent
     *            the parent composite
     * @param line
     *            contains data about semaphore acquire and release timings
     * @param zoom
     *            contains all zoom and scroll data
     */
    public SemaphoreCanvas(final Composite parent, 
    		final SampleLine line,
            final ZoomModel zoom) {
        super(parent, zoom, new SemaphoreSampleInfo(line));
        
        addPaintListener(new EventPaintListener(Colors.getColorRegistry().get(Colors.DARK_VIOLET), 
        		Colors.getColorRegistry().get(Colors.THISTLE),
                line, zoom));
    }

}
