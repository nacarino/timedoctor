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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * View containing the trace canvases.
 */
public class SectionTraceViewer implements IExpandClient {

	/**
	 * Constant for use in setting of form data to indicate the attachment's
	 * percentage is all the way right or bottom.
	 */
	private static final int FORMLAYOUT_FULL = 100;

	/**
	 * The section above.
	 */
	private SectionTraceViewer top;

	/**
	 * True if this is the last section, false otherwise.
	 */
	private boolean last;

	/**
	 * The section header.
	 */
	private Label header;

	/**
	 * This section's sash.
	 */
	private Sash sash;

	/**
	 * Composite to contain the trace canvases.
	 */
	private Composite sectionTrace;

	/**
	 * The height of the section header.
	 */
	private int headerHeight;

	/**
	 * <code>SashSyncListener</code> for synchronizing with a
	 * <code>SectionLabelViewer</code>.
	 */
	private SashSyncListener sashListener;

	/**
	 * The height of a single trace canvas.
	 */
	private int traceHeight = 0;

	/**
	 * Constructs a new <code>SectionTraceViewer</code>.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param topSection
	 *            the section above this one, or null if there is none
	 * @param lastSection
	 *            whether this is the last section
	 * @param sectionHeaderHeight
	 *            the section's header height
	 */
	public SectionTraceViewer(final Composite parent,
			final SectionTraceViewer topSection, final boolean lastSection,
			final int sectionHeaderHeight) {
		top = topSection;
		last = lastSection;
		headerHeight = sectionHeaderHeight;

		if (null == topSection) {
			createHeader(parent);
		}
		createContents(parent);
	}

	/**
	 * Gets the composite containing the trace canvases.
	 * 
	 * @return content composite
	 */
	public final Composite getContent() {
		return sectionTrace;
	}

	/**
	 * Adds a sash client to this <code>SectionTraceViewer</code>'s sash
	 * listener.
	 * 
	 * @param client
	 *            the client to be added
	 */
	public final void addSashClient(final ISashClient client) {
		sashListener.addClient(client);
	}

	/**
	 * Sets the header color.
	 * 
	 * @param color
	 *            the header color
	 */
	public final void setHeaderColor(final Color color) {
		if (header != null) {
			header.setBackground(color);
		} else {
			top.sash.setBackground(color);
		}
	}

	/**
	 * Expands this side of the section.
	 */
	public final void expand() {
		FormData data = (FormData) sectionTrace.getLayoutData();
		data.height = traceHeight;
		sash.setEnabled(true);
		sectionTrace.getParent().layout();
		updateVerticalScrollBar();

		updateLeftScroll();
	}

	/**
	 * Updates the manual scrolling of the left pane in the event of a sash move
	 * or section collapse.
	 */
	private void updateLeftScroll() {
		ScrolledComposite scroll = (ScrolledComposite) sectionTrace.getParent()
				.getParent();
		ScrollBar bar = scroll.getVerticalBar();
		int selection = bar.getSelection();
		Composite left = (Composite) scroll.getData();
		if (left != null) {
			((FormData) left.getLayoutData()).top = new FormAttachment(0,
					0 - selection);
			left.getParent().layout(false);
		}
	}

	/**
	 * Collapses this side of the section so only the header is visible.
	 */
	public final void collapse() {
		final FormData data = (FormData) sectionTrace.getLayoutData();
		traceHeight = data.height;
		data.height = 0;
		sash.setEnabled(false);
		sectionTrace.getParent().layout();
		updateVerticalScrollBar();

		updateLeftScroll();
	}

	/**
	 * Creates the contents of the right side of the section, laying out and
	 * populating trace lines.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	final void createContents(final Composite parent) {
		sectionTrace = new Composite(parent, SWT.NONE);
		GridLayout sectionTraceLayout = new GridLayout(1, false);
		sectionTraceLayout.marginHeight = 0;
		sectionTraceLayout.marginWidth = 0;
		sectionTraceLayout.verticalSpacing = 0;
		sectionTrace.setLayout(sectionTraceLayout);
		FormData data = new FormData();
		if (null != header) {
			data.top = new FormAttachment(header);
		} else {
			data.top = new FormAttachment(top.sash);
		}
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(FORMLAYOUT_FULL);
		sectionTrace.setLayoutData(data);
		sash = new Sash(parent, SWT.HORIZONTAL);
		data = new FormData();
		data.top = new FormAttachment(sectionTrace);
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(FORMLAYOUT_FULL);
		data.height = headerHeight;
		sash.setLayoutData(data);
		if (last) {
			Composite bottomComp = new Composite(parent, SWT.NONE);
			data = new FormData();
			data.top = new FormAttachment(sash);
			data.left = new FormAttachment(0);
			data.right = new FormAttachment(FORMLAYOUT_FULL);
			data.bottom = new FormAttachment(FORMLAYOUT_FULL);
			bottomComp.setLayoutData(data);
		}
		sash.setEnabled(true);

		ISashClient client = new ISashClient() {
			public int getMinSashOffset() {
				return sectionTrace.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			}

			public void setSashOffset(final int offset) {
				FormData traceData = (FormData) sectionTrace.getLayoutData();
				traceHeight = offset;
				traceData.height = offset;
				sectionTrace.getParent().layout();
				updateVerticalScrollBar();
				updateLeftScroll();
			}

			public void update() {	
			}
		};
		sashListener = new SashSyncListener(client, sectionTrace, SWT.HORIZONTAL, true);

		sash.addSelectionListener(sashListener);
		sash.addMouseListener(sashListener);
	}

	/**
	 * Creates the section's header.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createHeader(final Composite parent) {
		header = new Label(parent, SWT.NONE);
		FormData headerData = new FormData();
		headerData.top = new FormAttachment(0);
		headerData.left = new FormAttachment(0);
		headerData.right = new FormAttachment(FORMLAYOUT_FULL);
		headerData.height = headerHeight;
		header.setLayoutData(headerData);
	}

	/**
	 * Updates the vertical scrollbar to take into account any expand/collapse
	 * events.
	 * 
	 */
	// TODO it would be nice to move this to mainView as client of the
	// sash/expandbar somehow
	private void updateVerticalScrollBar() {
		// Update scrolling
		Composite rightPane = sash.getParent();
		ScrolledComposite rightScroll = (ScrolledComposite) rightPane
				.getParent();
		int height = rightPane.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		rightScroll.setMinHeight(height);
	}
}
