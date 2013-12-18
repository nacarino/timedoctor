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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.ZoomModel;
import net.timedoctor.core.model.SampleLine.LineType;

/**
 * Contains the code to paint a queue.
 */
public class QueuePaintListener extends TracePaintListener implements PaintListener {

	/**
	 * The line containing zoom to visualize.
	 */
	private SampleLine line;

	/**
	 * The color to use in painting the line.
	 */
	private Color color;

    /**
     * The color used to fill the Queue.
     */
	private Color fillColor;

	/**
	 * <code>Observable</code> containing zoom and scroll zoom.
	 */
	private ZoomModel zoom;

	private int traceHeight;
	private int traceMinHeight = -1;
	
	/**
	 * Constructs a new <code>TaskPaintListener</code> with the given color,
	 * sample line, and source of zoom/scroll zoom.
	 * 
	 * @param color
	 *            the color with which to pain the line
     *  @param fillColor
     *            the color which is used to fill the rectangle 
	 * @param sampleLine
	 *            contains the zoom to be displayed
	 * @param zoom
	 *            contains the zoom/scroll state of the system
	 * @param tdModel
	 *            model containing all trace information
	 */
	public QueuePaintListener(final Color color, final Color fillColor,
			final SampleLine sampleLine, final ZoomModel zoom,
			final TraceModel tdModel) {
		this.color = color;
		this.fillColor = fillColor;
		this.line = sampleLine;
		this.zoom = zoom;
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
		if (zoom.getStartTime() != zoom.getEndTime()) {
			Canvas canvas = ((Canvas) e.widget);
			Composite section = canvas.getParent();
			Composite rightPane = section.getParent();
			Composite scroll = rightPane.getParent();

			// guarantees trace drawing is unaffected by appearance of vertical
			// scrollbar.
			int fullWidth = scroll.getBounds().width;
			int canvasHeight = canvas.getBounds().height;
			
			int traceDrawHeight;
			
			if (traceHeight > traceMinHeight) {
				traceDrawHeight = canvasHeight;
			} else {
				traceDrawHeight = traceHeight * (canvasHeight/traceMinHeight);
			}
			
			double startTime = zoom.getStartTime();
			double endTime = zoom.getEndTime();
			double pixelsPerSec = fullWidth / (endTime - startTime);
			double drawStartTime = startTime + ((e.x) / pixelsPerSec);
			double drawEndTime = drawStartTime + ((e.width) / pixelsPerSec);
			double maxFilling = line.getMaxSampleValue();

			e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
			e.gc.fillRectangle(e.x, e.y, e.width, e.height);
			
			drawGridLines(e, canvasHeight); 

			int index = Math.max(1, line.binarySearch(drawStartTime));
			double curMaxFilling = 0;
			double curMinFilling = 0;
			for (; index < line.getCount(); index++) {
				int xCur = boundedInt((line.getSample(index - 1).time - startTime) * pixelsPerSec);
				int xNext = boundedInt((line.getSample(index).time - startTime) * pixelsPerSec);

				// TODO hide >> 32 in model interface
				double curFilling = line.getSample(index - 1).val;
				if (line.getType() == LineType.QUEUES) {
					curFilling = (long)(curFilling) >> 32;
				}
				
				// Compute maximum and minimum over the last queue events that fall within the same pixel
				// Always at least include the previous queue event to ensure the contour line is
				// continuous (the queue event typically occurs somewhere within a pixel, 
				// not on the exact pixel boundary).
				curMaxFilling = Math.max(curMaxFilling, curFilling);
				curMinFilling = Math.min(curMinFilling, curFilling);
				
				if ((xNext == xCur) && (index < line.getCount())) {
					continue;
				}
				
				// Get current buffer filling in pixels
				int curFillHeight = 0;
				if (curFilling > 0) {
					// Show at least one pixel if there is something in the queue
					curFillHeight = Math.max(1, (int) (traceDrawHeight * curFilling / maxFilling));
				}

				// Get min buffer filling in pixels
				int minFillHeight = 0;
				if (curMinFilling > 0) {
					// Show at least one pixel if there is something in the queue
					minFillHeight = Math.max(1, (int) (traceDrawHeight * curMinFilling / maxFilling));
				}

				// Get max buffer filling in pixels
				int maxFillHeight = (int) (traceDrawHeight * curMaxFilling / maxFilling);

				// Note: fillRectangle is drawn (verified for MS Windows) from the left-upper origin
				// including the origin, up to (excluding) width, height
				// Note that the origin stays upper-left, even when height is negative
				// Lines are drawn including the start and end point
				// A line with the same start and end point draws a point
				
				e.gc.setForeground(color);
				e.gc.setBackground(fillColor);
					
				// Draw rectangle with actual value
				// Set height origin to fullHeight + 1 to include drawing at fullHeight
				e.gc.fillRectangle(xCur, canvasHeight + 1, 
						xNext - xCur, - curFillHeight);
				// Draw top line on top of rectangle
				e.gc.drawLine(xCur, canvasHeight - curFillHeight, 
						xNext, canvasHeight - curFillHeight);

				// Draw min line
				e.gc.setForeground(fillColor);
				e.gc.drawLine(xCur, canvasHeight, 
						xCur, canvasHeight - minFillHeight);

				// Draw max line on top of min line
				// Drawing after the drawing of min line ensures that if max=min=0
				// only the contour is drawn (one pixel for the max line is visible)
				e.gc.setForeground(color);
				e.gc.drawLine(xCur, canvasHeight - minFillHeight, 
						xCur, canvasHeight - maxFillHeight);

				if (line.getSample(index).time > drawEndTime) {
					break;
				}

				// Include the previouse sample in the min/max computations
				// for the next sample
				curMaxFilling = curFilling;
				curMinFilling = curFilling;
			}
		}
	}

	public void setTraceHeight(int height) {
		if (traceMinHeight == -1) {
			traceMinHeight = height;
		}
		traceHeight = height;
	}
}
