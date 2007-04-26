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
import com.nxp.timedoctor.ui.trace.descriptions.ValueSampleInfo;

/**
 * Canvas to draw counters for numeric values.
 */
public class ValueCanvas extends TraceCanvas {
	private TraceModel model;
	private SampleLine line;
	private CounterPaintListener counterPaintListener;
	
	/**
	 * Constructs a new ValueCanvas in the given composite.
	 * 
	 * @param parent 
     *          the parent composite
	 * @param line 
     *          <code>SampleLine</code> to be drawn
	 * @param zoom
     *          <code>ZoomModel</code> containing zoom and scroll data
	 * @param traceModel
     * 			model containing all trace information
	 */
	public ValueCanvas(final Composite parent, 
			final SampleLine line, 
			final ZoomModel zoom,
			final TraceModel traceModel) {
		super(parent, zoom, new ValueSampleInfo(line, zoom));
		this.model = traceModel;
		this.line = line;
		
		counterPaintListener = new CounterPaintListener(Colors.getColorRegistry().get(Colors.DARK_CYAN), 
								Colors.getColorRegistry().get(Colors.LIGHT_CYAN),
								line, zoom);
		addPaintListener(counterPaintListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TracePluginActivator.PROPORTIONAL_COUNTERS_PREFERENCE)) {
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
		int traceHeight;
		
		if (preferenceStore.getBoolean(TracePluginActivator.PROPORTIONAL_COUNTERS_PREFERENCE)) {
			traceHeight = (int) Math.max(2, 
					(MAXIMUM_ROW_HEIGHT * line.getMaxSampleValue() / model.getMaxSampleValue(line.getType())));
			super.height = Math.max(super.minHeight, traceHeight);
		} else {
			traceHeight = super.minHeight;
			super.height = super.minHeight;
		}
		
		counterPaintListener.setTraceHeight(traceHeight);
	}
}
