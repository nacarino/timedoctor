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

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;

import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.ZoomModel;

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
	
	private MainViewer mainViewer;

	private HeaderViewer headerViewer;
	
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
	
	public void dispose() {
		mainViewer.dispose();
		mainViewer = null;
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

		headerViewer = new HeaderViewer(leftPane, rightPane, traceCursorFactory, zoomModel);
		mainViewer = new MainViewer(leftPane, rightPane, traceCursorFactory, traceModel, zoomModel);
		
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
		GridData leftPaneGridData = (GridData) leftPane.getLayoutData();
		
		int maxWidth = leftPane.computeSize(SWT.DEFAULT, SWT.DEFAULT, false).x;
		int newWidth = Math.min(width, maxWidth);
		
		if (leftPaneGridData.widthHint != newWidth) {
			leftPaneGridData.widthHint = newWidth;
		
			// Update complete editor pane (left and right)
			leftPane.getParent().layout(true);		
			leftPane.getParent().update();
		}
		return (width <= maxWidth);
	}
	
	/**
	 * Returns the {@link ISelectionProvider} 
	 * 
	 * @return The {@link ISelectionProvider} object
	 */
	public ISelectionProvider getSelectionProvider() {
		return mainViewer;
	}

	/**
	 * Returns an {@link Image} containing the screenshot of the current visible
	 * portion
	 * 
	 * @return
	 * 			The {@link Image} screenshot. The image resource must be disposed by the caller.
	 */
	public Image getScreenShot() {
		final Image topImage = headerViewer.getScreenShot();
		final Image bottomImage = mainViewer.getScreenShot();
		
		// Merge the two
		final Rectangle topRect = topImage.getBounds();
		final Rectangle bottomRect = bottomImage.getBounds();
		
	    final Image mergedImage = new Image(leftPane.getDisplay(), Math.min(topRect.width, bottomRect.width), topRect.height + bottomRect.height);
	    
	    final GC mergedGc = new GC(mergedImage);
	    mergedGc.drawImage(topImage, 0, 0);
	    mergedGc.drawImage(bottomImage, 0, topRect.height);
	    
	    topImage.dispose();
	    bottomImage.dispose();
	    
	    mergedGc.dispose();
	    
		return mergedImage; //Should be disposed by the caller
	}
}
