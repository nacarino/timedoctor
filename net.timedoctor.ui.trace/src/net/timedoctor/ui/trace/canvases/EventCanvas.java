/*******************************************************************************
 * Copyright (c) 2006-2013 TimeDoctor contributors.
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
import net.timedoctor.ui.trace.descriptions.EventSampleInfo;

/**
 * Canvas to handle lines of type <code>EVENT</code>.
 */
public class EventCanvas extends TraceCanvas {

	/**
	 * Creates a new EventCanvas.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param line
	 *            the line to draw
	 * @param zoom
	 *            the <code>ZoomModel</code> instance containing zoom and
	 *            scroll data
	 */
	public EventCanvas(final Composite parent, 
			final SampleLine line,
			final ZoomModel zoom) {
		super(parent, zoom, new EventSampleInfo(line, zoom));
		addPaintListener(new EventPaintListener(Colors.getColorRegistry().get(Colors.DARK_MAGENTA),
				Colors.getColorRegistry().get(Colors.LIGHT_PINK), line, zoom));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		//Do nothing
	}
}
