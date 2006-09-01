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

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.ZoomModel;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;

/**
 * Will handle correctly painting lines of types <code>CYCLES</code> and
 * <code>MEM_CYCLES</code>. Currently a skeleton file.
 */
public class CyclesPaintListener implements PaintListener {

	/**
	 * The minimum x-value, for use in the <code>boundedInt</code> function.
	 */
	private static final int X_MIN = -100;

	/**
	 * The maximum x-value, for use in the <code>boundedInt</code> function.
	 */
	private static final int X_MAX = 100000;

	/**
	 * The initial spacing between grid lines. Will probably be deleted, done
	 * proportionally from canvas height.
	 */
	private static final int GRID_SPACING = 10;

	/**
	 * The vertical padding between visible traces. Displayed only on the
	 * bottom.
	 */
	private static final int VERTICAL_PADDING = 2;

	/**
	 * The light color to use in drawing, for filling the inside of the
	 * rectangles.
	 */
	private Color lightColor;

	/**
	 * The dark color to be used for outlining the rectangles.
	 */
	private Color darkColor;

	/**
	 * The line of type <code>CYCLES</code> or <code>MEM_CYCLES</code>
	 * containing data to be drawn.
	 */
	private SampleLine line;

	/**
	 * The model component containing data on current zoom and offset values.
	 */
	private ZoomModel data;

	/**
	 * The end time of the currently displayed area.
	 */
	private double endTime;

	/**
	 * The time offset due to scrolling. Updated automatically by the
	 * <code>ZoomModel</code> instance.
	 */
	private double startTime;

	/**
	 * Constructs a new paint listener with the given light color, dark color,
	 * sample line, and source of zoom/scroll data.
	 * 
	 * @param light
	 *            the light color to use in drawing the line
	 * @param dark
	 *            the dar color to use in drawing the line
	 * @param sampleLine
	 *            a line of type <code>CYCLES</code> or
	 *            <code>MEM_CYCLES</code>
	 * @param zoomData
	 *            the source of zoom and scroll data
	 */
	public CyclesPaintListener(final Color light, final Color dark,
			final SampleLine sampleLine, final ZoomModel zoomData) {
		lightColor = light;
		darkColor = dark;
		this.line = sampleLine;
		data = zoomData;
		startTime = data.getStartTime();
	}

	/**
	 * Handles the paining of a line of type <code>CYCLES</code> or
	 * <code>MEM_CYCLES</code> as a series of rectangles with heights
	 * representing the values of the samples.
	 * 
	 * @param e
	 *            PaintEvent containing data about the paint
	 */
	public final void paintControl(final PaintEvent e) {

	}

}
