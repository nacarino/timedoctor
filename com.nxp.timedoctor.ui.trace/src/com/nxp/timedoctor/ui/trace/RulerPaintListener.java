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
package com.nxp.timedoctor.ui.trace;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import com.nxp.timedoctor.core.model.Times;
import com.nxp.timedoctor.core.model.ZoomModel;

/**
 * PaintListener for the ruler. Handles making the correct tick marks and labels
 * based upon zoom factor and scroll position.
 */
public class RulerPaintListener implements PaintListener {

	/**
	 * Sets the vertical padding value for the ruler to be threee pixels.
	 */
	private static final int VERTICAL_PADDING = 3;

	/**
	 * Sets the initial number of ticks per unit (in this case a second) to be
	 * ten.
	 */
	private static final int DEFAULT_TICKS_PER_UNIT = 10;

	/**
	 * Sets the main tick height to eight pixels.
	 */
	private static final int MAIN_TICK_HEIGHT = 8;

	/**
	 * Constant setting the secondary tick height to five pixels.
	 */
	private static final int TICK_HEIGHT = 5;

	/**
	 * Sets the offset of the bottom of the label from the ruler line to be nine
	 * pixels.
	 */
	private static final int LABEL_OFFSET = 9;

	/**
	 * Font size for labels, seven points.
	 */
	private static final int FONT_SIZE = 7;

	/**
	 * Sets the initial time interval size.
	 */
	private static final int INITIAL_INTERVAL = 1000;

	/**
	 * The default end time used if the time interval to be visualized is zero
	 * (e.g. in an empty file).
	 */
	private static final int DEFAULT_END_TIME = 10;

	/**
	 * ZoomModel instance containing zoom and scroll information.
	 */
	private ZoomModel data;
	
	/**
	 * Canvas created by setting the ruler as the parent.
	 */

	private static Canvas rulerCanvas;
	
	/**
	 * Previous x-position used in drawing the time in ruler.
	 */
	private static int prevXposition = 0;

	/**
	 * which holds the previous time value.
	 */
	private static String prevTime = "0s";


	/**
	 * Constructs a new <code>RulerPaintListener</code> with zoom/scroll data
	 * automatically updated by the <code>ZoomModel</code> instance.
	 * 
	 * @param zoomData
	 *            <code>Observable</code> to update zoom/scroll data
	 */
	public RulerPaintListener(final ZoomModel zoomData) {
		this.data = zoomData;
	}

	/**
	 * Handles the painting of the ruler when a paint event is called on it.
	 * 
	 * @param e
	 *            PaintEvent containing detailed information about the event
	 */
	public final void paintControl(final PaintEvent e) {

		double startTime = 0;
		double endTime = DEFAULT_END_TIME;
		if (data.getStartTime() != data.getEndTime()) {
			startTime = data.getStartTime();
			endTime = data.getEndTime();
		}

		Rectangle bounds = ((Canvas) e.widget).getClientArea();
		rulerCanvas = (Canvas) e.widget;
		double zoom = bounds.width / (endTime - startTime);

		// Make ruler background white
		e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
		e.gc.fillRectangle(0, 0, bounds.width, bounds.height);

		// Draw ruler
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
		e.gc.drawLine(0, bounds.height - VERTICAL_PADDING, bounds.width,
				bounds.height - VERTICAL_PADDING);

		Font font = new Font(e.display, "Arial", FONT_SIZE, SWT.NORMAL);

		double drawStartTime = startTime + (e.x / zoom);
		double drawEndTime = drawStartTime + (e.width / zoom);

		double interval = INITIAL_INTERVAL;
		double width = INITIAL_INTERVAL * zoom;
		int count = DEFAULT_TICKS_PER_UNIT;
		
		// these numbers seem clear enough to leave here
		int cursorWidth = 60;
		while (width > 10 * cursorWidth) {
			interval /= 10;
			width /= 10;
		}
		if (width > 5 * cursorWidth) {
			interval /= 5;
			width /= 5;
			count = 2;
		} else if (width > 2 * cursorWidth) {
			interval /= 2;
			width /= 2;
			count = 5;
		}
		double accuracy = interval;
		interval /= count;

		double time = accuracy * Math.floor(drawStartTime / accuracy);
		int pos;
		String s;

		while (true) {
			pos = (int) ((time - startTime) * zoom);
			e.gc.drawLine(pos, bounds.height - VERTICAL_PADDING, pos,
					bounds.height - VERTICAL_PADDING - MAIN_TICK_HEIGHT);
			s = Times.timeToString(time, accuracy);
			Point stringSize = e.gc.textExtent(s);
			if (time == 0.0) {
				e.gc.drawString(s, pos, bounds.height - VERTICAL_PADDING
						- LABEL_OFFSET - e.gc.textExtent(s).y);
			} else {
				e.gc.drawString(s, pos - (stringSize.x / 2), bounds.height
						- VERTICAL_PADDING - LABEL_OFFSET
						- e.gc.textExtent(s).y);
			}
			time += interval;
			if (time > drawEndTime) {
				break;
			}
			for (int i = 0; i < count - 1; i++) {
				pos = (int) ((time - startTime) * zoom);
				e.gc.drawLine(pos, bounds.height - VERTICAL_PADDING, pos,
						bounds.height - VERTICAL_PADDING - TICK_HEIGHT);
				time += interval;
			}
		}
		font.dispose();
	}
}
