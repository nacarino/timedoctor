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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Subclass of <code>SectionBar</code> designed to hold the labels containing
 * line names in a section.
 */
public class SectionLabelViewer extends SectionBar implements ISashClient {

	/**
	 * Composite to contain the labels.
	 */
	private Composite sectionLabel;

	/**
	 * Constructs a <code>SectionLabelViewer</code> with the given parent.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public SectionLabelViewer(final Composite parent) {
		super(parent);

		createContents(parent);
	}

	/**
	 * Get the composite that holds the trace labels.
	 * 
	 * @return the content composite
	 */
	public final Composite getContent() {
		return sectionLabel;
	}

	// Handle sash in the trace section

	/**
	 * Gets the minimum sash offset permissible for this composite, based upon
	 * the width of its children.
	 * 
	 * @return the minimum allowable sash offset
	 */
	public final int getMinSashOffset() {
		return sectionLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
	}

	/**
	 * Sets the sash offset of the sash below.
	 * 
	 * @param offset
	 *            the offset
	 */
	public final void setSashOffset(final int offset) {
		setContentHeight(offset);
	}

	/**
	 * Creates the contents, populating the composite from the model.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createContents(final Composite parent) {
		sectionLabel = new Composite(this, SWT.NONE);
	
		GridLayout sectionLabelLayout = new GridLayout(1, false);
		sectionLabelLayout.marginHeight = 0;
		sectionLabelLayout.marginWidth = 0;
		sectionLabelLayout.verticalSpacing = 0; // no spacing here to be able to
		// always accept drops on a
		// label and not on the padding
		sectionLabel.setLayout(sectionLabelLayout);
	
		setContent(sectionLabel);
	}	

	/**
	 * This method will relayout the section which contains the labels.
	 * 
	 */
	public void layoutSection() {
		this.getParent().layout(true);
		this.getParent().update();
	}
}
