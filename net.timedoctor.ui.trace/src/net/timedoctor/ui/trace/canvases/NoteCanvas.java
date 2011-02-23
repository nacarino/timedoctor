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
package net.timedoctor.ui.trace.canvases;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.ZoomModel;
import net.timedoctor.ui.trace.Colors;
import net.timedoctor.ui.trace.descriptions.NoteSampleInfo;

/**
 * Canvas to draw sample lines of type <code>NOTE</code>.
 */
public class NoteCanvas extends TraceCanvas {

	/**
	 * Creates a new NoteCanvas.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param line
	 *            <code>SampleLine</code> to be drawn
	 * @param zoom
	 *            <code>ZoomModel</code> containing zoom and scroll data
	 */
	public NoteCanvas(final Composite parent, 
			final SampleLine line,
			final ZoomModel zoom) {
		super(parent, zoom, new NoteSampleInfo(line, zoom));
		addPaintListener(new NotePaintListener(Colors.getColorRegistry().get(Colors.DARK_GOLDENROD), line, zoom));
		
	}

	public void propertyChange(PropertyChangeEvent event) {
		//Do nothing
	}
}
