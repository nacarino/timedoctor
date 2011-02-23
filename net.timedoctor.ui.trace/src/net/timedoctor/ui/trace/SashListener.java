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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * handle splitter between left and right trace view panes
 */
public class SashListener implements SelectionListener, MouseListener {

	private ISashClient client;
	private int style;
	
	/**
	 * Constructs a SashListener with the given properties.
	 * 
	 * @param client
	 *            the <code>ISashClient</code> that implements the 
	 *            updates upon sash actions
	 * @param style SWT.HORIZONTAL or SWT.VERTICAL
	 * 			
	 */
	public SashListener(final ISashClient client, final int style) {
		this.client = client;
		this.style = style;
	}

	/**
	 * Empty method to satisfy interface.
	 * 
	 * @param e
	 *            SelectionEvent
	 */
	public final void widgetDefaultSelected(final SelectionEvent e) {
	}

	/**
	 * Method in which the relocation of the sash is handled, using either the
	 * current cursor position or the minimum offset value.
	 * 
	 * @param e
	 *            SelectionEvent containing details on the sash's location
	 */
	public final void widgetSelected(final SelectionEvent e) {
		// Ensure that select action does not execute upon double-click in linux
		if (e.detail == SWT.DRAG) {
			int offset = (style == SWT.VERTICAL) ? e.x : e.y;
			if (!client.setSashOffset(offset)) {
				e.doit = false;
			}
		}
	}

	/**
	 * Sets the sash offset to the minimum offset value on a double click
	 * action.
	 * 
	 * @param e
	 *            <code>MouseEvent</code> containing details on the event
	 */
	public final void mouseDoubleClick(final MouseEvent e) {
		client.setDefaultSashOffset();
	}

	/**
	 * Empty method to satisfy interface.
	 * 
	 * @param e
	 *            <code>MouseEvent</code>
	 */
	public final void mouseDown(final MouseEvent e) {
	}

	/**
	 * Empty method to satisfy interface.
	 * 
	 * @param e
	 *            <code>MouseEvent</code>
	 */
	public final void mouseUp(final MouseEvent e) {
	}
}
