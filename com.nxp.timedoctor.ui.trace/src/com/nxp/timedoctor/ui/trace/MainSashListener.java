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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * handle splitter between left and right trace view panes
 */
public class MainSashListener implements SelectionListener, MouseListener {

	private Composite leftPane;
	
	/**
	 * Constructs a MainSashListener with the given properties.
	 * 
	 * @param refControl
	 *            the reference control
	 * @param sashStyle
	 *            the orientation of the sash
	 * @param hasMinOffset
	 *            whether or not it has a minumum offset
	 */
	public MainSashListener(final Composite leftPane) {
		this.leftPane = leftPane;
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
		setSashOffset(e.x);
	}

	/**
	 * Sets the sash offset to the minimum offset value on a double click
	 * action.
	 * 
	 * @param e
	 *            <code>MouseEvent</code> containing details on the event
	 */
	public final void mouseDoubleClick(final MouseEvent e) {
		setSashOffset(getMinSashOffset());
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

	/**
	 * Returns the minimum sash offset from the left of the parent's client
	 * area.
	 * 
	 * @return the minimum sash offset in pixels
	 */
	public final int getMinSashOffset() {
		return leftPane.computeSize(SWT.DEFAULT, SWT.DEFAULT, false).x;
	}

	/**
	 * Sets the sash offset to the given value.
	 * 
	 * @param offset
	 *            the offset in pixels from the left of the parent's client area
	 */
	public final void setSashOffset(final int offset) {
		((GridData) leftPane.getLayoutData()).widthHint = offset;
		leftPane.getParent().layout(true);
		leftPane.getParent().update();
	}
}
