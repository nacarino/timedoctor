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
package net.timedoctor.ui.trace;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.ZoomModel;
import net.timedoctor.core.model.Sample.SampleType;
import net.timedoctor.core.model.SampleLine.LineType;
import net.timedoctor.ui.trace.canvases.TraceCanvas;

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
		
		LineType lineType = line.getType();
		
		switch(lineType) {
		case EVENTS:
		case SEMAPHORES:
		case QUEUES:
		case NOTES:
		{
			double timeDifference = Math.abs(line.getSample(index).time - time);
			int dx = (int) (timeDifference * zoomFactor);
			
			if (dx < POPUP_CURSOR_OFFSET) {
				return index;
			}
			
			index++;
			
			try {
				timeDifference = Math.abs(line.getSample(index).time - time);
			} catch (IndexOutOfBoundsException e1) {
				return -1;
			}
			
			dx = (int) (timeDifference * zoomFactor);
			
			if (dx < POPUP_CURSOR_OFFSET) {
				return index;
			}			
			
			return -1;			
		}
		
		case VALUES:
		case CYCLES:
		case MEM_CYCLES:
		{
			if (line.getSample(index).time > time) {
				return -1;
			}
			
			if (index < line.getCount() - 2) {
				return index;
			}
			
			return -1;
		}
		
		case TASKS:
		case ISRS:
		case AGENTS:
		{
			if (line.getSample(index).time > time) {
				return -1;
			}
			
			SampleType taskSampleType = line.getSample(index).type;
			
			switch(taskSampleType){
			case START:
			case SUSPEND:
			case RESUME:
				return index;
			default:
				return -1;			
			}			
		}
		
		default:
			return -1;
		}
	}

	private int getWidth(final MouseEvent e) {
		final Canvas currentCanvas = (Canvas) e.widget;
		final Composite parent1 = currentCanvas.getParent();
		final Composite parent2 = parent1.getParent(); // ScrolledComposite
		return parent2.getBounds().width;
	}
}
