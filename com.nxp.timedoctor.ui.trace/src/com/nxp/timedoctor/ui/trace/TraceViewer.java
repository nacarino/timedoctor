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

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;

/**
 * Contains and initializes the entire contents of the trace data viewer: a
 * header containing a logo and ruler, and a main, scrollable body within which
 * to view the data.
 */
public class TraceViewer {

	/**
	 * Constant for use in creation of <code>FormData</code> to signify the
	 * object reaches (not counting any offset) all the way to the right or
	 * bottom of the client area.
	 */
	private static final int FORMLAYOUT_FULL = 100;

	/**
	 * The height of the header in pixels.
	 */
	private static final int HEADER_HEIGHT = 50;

	/**
	 * The model containing this view's data.
	 */
	private TraceModel model;

	/**
	 * Model component containing zoom and scroll data.
	 */
	private ZoomModel zoomData;

	/**
	 * Creates the <code>TraceViewer</code> contents in the parent composite,
	 * using the model for data. So far, just uses dummy data, and model isn't
	 * passed beyond this step.
	 * 
	 * @param parent
	 *            the parent composite in which to construct the view
	 * @param model
	 *            the model containing data to display
	 * @param zoomData
	 *            <code>ZoomModel</code> object from which to retrieve zoom and
	 *            scroll data
	 */
	public TraceViewer(final Composite parent, final TraceModel model,
			final ZoomModel zoomData) {
		this.model = model;
		this.zoomData = zoomData;
		createContents(parent);
	}

	/**
	 * Creates the contents of the view within the parent composite. Handles all
	 * sub-initializations and will eventually pass the model along.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createContents(final Composite parent) {
		parent.setLayout(new FormLayout());

		HeaderViewer header = new HeaderViewer(parent, zoomData);
		FormData data = new FormData();
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(FORMLAYOUT_FULL);
		data.top = new FormAttachment(0);
		data.height = HEADER_HEIGHT;
		header.setLayoutData(data);

		MainViewer main = new MainViewer(parent, model, zoomData);
		data = new FormData();
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(FORMLAYOUT_FULL);
		data.top = new FormAttachment(header);
		data.bottom = new FormAttachment(FORMLAYOUT_FULL);
		main.setLayoutData(data);

		main.addSashClient(header);
		header.addSashClient(main);
	}
}
