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
import com.nxp.timedoctor.ui.trace.descriptions.SemaphoreSampleInfo;

/**
 * Skeleton class for a canvas to display lines of type <code>SEMAPHORE</code>.
 */
public class SemaphoreCanvas extends TraceCanvas {
	private TraceModel model;
	private SampleLine line;
	
	private EventPaintListener eventPaintListener;
	private QueuePaintListener queuePaintListener;
	
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
	 * @param traceModel
	 *            model containing all trace information
     */
    public SemaphoreCanvas(final Composite parent, final SampleLine line,
			final ZoomModel zoom, final TraceModel traceModel) {
		super(parent, zoom, new SemaphoreSampleInfo(line));
		
		this.model = traceModel;
		this.line  = line;
		
		eventPaintListener = new EventPaintListener (Colors.getColorRegistry().get(Colors.DARK_VIOLET), 
						Colors.getColorRegistry().get(Colors.THISTLE), line, zoom);
		queuePaintListener = new QueuePaintListener(Colors.getColorRegistry().get(Colors.DARK_VIOLET), 
						Colors.getColorRegistry().get(Colors.THISTLE), line, zoom, traceModel);
	
		installPaintListener();
	}

	private void installPaintListener() {
		if (preferenceStore.getBoolean(TracePluginActivator.SEMAPHORE_QUEUE_PREFERENCE)) {
			removePaintListener(eventPaintListener);
			addPaintListener(queuePaintListener);
		} else {
			removePaintListener(queuePaintListener);
			addPaintListener(eventPaintListener);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if(event.getProperty().equals(TracePluginActivator.SEMAPHORE_QUEUE_PREFERENCE)) {
			installPaintListener();
			updateHeight();
			redraw();
		}
		if (event.getProperty().equals(TracePluginActivator.PROPORTIONAL_QUEUES_PREFERENCE)) {
			updateHeight();
			redraw();
		}
	}

	@Override
	public void setMinHeight(int height) {
		super.setMinHeight(height);
		updateHeight();
	}
	
	private void updateHeight() {
		if (preferenceStore.getBoolean(TracePluginActivator.SEMAPHORE_QUEUE_PREFERENCE)) {
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
		} else {
			super.height = super.minHeight;
		}
	}
}
