/*******************************************************************************
 * Copyright (c) 2006-2012 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.ui.trace;

import java.util.Observable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import net.timedoctor.core.model.ZoomModel;

/**
 * Draw a marker line and time label 
 * as composites above all other
 * widgets. 
 */
public class TimeBaseLine extends TimeLine {
	private static final int FLAG_WIDTH = 8;
	private static final int OFFSET = 11;

	public TimeBaseLine(final Composite rulerPane, 
			final Composite tracePane, 
			final ZoomModel zoom) {
		super(rulerPane, tracePane, zoom, FLAG_WIDTH, SWT.COLOR_GREEN, OFFSET);
		
		addSelectionListener();
	}

	/**
	 * Called when the zoom or scroll changes to redraw the ruler.
	 * Override from the TimeLine implementation to ensure this class
	 * uses the selectTime stored in the model.
	 * (needed for goToTime, Next, Prev, etc.)
	 * 
	 * @param o
	 *            the <code>Observable</code> calling the update
	 * @param data
	 *            has no effect
	 */
	@Override
	public void update(final Observable o, final Object data) {
		updatePositionAndLabel(zoom.getSelectTime());
	}

	private void addSelectionListener() {
		SelectionListener selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setCursor(e.x);
			}
		};
		cursorSash.addSelectionListener(selectionListener);
	}
}
