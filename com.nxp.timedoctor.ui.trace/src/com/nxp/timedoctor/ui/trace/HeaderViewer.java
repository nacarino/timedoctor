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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;

import com.nxp.timedoctor.core.model.ZoomModel;

/**
 * Header view containing a logo canvas (for eventual product logo as well as
 * spacing) and a ruler, with a disabled sash between them to synchronize
 * position with the sash in the lower pane.
 */
public class HeaderViewer extends Composite implements ISashClient, Observer {

	/**
	 * Constant for use in form layouts, to indicate that a given attachment is
	 * to be at 100% of the parent's client area.
	 */
	private static final int FORMLAYOUT_FULL = 100;

	/**
	 * The logo composite, for spacing in the header (and eventual presence of a
	 * logo).
	 */
	private Composite logo;

	/**
	 * The header sash, for synchronization with lower pane's sash.
	 */
	private Sash headerSash;

	/**
	 * The sash listener to synchronize the two sashes.
	 */
	private SashSyncListener sashListener;

	/**
	 * The ruler canvas.
	 */
	private Canvas ruler;

	/**
	 * Constructs a header view in the given parent, and creates the contents of
	 * the header.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param data
	 *            <code>Observable</code> containing zoom and scroll data
	 */
	public HeaderViewer(final Composite parent, final ZoomModel data) {
		super(parent, SWT.NONE);

		setLayout(new FormLayout());
		createContents(parent, data);

		data.addObserver(this);
	}

	/**
	 * Creates and lays out the header contents.
	 * 
	 * @param parent
	 *            the composite in which the header is situated.
	 * @param zoomData
	 *            <code>Observable</code> containing current zoom and scroll
	 *            data
	 */
	private void createContents(final Composite parent,
			final ZoomModel zoomData) {
		logo = new Canvas(this, SWT.NONE);
		FormData data = new FormData();
		data.left = new FormAttachment(0);
		data.top = new FormAttachment(0);
		data.bottom = new FormAttachment(FORMLAYOUT_FULL);
		logo.setLayoutData(data);

		headerSash = new Sash(this, SWT.VERTICAL);
		data = new FormData();
		data.left = new FormAttachment(logo);
		data.top = new FormAttachment(0);
		data.bottom = new FormAttachment(FORMLAYOUT_FULL);
		headerSash.setLayoutData(data);

		sashListener = new SashSyncListener(null, SWT.VERTICAL, false);
		headerSash.addSelectionListener(sashListener);
		headerSash.addMouseListener(sashListener);
		sashListener.addClient(this);

		// Ruler pane
		ruler = new Canvas(this, SWT.DOUBLE_BUFFERED);
		data = new FormData();
		data.left = new FormAttachment(headerSash);
		data.right = new FormAttachment(FORMLAYOUT_FULL);
		data.top = new FormAttachment(0);
		data.bottom = new FormAttachment(FORMLAYOUT_FULL);
		ruler.setLayoutData(data);

		// Paint ruler
		RulerPaintListener rulerPaintListener =
			new RulerPaintListener(zoomData);
		ruler.addPaintListener(rulerPaintListener);
	}

	// Handle sash between left and right panes

	/**
	 * Adds the given SashClient to its sash listener for synchronization.
	 * 
	 * @param client
	 *            the client to be added
	 */
	public final void addSashClient(final ISashClient client) {
		sashListener.addClient(client);
	}

	/**
	 * Returns the current size of the logo composite, which determines the
	 * sash's minimum x-position.
	 * 
	 * @return the minimum sash offset
	 */
	public final int getMinSashOffset() {
		return logo.computeSize(SWT.DEFAULT, SWT.DEFAULT, false).x;
	}

	/**
	 * Sets the sash offset.
	 * 
	 * @param offset
	 *            sets the sash offset from the left of the client area.
	 */
	public final void setSashOffset(final int offset) {
		((FormData) logo.getLayoutData()).width = offset;
		this.layout();
		this.update();
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
		ruler.redraw();
		ruler.update();
	}
}
