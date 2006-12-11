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

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.model.Sample.SampleType;
import com.nxp.timedoctor.core.model.SampleLine.LineType;
import com.nxp.timedoctor.ui.trace.canvases.TraceCanvas;

/**
 * This class implements <code> MouseTrackListner </code>. It will display a
 * popup containing information specific to the sample line at the cursor
 * position
 */
public class TraceToolTipListener implements MouseMoveListener {
	private static final int POPUP_CURSOR_OFFSET = 6;

	private ZoomModel zoom;
	
	private SampleLine line;

	/*
	 * Constructor for TracePopupListener class.
	 * 
	 * @param zoomData the observable model part containing zoom/scroll zoom
	 * 
	 */
	public TraceToolTipListener(final SampleLine line,
			final ZoomModel zoomData) {
		this.line = line;
		this.zoom = zoomData;
	}

	/*
	 * Displays a tooltip if any sample is present at the mouse position.
	 * 
	 * @param e an event containing information about the mouse position.
	 */
	public void mouseMove(final MouseEvent e) {
		TraceCanvas canvas = (TraceCanvas) e.widget;
		canvas.showSampleInfo(getSampleIndex(e));
	}

	private int getSampleIndex(final MouseEvent e) {
		int width = getWidth(e);
		double zoomFactor = zoom.getPixelsPerTime(width);
		double time = zoom.getTimeAtPosition(e.x, width);

		int index = line.binarySearch(time);
		while ((index > 0) && (line.getSample(index).time > time)) {
			index--;
		}
		while ((index < line.getCount() - 1)
				&& (line.getSample(index + 1).time < time)) {
			index++;
		}

		if ((line.getType() == LineType.EVENTS)
				|| (line.getType() == LineType.SEMAPHORES)
				|| (line.getType() == LineType.QUEUES)
				|| (line.getType() == LineType.NOTES)) {
			double timeDifference = Math
					.abs(line.getSample(index).time - time);
			int dx = (int) (timeDifference * zoomFactor);
			if (dx < POPUP_CURSOR_OFFSET) {
				return index;
			}
			index++;
			timeDifference = Math.abs(line.getSample(index).time
					- time);
			dx = (int) (timeDifference * zoomFactor);
			if (dx < POPUP_CURSOR_OFFSET) {
				return index;
			}
			return -1;
		} else {
			if ((line.getSample(index).time > time)
					|| (line.getSample(index + 1).time < time)) {
				return -1;
			}
			if ((line.getType() == LineType.VALUES)
					|| (line.getType() == LineType.CYCLES)
					|| (line.getType() == LineType.MEM_CYCLES)) {
				if (index < line.getCount() - 2) {
					return index;
				} else {
					return -1;
				}
			} else {
				if (((line.getSample(index).type == SampleType.START)
						|| (line.getSample(index).type == SampleType.SUSPEND) || (line
						.getSample(index).type == SampleType.RESUME))) {
					return index;
				} else {
					return -1;
				}
			}
		}
	}

	private int getWidth(final MouseEvent e) {
		final Canvas currentCanvas = (Canvas) e.widget;
		final Composite parent1 = currentCanvas.getParent();
		final Composite parent2 = parent1.getParent(); // ScrolledComposite
		return parent2.getBounds().width;
	}
}
