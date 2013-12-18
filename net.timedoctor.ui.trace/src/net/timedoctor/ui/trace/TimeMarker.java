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
package net.timedoctor.ui.trace;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
public class TimeMarker extends TimeLine {
	private static final int FLAG_WIDTH = 8;
	private static final int OFFSET = 11;
	
	public TimeMarker(final Composite rulerPane, 
			final Composite tracePane, 
			final ZoomModel zoom) {
		super(rulerPane, tracePane, zoom, FLAG_WIDTH, SWT.COLOR_DARK_GRAY, OFFSET);
		
		addSelectionListener();
		addMouseListener();
	}

	private void addSelectionListener() {
		SelectionListener selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.DRAG) {
					setCursor(e.x);
				}
			}
		};
		cursorSash.addSelectionListener(selectionListener);
	}
	
	private void addMouseListener() {
		MouseListener mouseListener = new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				dispose();
			}
		};
		cursorSash.addMouseListener(mouseListener);
	}
}
