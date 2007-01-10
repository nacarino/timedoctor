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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;

import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;

/**
 * Contains and initializes the entire contents of the trace data viewer: a
 * header containing a logo and ruler, and a main, scrollable body within which
 * to view the data.
 */
public class TraceViewer implements ISashClient {

	private static final int SASH_WIDTH = 2;

	private TraceModel traceModel;

	private ZoomModel zoomModel;

	private Composite leftPane;

	private Composite rightPane;

	/**
	 * Creates the <code>TraceViewer</code> contents in the parent composite,
	 * using the traceModel for data. So far, just uses dummy data, and traceModel isn't
	 * passed beyond this step.
	 * 
	 * @param parent
	 *            the parent composite in which to construct the view
	 * @param traceModel
	 *            the traceModel containing data to display
	 * @param zoomModel
	 *            <code>ZoomModel</code> object from which to retrieve zoom and
	 *            scroll data
	 */
	public TraceViewer(final Composite parent, final TraceModel model,
			final ZoomModel zoomData) {
		this.traceModel = model;
		this.zoomModel = zoomData;
		
		createContents(parent);
	}

	/**
	 * Creates the contents of the view within the parent composite. Handles all
	 * sub-initializations and will eventually pass the traceModel along.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createContents(final Composite parent) {
		GridLayout parentLayout = new GridLayout(3, false);
		parentLayout.marginHeight = 0;
		parentLayout.marginWidth = 0;
		parentLayout.verticalSpacing = 0;
		parentLayout.horizontalSpacing = 0;
		parent.setLayout(parentLayout);

		leftPane = new Composite(parent, SWT.NONE);
		GridLayout leftLayout = new GridLayout(1, false);
		leftLayout.marginHeight = 0;
		leftLayout.marginWidth = 0;
		leftLayout.verticalSpacing = 0;
		leftPane.setLayout(leftLayout);
		
		leftPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

		Sash mainSash = new Sash(parent, SWT.VERTICAL);
		GridData sashGridData = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		sashGridData.widthHint = SASH_WIDTH;
		mainSash.setLayoutData(sashGridData);
				
		rightPane = new Composite(parent, SWT.NONE);
		GridLayout rightLayout = new GridLayout(1, false);
		rightLayout.marginHeight = 0;
		rightLayout.marginWidth = 0;
		rightLayout.verticalSpacing = 0;		
		rightPane.setLayout(rightLayout);

		rightPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TraceCursorFactory traceCursorFactory = new TraceCursorFactory(zoomModel);

		new HeaderViewer(leftPane, rightPane, traceCursorFactory, zoomModel);
		new MainViewer(leftPane, rightPane, traceCursorFactory, traceModel, zoomModel);
		
		SashListener sashListener = new SashListener(this, SWT.VERTICAL);
		mainSash.addSelectionListener(sashListener);
		mainSash.addMouseListener(sashListener);
		
		// Set initial width of the leftPane
		setDefaultSashOffset();
	}
	
	public final void setDefaultSashOffset() {
		int labelOffset = leftPane.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
		setSashOffset(labelOffset);
	}

	/**
	 * Sets the sash offset to the given value.
	 * 
	 * @param width
	 *            the offset in pixels from the left of the parent's client area
	 */
	public final boolean setSashOffset(final int width) {
		int minWidth = leftPane.computeSize(SWT.DEFAULT, SWT.DEFAULT, false).x;
		int newWidth = Math.min(width, minWidth);
		((GridData) leftPane.getLayoutData()).widthHint = newWidth;
		leftPane.getParent().layout(true);
		leftPane.getParent().update();
		return (width <= minWidth);
	}	
}
