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
	 * Sets the main tick height to eight pixels.
	 */
	private static final int MAIN_TICK_HEIGHT = 6;

	/**
	 * Constant setting the secondary tick height to five pixels.
	 */
	private static final int TICK_HEIGHT = 3;

	/**
	 * Sets the offset of the bottom of the label from the ruler line to be nine
	 * pixels.
	 */
	private static final int LABEL_OFFSET = 7;

	/**
	 * The default end time used if the time interval to be visualized is zero
	 * (e.g. in an empty file).
	 */
	private static final int DEFAULT_END_TIME = 10;

	/**
	 * ZoomModel instance containing zoom and scroll information.
	 */
	private ZoomModel zoomModel;

	/**
	 * Constructs a new <code>RulerPaintListener</code> with zoom/scroll zoomModel
	 * automatically updated by the <code>ZoomModel</code> instance.
	 * 
	 * @param zoomModel
	 *            <code>Observable</code> to update zoom/scroll zoomModel
	 */
	public RulerPaintListener(final ZoomModel zoomModel) {
		this.zoomModel = zoomModel;
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
		if (zoomModel.getStartTime() != zoomModel.getEndTime()) {
			startTime = zoomModel.getStartTime();
			endTime = zoomModel.getEndTime();
		}

		Rectangle bounds = ((Canvas) e.widget).getClientArea();
		double pixelsPerTime = bounds.width / (endTime - startTime);

		// Make ruler background white
		e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
		e.gc.fillRectangle(0, 0, bounds.width, bounds.height);

		// Draw ruler
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
		e.gc.drawLine(0, bounds.height - 1, bounds.width,
				bounds.height - 1);

		final Font font = Fonts.getFontRegistry().get(Fonts.RULER_FONT);
		e.gc.setFont(font);

		double drawStartTime = startTime + (e.x / pixelsPerTime);
		double drawEndTime = drawStartTime + (e.width / pixelsPerTime);
		
		double accuracy = zoomModel.getTimeDisplayAccuracy();		
		double interval = zoomModel.getTimeDisplayInterval();
		int count = zoomModel.getIntevalCount();

		double time = accuracy * Math.floor(drawStartTime / accuracy);
		int pos;
		String s;

		while (true) {
			pos = (int) ((time - startTime) * pixelsPerTime);
			e.gc.drawLine(pos, bounds.height - 1, pos,
					bounds.height - 1 - MAIN_TICK_HEIGHT);
			s = Times.timeToString(time, accuracy);
			Point stringSize = e.gc.textExtent(s);
			if (time == 0.0) {
				e.gc.drawString(s, pos, bounds.height - 1
						- LABEL_OFFSET - e.gc.textExtent(s).y);
			} else {
				e.gc.drawString(s, pos - (stringSize.x / 2), bounds.height
						- 1 - LABEL_OFFSET
						- e.gc.textExtent(s).y);
			}
			time += interval;
			if (time > drawEndTime) {
				break;
			}
			for (int i = 0; i < count - 1; i++) {
				pos = (int) ((time - startTime) * pixelsPerTime);
				e.gc.drawLine(pos, bounds.height - 1, pos,
						bounds.height - 1 - TICK_HEIGHT);
				time += interval;
			}
		}
	}
}
