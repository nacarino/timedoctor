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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.ZoomModel;
import net.timedoctor.ui.trace.Colors;
import net.timedoctor.ui.trace.TracePluginActivator;
import net.timedoctor.ui.trace.descriptions.CycleSampleInfo;

/**
 * Canvas for graphical display of <code>SampleLine</code>s of type
 * <code>CYCLES</code>. Handles its own drawing through a
 * <code>CyclesPaintListener</code>.
 */
public class CyclesCanvas extends TraceCanvas {
	
	private TraceModel model;
	private SampleLine line;

	/**
	 * The initial height of the canvas. Will probably be removed, handled at a
	 * higher level.
	 */
	private static final int INITIAL_HEIGHT = 50;
	private CounterPaintListener counterPaintListener;

	/**
	 * Constructs a new canvas for the display of cycles.
	 * 
	 * @param parent
	 *            the parent composite in which to create this canvas
	 * @param line
	 *            the line of type <code>CYCLES</code> containing the data to
	 *            be displayed
	 * @param zoom
	 *            model part containing zoom/scroll data
	 * @param traceModel The {@link TraceModel} object
	 */
	public CyclesCanvas(final Composite parent, 
			final SampleLine line,
			final ZoomModel zoom, 
			final TraceModel traceModel) {
		super(parent, zoom, new CycleSampleInfo(line, zoom));

		setSize(computeSize(SWT.DEFAULT, INITIAL_HEIGHT));
		
		this.model = traceModel;
		this.line = line;

		counterPaintListener = new CounterPaintListener(Colors.getColorRegistry().get(Colors.DARK_CYAN), 
								Colors.getColorRegistry().get(Colors.LIGHT_CYAN), line, zoom);
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
		counterPaintListener.setTraceHeight(height);
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
