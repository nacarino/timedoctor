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
import net.timedoctor.ui.trace.TracePluginActivator;
import net.timedoctor.ui.trace.descriptions.TaskSampleInfo;

/**
 * Canvas to draw tasks.
 */
public class TaskCanvas extends TraceCanvas {

	private TaskPaintListener fTaskPaintListener;

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
		super(parent, zoom, new TaskSampleInfo(line, zoom));
		
		fTaskPaintListener = new TaskPaintListener(Colors.getColorRegistry().get(Colors.DARK_BLUE), line, zoom, model);
		updateSubPixelPreference();
		
		addPaintListener(fTaskPaintListener);
	}

	private void updateSubPixelPreference() {
		final boolean enable = preferenceStore.getBoolean(TracePluginActivator.SUB_PIXEL_LOAD);
		fTaskPaintListener.enableSubPixel(enable);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TracePluginActivator.SUB_PIXEL_LOAD)) {
			updateSubPixelPreference();
			redraw();
		}
	}
}
