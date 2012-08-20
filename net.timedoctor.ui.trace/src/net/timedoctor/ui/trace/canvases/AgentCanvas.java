/*******************************************************************************
 * Copyright (c) 2006-2012 TimeDoctor contributors.
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
import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.ZoomModel;
import net.timedoctor.ui.trace.Colors;
import net.timedoctor.ui.trace.descriptions.TaskSampleInfo;

/**
 * Canvas to display SampleLines of type agent. Handles creating the correct
 * paint listener with the correct color.
 */
public class AgentCanvas extends TraceCanvas {
	
	/**
	 * Creates a new canvas for displaying lines of type <code>AGENT</code>.
	 * The canvas handles its own drawing through a
	 * <code>TaskPaintListener</code>.
	 * 
	 * @param parent
	 *            the parent composite in which to create this canvas.
	 * @param sampleLine
	 *            a line of type <code>AGENT</code> containing the data to be
	 *            displayed.
	 * @param zoom
	 *            the model component containing zoom and scroll data
	 * @param model
	 *            the model containing data on the full set of traces
	 */
	public AgentCanvas(final Composite parent, 
			final SampleLine line,
			final ZoomModel zoom, 
			final TraceModel model) {
		super(parent, zoom, new TaskSampleInfo(line, zoom));

		addPaintListener(new TaskPaintListener(Colors.getColorRegistry().get(Colors.SEA_GREEN), line, zoom, model));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		//do nothing
	}

}
