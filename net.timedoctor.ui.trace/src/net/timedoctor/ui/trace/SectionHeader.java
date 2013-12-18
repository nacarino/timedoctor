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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Custom implementation of an expand bar header 
 * based on a composite of a label and a
 * button for expand/collapse.
 */
public class SectionHeader extends Composite implements IExpandClient {
	 	 
	private static final int LABEL_HOR_INDENT = 5;

	private Label label;
	private Button expandButton;
	
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
	public SectionHeader(final Composite parent) {
		super(parent, SWT.NONE);
		createHeader(parent);
	}

	/**
	 * Gets the header height of the <code>SectionBar</code>.
	 * 
	 * @return the height of the header
	 */
	public final int getHeaderHeight() {
		return label.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
	}
	
	@Override
	public final void setBackground(final Color color) {
		super.setBackground(color);
		label.setBackground(color);
		expandButton.setForeground(color);
	}

	public final void setText(final String text) {
		label.setText(text);
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
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		setLayout(layout);
		
		label = new Label(this, SWT.NONE);
		GridData labelGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		labelGridData.horizontalIndent = LABEL_HOR_INDENT;
		label.setLayoutData(labelGridData);
		
		label.setForeground(getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		label.setFont(Fonts.getFontRegistry().getBold(Fonts.SECTION_HEADER_FONT));
				
		expandButton = new Button(this, SWT.ARROW);
		GridData buttonGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		buttonGridData.heightHint = getHeaderHeight();
		buttonGridData.widthHint = getHeaderHeight();
		expandButton.setLayoutData(buttonGridData);
		
		expandListener = new ExpandSyncListener();
		expandListener.addClient(this);
		label.addMouseListener(expandListener);
		expandButton.addSelectionListener(expandListener);
	}
	
	public void expand() {
		expandButton.setAlignment(SWT.UP);
	}
	
	public void collapse() {
		expandButton.setAlignment(SWT.DOWN);
	}
}
