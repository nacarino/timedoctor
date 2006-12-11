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
public class TraceViewer {

	private static final int SASH_WIDTH = 2;

	/**
	 * The model containing this view's data.
	 */
	private TraceModel model;

	/**
	 * Model component containing zoom and scroll data.
	 */
	private ZoomModel zoomData;

	private MainSashListener sashListener;

	private Composite leftPane;

	private Composite rightPane;
	
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
		GridLayout parentLayout = new GridLayout(3, false);
		parentLayout.marginHeight = 0;
		parentLayout.marginWidth = 0;
		parentLayout.verticalSpacing = 0;
		parentLayout.horizontalSpacing = 0;
		parent.setLayout(parentLayout);

		TraceCursorFactory traceCursorFactory = new TraceCursorFactory(zoomData);
		
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

		HeaderViewer header = new HeaderViewer(leftPane, rightPane, traceCursorFactory, zoomData);
		MainViewer mainViewer = new MainViewer(leftPane, rightPane, traceCursorFactory, model, zoomData);
		
		sashListener = new MainSashListener(leftPane);
		mainSash.addSelectionListener(sashListener);
		mainSash.addMouseListener(sashListener);
	}
}
