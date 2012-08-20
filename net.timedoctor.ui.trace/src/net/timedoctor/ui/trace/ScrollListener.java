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

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * Scrollbar listener, calls client with the IScrollClient interface
 */
public class ScrollListener implements SelectionListener, ControlListener {

	private IScrollClient client;
	
	/**
	 * Constructs a SashListener with the given properties.
	 * 
	 * @param leftPane
	 *            the compositeon the left that must be synchronized
	 */
	public ScrollListener(final IScrollClient client) {
		this.client = client;
	}

	public void widgetDefaultSelected(final SelectionEvent e) {
	}

	public void widgetSelected(final SelectionEvent e) {
		ScrollBar bar = ((ScrollBar) e.widget);
		client.setScroll(bar.getSelection());
	}

	public void controlMoved(final ControlEvent e) {
	}

	public void controlResized(final ControlEvent e) {
		ScrollBar bar = ((ScrolledComposite) e.widget).getVerticalBar();
		int selection = 0;
		
		if (bar.getVisible()) {
			selection = bar.getSelection();
			
			final int height = ((ScrolledComposite)e.widget).getBounds().height;
			
			// Page increment set to the height of the client area
			// Single-scroll increment is set to height/10
			bar.setIncrement(height/10); 
			bar.setPageIncrement(height);
		}
		
		client.setScroll(selection);		
	}
}
