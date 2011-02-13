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
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.model.Sample.SampleType;
import com.nxp.timedoctor.core.model.SampleLine.LineType;
import com.nxp.timedoctor.ui.trace.Colors;

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
	private static final String[] colorList = new String[] {
		Colors.TASKCOLOR1,
		Colors.TASKCOLOR2,
		Colors.TASKCOLOR3,
		Colors.TASKCOLOR4,
		Colors.TASKCOLOR5,
		Colors.TASKCOLOR6,
		Colors.TASKCOLOR7,
		Colors.TASKCOLOR8
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

	private boolean fSubPixelOn;

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
			int traceHeight = canvasHeight - VERTICAL_PADDING; //height of the trace-blocks
			
			//for subpixel calculation
			double totalTimeIn1Pixel = 0;
			int subPixelTraceHeight = 0;
			
			double zoom = fullWidth / (data.getEndTime() - data.getStartTime()); //zoom = pixels / time

			final double drawStartTime = timeOffset + (e.x / zoom);
			final double drawEndTime = drawStartTime + (e.width / zoom);
			
			//search for the first sample before drawStartTime
			int index = line.binarySearch(drawStartTime);

			e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
			e.gc.fillRectangle(e.x, e.y, e.width, e.height);

			if ((line.getSample(index).time <= drawStartTime)
					&& (line.getSample(index).type != SampleType.STOP)) {
				active = true;
			}
			
			for (int xEndOld = -1; index < line.getCount(); index++) { //for loop over all the samples starting from 'index'
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

						if (!fSubPixelOn && (xEnd <= xEndOld)) {
							continue;
						}

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
							final String colorType = colorList[colorIndex % MAX_COLORS];
							e.gc.setBackground(Colors.getColorRegistry().get(colorType));
						}
						
						/*
						 * For sub-pixel visualization, the calculation of traceHeight is explained as follows.
						 * 
						 * timeIn1Pixel = 1 pixel / zoom, The time being drawn in 1 pixel
						 *  
						 * totalTimeIn1Pixel, stores the total time the CPU was occupied in 1 pixel. If more than one sample 
						 *                    is being painted on the same pixel-column, then the time they occupy are summed up.
						 * 
						 * load = totalTimeIn1Pixel/timeIn1Pixel, Load is the time the CPU is active, in the pixel column.
						 * 
						 * subPixelTraceHeight = load * traceHeight
						 * =>
						 * subPixelTraceHeight = totalTimeIn1Pixel * zoom * traceHeight  
						 *  
						 */
						if (fSubPixelOn && (xStart == xEnd)) {
							if (xEndOld == xStart) {
								//We have already calculated a totalTimeIn1Pixel for this pixel
								totalTimeIn1Pixel += (line.getSample(index+1).time - line.getSample(index).time);								
							} else {
								//First sample in this pixel
								totalTimeIn1Pixel = (line.getSample(index+1).time - line.getSample(index).time);								
							}	

							subPixelTraceHeight = (int)Math.ceil(totalTimeIn1Pixel * zoom * traceHeight);
							e.gc.drawLine(xStart, canvasHeight, xStart, canvasHeight - subPixelTraceHeight); //draw this pixel
							
						} else if (fSubPixelOn && (xStart == (xEnd - 1))) {
							//The sample spans two pixels, so we need to redraw both pixels							
							if (xEndOld == xStart) {
								//We have already calculated a totalTimeIn1Pixel for the first pixel
								totalTimeIn1Pixel += ((xStart + 1)/zoom + timeOffset - line.getSample(index).time);								
							} else {
								//First sample in the first pixel
								totalTimeIn1Pixel = ((xStart + 1)/zoom + timeOffset - line.getSample(index).time);								
							}
							
							subPixelTraceHeight = (int)Math.ceil(totalTimeIn1Pixel * zoom * traceHeight);
							e.gc.drawLine(xStart, canvasHeight, xStart, canvasHeight - subPixelTraceHeight); //draw this pixel
							
							totalTimeIn1Pixel = (line.getSample(index + 1).time - ((xStart + 1)/zoom + timeOffset));
							subPixelTraceHeight = (int)Math.ceil(totalTimeIn1Pixel * zoom * traceHeight);
							e.gc.drawLine(xEnd, canvasHeight, xEnd, canvasHeight - subPixelTraceHeight); //draw next pixel
							
						} else if (xStart == xEnd) {
							e.gc.drawLine(xStart, canvasHeight, xEnd, VERTICAL_PADDING);
						} else {
							e.gc.fillRectangle(xStart, VERTICAL_PADDING, xEnd - xStart, traceHeight);
							e.gc.drawRectangle(xStart, VERTICAL_PADDING, xEnd - xStart, traceHeight);
							totalTimeIn1Pixel = 0;
						}
						xEndOld = xEnd;
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
						xEndOld = xEnd;
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
	
	public void enableSubPixel(boolean enable) {
		fSubPixelOn = enable && (line.getType() == LineType.TASKS);
	}
}
