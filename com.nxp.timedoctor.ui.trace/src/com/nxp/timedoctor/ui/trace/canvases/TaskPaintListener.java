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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.model.Sample.SampleType;

/**
 * Contains the code to paint a task, ISR, or agent.
 */
public class TaskPaintListener extends TracePaintListener implements PaintListener {

	/**
	 * Vertical padding value above a trace line.
	 * Relatively large value as task trace does not
	 * need to use the full canvas height.
	 */
	private static final int VERTICAL_PADDING = 6;

	/**
	 * Contains the list of colors which are used to paint the event.
	 */
	private static final RGB[] colorList = new RGB[] { 
        new RGB(0XEF, 0X00, 0X00),        
		new RGB(0x00, 0x00, 0xEF),
        new RGB(0xFF, 0xFF, 0x00),        
        new RGB(0xFF, 0x00, 0xFF),
        new RGB(0x94, 0x00, 0xD3),
		new RGB(0XFF, 0XB0, 0X8A), 
        new RGB(0x80, 0xFF, 0xFF),
		new RGB(0x00, 0xFF, 0x80) 
        };

	/**
	 * Length of <code>colorList</code>.
	 */
	private static final int MAX_COLORS = 8;
	
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
	 * Used as an index for colorlist.
	 */
	private int colorIndex;

	/**
	 * Constructs a new <code>TaskPaintListener</code> with the given color,
	 * sample line, and source of zoom/scroll data.
	 * 
	 * @param col
	 *            the color with which to paint the line
	 * @param sampleLine
	 *            contains the data to be displayed
	 * @param zoomData
	 *            contains data on the zoom/scroll state of the system
	 * @param tdModel
	 *            contains all trace data
	 */
	public TaskPaintListener(final Color color, 
			final SampleLine sampleLine,
			final ZoomModel zoomData, 
			final TraceModel tdModel) {
		this.color = color;
		this.line = sampleLine;
		this.data = zoomData;
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
			int canvasHeight = canvas.getBounds().height;
			int traceHeight = canvasHeight - VERTICAL_PADDING;
			double zoom = fullWidth / (data.getEndTime() - data.getStartTime());

			final double drawStartTime = timeOffset + (e.x / zoom);
			final double drawEndTime = drawStartTime + (e.width / zoom);
			int index = line.binarySearch(drawStartTime);

			e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
			e.gc.fillRectangle(e.x, e.y, e.width, e.height);

			if ((line.getSample(index).time < drawStartTime)
					&& (line.getSample(index).type != SampleType.STOP)) {
				active = true;
			}
			
			for (int xOld = -1; index < line.getCount(); index++) {
				if (line.getSample(index).time > drawEndTime) {
					break;
				}
				if (!active && (line.getSample(index).type == SampleType.START)) {
					active = true;
				}
				if (active) {
					if ((line.getSample(index).type == SampleType.START)
							|| (line.getSample(index).type == SampleType.RESUME)) {
                        
						final int xStart = boundedInt((line.getSample(index).time - timeOffset)
								* zoom);
						final int xEnd = boundedInt((line.getSample(index + 1).time - timeOffset)
								* zoom);
						if (xEnd <= xOld) {
							continue;
						}
						xOld = xEnd;
						e.gc.setForeground(color);

						if (line.getSample(index).type == SampleType.RESUME) {
							colorIndex = (int) line.getSample(index).val;
						} else {
							colorIndex = (int) line.getSample((int) line
									.getSample(index).val).val;
						}
						if (colorIndex < 0) {
							e.gc.setBackground(canvas.getDisplay()
                                    .getSystemColor(SWT.COLOR_WHITE));
						} else {
							e.gc.setBackground(new Color(canvas.getDisplay(),
									colorList[colorIndex % MAX_COLORS]));
						}

						if (xStart == xEnd) {
							e.gc.drawLine(xStart, canvasHeight, xEnd, VERTICAL_PADDING);
						} else {
							e.gc.fillRectangle(xStart, VERTICAL_PADDING,
									xEnd - xStart, traceHeight);
							e.gc.drawRectangle(xStart, VERTICAL_PADDING, xEnd - xStart,
									traceHeight);
						}
					} else if (line.getSample(index).type == SampleType.SUSPEND) {
						// Handle preemption by another task or ISR
						
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

						if (xStart == xEnd) {
							e.gc.drawLine(xStart, canvasHeight, xEnd, VERTICAL_PADDING);
						} else {
							e.gc.setForeground(color);
							
							e.gc.fillRectangle(xStart, VERTICAL_PADDING, xEnd - xStart, traceHeight);
							e.gc.drawRectangle(xStart, VERTICAL_PADDING, xEnd - xStart,
									traceHeight);
						}

					} else if ((line.getSample(index).type == SampleType.STOP)
							|| (line.getSample(index).type == SampleType.END)) {
						active = false;
					}
				}
			}
		}
	}
}
