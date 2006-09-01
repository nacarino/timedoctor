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

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;

/**
 * Synchronize multiple sashes. Also reduce the sash to the minimum offset upon
 * double click
 */
public class SashSyncListener implements SelectionListener, MouseListener {

	/**
	 * Reference control fro calculation of current and minimum sash positions.
	 */
	private Control reference;

	/**
	 * The style of sash (<code>SWT.VERTICAL</code> or
	 * <code>SWT.HORIZONTAL</code>), to determine which piece of layout data
	 * is set.
	 */
	private int style;

	/**
	 * ArrayList to keep track of this listener's clients.
	 */
	// Checkstyle incompatible with J2SE5 type parameterization
	private ArrayList<ISashClient> clients;

	/**
	 * Boolean indicating whether or not this sash has a minimum offset.
	 */
	private boolean constrained;

	/**
	 * Constructs a SashSyncListener with the given properties.
	 * 
	 * @param refControl
	 *            the reference control
	 * @param sashStyle
	 *            the orientation of the sash
	 * @param hasMinOffset
	 *            whether or not it has a minumum offset
	 */
	public SashSyncListener(final Control refControl, final int sashStyle,
			final boolean hasMinOffset) {
		reference = refControl;
		style = sashStyle;
		constrained = hasMinOffset;

		// Checkstyle incompatible with J2SE5 type parameterization.
		clients = new ArrayList<ISashClient>();
	}

	/**
	 * Adds a client to this listener.
	 * 
	 * @param client
	 *            the client to be added
	 */
	public final void addClient(final ISashClient client) {
		clients.add(client);
		setSashOffset(getMinSashOffset());
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
		int sashLocation;
		int referenceLocation = 0;
		if (SWT.VERTICAL == style) {
			sashLocation = e.x;
			if (null != reference) {
				referenceLocation = reference.getLocation().x;
			}
		} else {
			sashLocation = e.y;
			if (null != reference) {
				referenceLocation = reference.getLocation().y;
			}
		}
		int offset = sashLocation - referenceLocation;

		if (constrained) {
			int minOffset = getMinSashOffset();
			if (offset < minOffset) {
				// Restrict the sash to the minimum height
				if (SWT.VERTICAL == style) {
					e.x = minOffset + referenceLocation;
				} else {
					e.y = minOffset + referenceLocation;
				}
				offset = minOffset;
			}
		}
		setSashOffset(offset);
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
	 * Sets the sash offset to the given pixel value.
	 * 
	 * @param offset
	 *            the new offset in pixels
	 */
	private void setSashOffset(final int offset) {

		// if (offset != currentOffset) { // only update if needed to avoid
		// flickering sash when it cannot move
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).setSashOffset(offset);
		}
		// }
	}

	/**
	 * Returns the minimum sash offset based upon those of its clients.
	 * 
	 * @return the minimum sash offset
	 */
	private int getMinSashOffset() {
		int maxOffset = 0;
		for (int i = 0; i < clients.size(); i++) {
			int offset = clients.get(i).getMinSashOffset();
			if (offset > maxOffset) {
				maxOffset = offset;
			}
		}
		return maxOffset;
	}
}
