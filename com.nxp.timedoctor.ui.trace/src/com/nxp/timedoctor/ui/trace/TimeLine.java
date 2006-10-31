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

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;

import com.nxp.timedoctor.core.model.Times;
import com.nxp.timedoctor.core.model.ZoomModel;

/**
 * Draw a marker line and time label 
 * as composites above all other
 * widgets. 
 */
public class TimeLine implements Observer {
	private static final int FORM_LAYOUT_FULL = 100;
	private static final int CURSOR_LINE_WIDTH = 1;

	protected ZoomModel zoom;
	
	protected Label cursorLabel;
	protected Sash cursorSash;
	protected Composite cursorLine;
	
	private double cursorTime = 0;
	
	public TimeLine(final Composite rulerPane, 
			final Composite tracePane, 
			final ZoomModel zoom, 
			final int color, 
			final int offset) {
		this.zoom = zoom;
		
		// Ensure updates when zoom or horizontal scroll changes
		zoom.addObserver(this);
		
		createRulerContents(rulerPane, color, offset);
		createTraceContents(tracePane, color);
	}

	// Ensure that zoom does not call a deleted observer
	public void dispose() {
		zoom.deleteObserver(this);		
	}
	
	private void createRulerContents(final Composite parent, 
			final int color, 
			final int offset) {
		// Time label
		cursorLabel = new Label(parent, SWT.LEFT);
 		cursorLabel.setBackground(cursorLabel.getDisplay().getSystemColor(SWT.COLOR_WHITE));
 		cursorLabel.setForeground(cursorLabel.getDisplay().getSystemColor(color));

		final FormData cursorLabelData = new FormData();
		cursorLabelData.top = new FormAttachment(0, offset);
		cursorLabel.setLayoutData(cursorLabelData);
		
		cursorLabel.setVisible(false);
		
		// Sash
		cursorSash = new Sash(parent, SWT.VERTICAL);
 		cursorSash.setBackground(cursorLabel.getDisplay().getSystemColor(color));
 		
		final FormData cursorSashData = new FormData();
		cursorSashData.top = new FormAttachment(cursorLabel);
		cursorSashData.bottom = new FormAttachment(FORM_LAYOUT_FULL);
		cursorSashData.width = CURSOR_LINE_WIDTH;
		cursorSash.setLayoutData(cursorSashData);
		
		cursorSash.setVisible(false);
		
		// Handle window resize and maximize events
		// (Only one needed, either in ruler or trace area)
		parent.addControlListener(new ControlListener(){
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				updatePositionAndLabel();
			}
		});
	}
	
	private void createTraceContents(final Composite parent, final int color)
	{
		// Cursor line
		cursorLine = new Composite(parent, SWT.NONE);
 		cursorLine.setBackground(cursorLabel.getDisplay().getSystemColor(color));
 		
		final FormData cursorLineData = new FormData();
		cursorLineData.top = new FormAttachment(0);
		cursorLineData.width = CURSOR_LINE_WIDTH;
		cursorLineData.bottom = new FormAttachment(FORM_LAYOUT_FULL);
		cursorLine.setLayoutData(cursorLineData);
		
		cursorLine.setVisible(false);
		// Make sure the mouse cursor does not see this as a widget
		// and responds to MouseExit, etc. triggers when moving over this widget.
		cursorLine.setEnabled(false);
	}

	public void setVisible(final boolean visible) {
		cursorLabel.setVisible(visible);
		cursorSash.setVisible(visible);
		cursorLine.setVisible(visible);

		// Move above all other widgets
		// Done here and not in createContents
		// to assure that latest traceCursor that becomes
		// visible is always on top
		if (visible == true) {
			cursorLabel.moveAbove(null);
			cursorSash.moveAbove(null);
			cursorLine.moveAbove(null);
		}
	}
	
	public void setCursor(final int x) {
		int width = cursorLabel.getParent().getBounds().width;
		setPosition(x, width);
		cursorTime = zoom.getTimeAtPosition(x, width);
		setTimeLabel(cursorTime);
	}

	public double getTime() {
		return cursorTime;
	}
	
	private void setPosition(final int x, final int width) {
		((FormData)cursorSash.getLayoutData()).left = new FormAttachment(0, x);
		((FormData)cursorLine.getLayoutData()).left = new FormAttachment(0, x);
		
		// New text may change label width, recompute label size
		int labelWidth = cursorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
		// Move label to left of cursor line at the right edge of the screen
		int offset = x;
		if (x + labelWidth > width)
		{
			offset -= labelWidth;
		}
		((FormData)cursorLabel.getLayoutData()).left = new FormAttachment(0, offset);

		// layout(true) for the label to ensure the label of other markers
		// is redrawn when the mouse cursor moves ahead
		cursorLabel.getParent().layout(true);
		cursorLine.getParent().update();
		cursorLine.getParent().layout(false);
		cursorLabel.getParent().update();
	}
	
	protected void setTimeLabel(final double time) {
		String timeString = Times.timeToString(time, zoom.getTimeDisplayAccuracy());
		
		cursorLabel.setText(timeString);
		cursorLabel.setToolTipText(timeString);

		// Update label width with the new text
		// Must be called after layout
		cursorLabel.pack(false);
	}
	
	/**
	 * Called when the zoom or scroll changes to redraw the ruler.
	 * 
	 * @param o
	 *            the <code>Observable</code> calling the update
	 * @param data
	 *            has no effect
	 */
	public final void update(final Observable o, final Object data) {
		updatePositionAndLabel();
	}

	private void updatePositionAndLabel() {
		double startTime = zoom.getStartTime();
		double endTime = zoom.getEndTime();
		double timeRange = endTime - startTime;
		int width = cursorLabel.getParent().getBounds().width;
		int x = (int) ((cursorTime - startTime) * ((double)width) / timeRange);
		
		setPosition(x, width);
		setTimeLabel(cursorTime);
	}
}
