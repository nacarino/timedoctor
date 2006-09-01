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
	 *            the line to display
	 * @param data
	 *            contains all zoom and scroll data
	 */
	public SemaphoreCanvas(Composite parent, SampleLine line, ZoomModel data) {
		super(parent, data);
	}

}
