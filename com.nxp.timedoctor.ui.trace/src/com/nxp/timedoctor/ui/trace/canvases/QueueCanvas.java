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

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.ui.trace.Colors;
import com.nxp.timedoctor.ui.trace.TracePluginActivator;
import com.nxp.timedoctor.ui.trace.descriptions.QueueSampleInfo;

/**
 * Canvas that adds to itself a paint listener to correctly handle the drawing
 * of queues.
 */
public class QueueCanvas extends TraceCanvas {
	private QueuePaintListener queuePaintListener;
	private TraceModel model;
	private SampleLine line;
	
	/**
	 * Constructs a new QueueCanvas in the given composite. QueueCanvases
	 * automatically use style <code>SWT.DOUBLE_BUFFERED</code> to smooth
	 * redrawing.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param line
	 *            the line of samples to draw
	 * @param zoom
	 *            model part containing zoom and scroll data
	 * @param model
	 *            model containing all trace information
	 */
	public QueueCanvas(final Composite parent, 
			final SampleLine line,
			final ZoomModel zoom, 
			final TraceModel model) {
		super(parent, zoom, new QueueSampleInfo(line, zoom));
		
		this.model = model;
		this.line  = line;
		
		queuePaintListener = new QueuePaintListener(Colors.getColorRegistry().get(Colors.DARK_RED), 
								Colors.getColorRegistry().get(Colors.MISTY_ROSE),
								line, zoom, model);
		addPaintListener(queuePaintListener);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TracePluginActivator.PROPORTIONAL_QUEUES_PREFERENCE)) {
			updateHeight();
			redraw();
		}
	}
	
	@Override
	public void setMinHeight(int height) {
		super.setMinHeight(height);
		queuePaintListener.setTraceHeight(height);
		updateHeight();
	}

	private void updateHeight() {
		int traceHeight;
		
		if (preferenceStore.getBoolean(TracePluginActivator.PROPORTIONAL_QUEUES_PREFERENCE)) {
			traceHeight = (int) Math.max(2, 
					(MAXIMUM_ROW_HEIGHT * line.getMaxSampleValue() / model.getMaxSampleValue(line.getType())));
			super.height = Math.max(super.minHeight, traceHeight);
		} else {
			traceHeight = super.minHeight;
			super.height = super.minHeight;
		}
		
		queuePaintListener.setTraceHeight(traceHeight);
	}
}
