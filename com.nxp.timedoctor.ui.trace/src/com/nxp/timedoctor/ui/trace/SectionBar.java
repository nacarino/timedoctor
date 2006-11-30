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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Custom implementation of an expand bar based on a composite of a label and a
 * button for expand/collapse. The contents are collapsed by setting the height
 * of the composite to zero in the FormLayout
 */
public class SectionBar extends Composite {

	/**
	 * Constant for the creation of form attachments, to indicate the given
	 * attachment goes to 100% right or bottom of the parent.
	 */
	private static final int FORMLAYOUT_FULL = 100;

	/**
	 * Sets the font size for the header's label.
	 */
	private static final int HEADER_FONT_SIZE = 8;

	/**
	 * The header of the expand bar.
	 */
	private Composite header;

	/**
	 * The expand bar's label.
	 */
	private Label headerLabel;

	/**
	 * The composite containing the content of the <code>SectionBar</code>.
	 */
	private Composite content;

	/**
	 * The height of the content.
	 */
	private int contentHeight;

	/**
	 * <code>ExpandSyncListener</code> to synchronize collapse/expand events
	 * with other widgets.
	 */
	private ExpandSyncListener expandListener;

	/**
	 * Constructs an expand bar with the given parent and creates the header.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public SectionBar(final Composite parent) {
		super(parent, SWT.NONE);
		createHeader(parent);
	}

	/**
	 * Sets the content of the section bar.
	 * 
	 * @param contentComposite
	 *            the composite containing the content
	 */
	public final void setContent(final Composite contentComposite) {
		content = contentComposite;
		FormData data = new FormData();
		data.top = new FormAttachment(header);
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(FORMLAYOUT_FULL);
		contentComposite.setLayoutData(data);
	}

	/**
	 * Sets the header text to the given string.
	 * 
	 * @param text
	 *            the text to be displayed in the header
	 */
	public final void setHeaderText(final String text) {
		headerLabel.setText(text);
	}

	/**
	 * Sets the header color.
	 * 
	 * @param color
	 *            the color of the header
	 */
	public final void setHeaderColor(final Color color) {
		headerLabel.setBackground(color);
		headerLabel.setForeground(headerLabel.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
	}

	/**
	 * Gets the header height of the <code>SectionBar</code>.
	 * 
	 * @return the height of the header
	 */
	public final int getHeaderHeight() {
		return header.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
	}

	/**
	 * Gets the height of the content.
	 * 
	 * @return the height of the content
	 */
	public final int getContentHeight() {
		return contentHeight;
	}

	/**
	 * Sets the height of the content composite.
	 * 
	 * @param height
	 *            the new height
	 */
	public final void setContentHeight(final int height) {
		((FormData) content.getLayoutData()).height = height;
		contentHeight = height;
		this.getParent().layout();
		this.getParent().update();
	}

	/**
	 * Add a listener to expand/collapse events. The listener must implement the
	 * ExpandClient interface.
	 * 
	 * @param client
	 *            The class to be called upon expand/collapse events
	 */
	public final void addExpandClient(final IExpandClient client) {
		expandListener.addClient(client);
	}

	/**
	 * Creates the <code>SectionBar</code>'s header, with a label and
	 * expand/collapse button.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createHeader(final Composite parent) {
		setLayout(new FormLayout());
	
		header = new Composite(this, SWT.NONE);
		header
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1,
						1));
	
		FormData data = new FormData();
		data.top = new FormAttachment(0);
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(FORMLAYOUT_FULL);
		header.setLayoutData(data);
	
		GridLayout headerLayout = new GridLayout(2, false);
		headerLayout.verticalSpacing = 0;
		headerLayout.horizontalSpacing = 0;
		headerLayout.marginWidth = 0;
		headerLayout.marginHeight = 0;
		header.setLayout(headerLayout);
	
		headerLabel = new Label(header, SWT.LEFT);
		headerLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		headerLabel.setFont(new Font(headerLabel.getDisplay(), "Arial",
				HEADER_FONT_SIZE, SWT.BOLD));
	
		Button expandButton = new Button(header, SWT.ARROW | SWT.FLAT);
		expandButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
	
		expandListener = new ExpandSyncListener();
		expandButton.addSelectionListener(expandListener);
		expandListener.addClient(new IExpandClient() {
			public void collapse() {
				FormData data = (FormData) content.getLayoutData();
				contentHeight = data.height;
				data.height = 0;
				getParent().layout();
			}
	
			public void expand() {
				((FormData) content.getLayoutData()).height = contentHeight;
				getParent().layout();
			}
		});
	}
	
	/**
	 * This method will adjust the height of the section which contains the labels. 
	 * 
	 * @param height
	 *            The amount by which the height of the Section must be changed.	 
	 */
	public void updateHeight(final int height) {
		contentHeight += height;
		((FormData) content.getLayoutData()).height = contentHeight;
	}
}
