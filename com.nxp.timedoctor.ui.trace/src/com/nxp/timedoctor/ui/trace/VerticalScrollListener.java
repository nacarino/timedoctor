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

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * Trace window vertical scrollbar listener
 */
public class VerticalScrollListener implements SelectionListener, ControlListener {

	private Composite leftPane;
	
	/**
	 * Constructs a MainSashListener with the given properties.
	 * 
	 * @param leftPane
	 *            the compositeon the left that must be synchronized
	 */
	public VerticalScrollListener(final Composite leftPane) {
		this.leftPane = leftPane;
	}

	public void widgetDefaultSelected(final SelectionEvent e) {
	}

	public void widgetSelected(final SelectionEvent e) {
		ScrollBar bar = ((ScrollBar) e.widget);
		setVerticalScroll(bar.getSelection());		
	}

	public void controlMoved(final ControlEvent e) {
	}

	public void controlResized(final ControlEvent e) {
		ScrollBar bar = ((ScrolledComposite) e.widget).getVerticalBar();
		int selection = 0;
		if (bar.getVisible()) {
			selection = bar.getSelection();
		}
		setVerticalScroll(selection);		
	}

	private void setVerticalScroll(final int selection) {
		((GridData) leftPane.getLayoutData()).verticalIndent = - selection;
		leftPane.getParent().layout(false);
	}
}
