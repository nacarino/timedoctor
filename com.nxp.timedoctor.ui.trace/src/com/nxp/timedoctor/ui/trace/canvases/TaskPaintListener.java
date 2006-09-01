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
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.model.Sample.SampleType;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;

/**
 * Contains the code to paint a task, ISR, or agent.
 */
public class TaskPaintListener implements PaintListener {

	/**
	 * The minimum allowed x-value, for use in the <code>boundedInt</code>
	 * function.
	 */
	private static final int X_MIN = -100;

	/**
	 * The maximum allowed x-value, for use in the <code>boundedInt</code>
	 * function.
	 */
	private static final int X_MAX = 100000;

	/**
	 * Vertical padding value on the bottom of trace lines.
	 */
	private static final int VERTICAL_PADDING = 2;

	/**
	 * The model containing all trace data. Used to retrieve the end time of the
	 * full trace, for use in full trace width calculations.
	 */
	private TraceModel model;

	/**
	 * The line containing data to visualize.
	 */
	private SampleLine line;

	/**
	 * The color to use in painting the line.
	 */
	private Color color;

	/**
	 * <code>Observable</code> containing zoom and scroll data.
	 */
	private ZoomModel data;

	/**
	 * The starting time of the part of the line currently displayed, based on
	 * scroll data.
	 */
	private double timeOffset;

	/**
	 * Constructs a new <code>TaskPaintListener</code> with the given color,
	 * sample line, and source of zoom/scroll data.
	 * 
	 * @param col
	 *            the color with which to pain the line
	 * @param sampleLine
	 *            contains the data to be displayed
	 * @param zoomData
	 *            contains data on the zoom/scroll state of the system
	 * @param tdModel
	 *            contains all trace data
	 */
	public TaskPaintListener(final Color col, final SampleLine sampleLine,
			final ZoomModel zoomData, final TraceModel tdModel) {
		color = col;
		model = tdModel;
		line = sampleLine;
		data = zoomData;
	}

	/**
	 * Sent when a paint event occurs for the control. Repaints the affected
	 * section of the line.
	 * 
	 * @param e
	 *            an event containing information about the paint
	 * 
	 * @see PaintListener#paintControl(PaintEvent)
	 */
	public final void paintControl(final PaintEvent e) {

		if (data.getStartTime() != data.getEndTime()) {
			timeOffset = data.getStartTime();
			boolean active = false;
			Canvas canvas = ((Canvas) e.widget);
			Composite section = canvas.getParent();
			Composite rightPane = section.getParent();
			Composite scroll = rightPane.getParent();

			// guarantees trace drawing is unaffected by appearance of vertical
			// scrollbar.
			int fullWidth = scroll.getBounds().width;
			int fullHeight = canvas.getBounds().height;
			double zoom = fullWidth / (data.getEndTime() - data.getStartTime());
			final double drawStartTime = timeOffset + (e.x / zoom);
			final double drawEndTime = drawStartTime + (e.width / zoom);
			int index = line.binarySearch(drawStartTime);

			e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
			e.gc.fillRectangle(e.x, e.y, e.width, e.height);

			if (line.getSample(index).time < drawStartTime
					&& line.getSample(index).type != SampleType.STOP) {
				active = true;
			}

			for (int xOld = -1; index < line.getCount(); index++) {
				if (line.getSample(index).time > drawEndTime) {
					break;
				}
				if (!active && line.getSample(index).type == SampleType.START) {
					active = true;
				}
				if (active) {
					if (line.getSample(index).type == SampleType.START
							|| line.getSample(index).type
								== SampleType.RESUME) {
						final int xStart = boundedInt((line.getSample(index).time - timeOffset)
								* zoom);
						final int xEnd = boundedInt((line.getSample(index + 1).time - timeOffset)
								* zoom);
						if (xEnd <= xOld) {
							continue;
						}
						xOld = xEnd;
						e.gc.setForeground(color);
						if (xStart == xEnd) {
							e.gc.drawLine(xStart, 0, xEnd, fullHeight
									- VERTICAL_PADDING);
						} else {
							e.gc.drawRectangle(xStart, 0, xEnd - xStart,
									fullHeight - VERTICAL_PADDING);
						}
					} else if (line.getSample(index).type == SampleType.SUSPEND) {
						final int j = (int) line.getSample(index).val;
						if (line.getSample(j).time < drawStartTime) {
							index = j - 1;
							continue;
						}
						final int xStart = boundedInt((line.getSample(index).time - timeOffset)
								* zoom);
						final int xEnd = Math.max(xStart + 1, boundedInt((line
								.getSample(j).time - timeOffset)
								* zoom));
						xOld = xEnd;
						index = j - 1;
						e.gc.setBackground(e.display
								.getSystemColor(SWT.COLOR_GRAY));
						e.gc.fillRectangle(xStart, 0, xEnd - xStart, fullHeight
								- VERTICAL_PADDING);
						if (xStart == xEnd) {
							e.gc.drawLine(xStart, 0, xEnd, fullHeight
									- VERTICAL_PADDING);
						} else {
							e.gc.setForeground(color);
							e.gc.drawRectangle(xStart, 0, xEnd - xStart,
									fullHeight - VERTICAL_PADDING);
						}

					} else if (line.getSample(index).type == SampleType.STOP
							|| line.getSample(index).type == SampleType.END) {
						active = false;
					}
				}
			}
		}
	}

	/**
	 * Ensures the given value is within the valid x-values and casts it to an
	 * int. If the value is too low, returns <code>X_MIN</code>. If it's too
	 * high, returns <code>X_MAX</code>.
	 * 
	 * @param value
	 *            the value to be checked and casted
	 * @return <code>value</code>, <code>X_MIN</code>, or <code>X_MAX</code>
	 */
	private int boundedInt(final double val) {
		return (int) Math.min(X_MAX, Math.max(X_MIN, val));
	}

}
