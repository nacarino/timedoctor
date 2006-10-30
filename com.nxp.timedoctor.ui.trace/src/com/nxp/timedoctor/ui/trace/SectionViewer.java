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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.Section;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;

/**
 * An instance of this class holds a number of trace lines of the same type
 * (tasks, ISRs, Queues, etc.) The section has a header at the top and a
 * splitter at the bottom to resize the height of the section.
 */
public class SectionViewer {

	/**
	 * Constant for use in form layouts to indicate an element's
	 * <code>FormData</code> goes all the way to the bottom or right of the
	 * client area.
	 */
	private static final int FORMLAYOUT_FULL = 100;

	/**
	 * The section in the model with which this view is associated.
	 */
	private Section section;

	/**
	 * The section above this one.
	 */
	private SectionViewer top;

	/**
	 * True iff this is the last section.
	 */
	private boolean last;

	/**
	 * Holds the labels for the trace lines.
	 */
	private SectionLabelViewer sectionLabel;

	/**
	 * Holds the lines themselves.
	 */
	private SectionTraceViewer sectionTrace;

	/**
	 * Model component containing current zoom/scroll information.
	 */
	private ZoomModel zoomData;

	/**
	 * Constructs a new SectionViewer, and creates and lays out the trace lines.
	 * Handles setting the label and canvas parts to synchronize expand/collapse
	 * and sash events.
	 * 
	 * @param leftPane
	 *            the left pane of the view, where the labels will be placed
	 * @param rightPane
	 *            the right pane of the view, where the trace canvases will be
	 *            placed
	 * @param topSection
	 *            the section above this one
	 * @param lastSection
	 *            whether this is the last section
	 * @param section
	 *            the section from which to get data
	 * @param zoomData
	 *            <code>Observable</code> containing current zoom/scroll data
	 * @param model
	 *            the model containing data for the whole trace
	 * @param listeners
	 * 			  collection of (mouse) listeners for the trace lines
	 */
	public SectionViewer(final Composite leftPane, 
			final Composite rightPane,
			final SectionViewer topSection, 
			final boolean lastSection,
			final Section section, 
			final ZoomModel zoomData, 
			final TraceModel model, 
			TraceListeners listeners) {
		this.top = topSection;
		this.last = lastSection;
		this.section = section;
		this.zoomData = zoomData;

		createLabelView(leftPane);
		createTraceView(rightPane);

		createTraceLines(sectionLabel.getContent(), sectionTrace.getContent(),
				model, listeners);

		sectionTrace.addSashClient(sectionLabel);
		sectionLabel.addExpandClient(sectionTrace);
	}

	/**
	 * Sets the section's header color to the given color.
	 * 
	 * @param color
	 *            the header color
	 */
	public final void setHeaderColor(final Color color) {
		sectionLabel.setHeaderColor(color);
		sectionTrace.setHeaderColor(color);
	}

	/**
	 * Sets the section title to the given string.
	 * 
	 * @param text
	 *            the section title to be displayed in the header
	 */
	public final void setHeaderText(final String text) {
		sectionLabel.setHeaderText(text);
	}

	/**
	 * Sets whether or not this is the last section.
	 * 
	 * @param lastSection
	 *            section true if this is the last section, false otherwise
	 */
	public final void setLast(final boolean lastSection) {
		this.last = lastSection;
	}

	/**
	 * Creates the label view for this section, handling the layout. The label
	 * view is populated in the creation of trace lines.
	 * 
	 * @param parent
	 *            the left pane of the window
	 */
	private void createLabelView(final Composite parent) {
		sectionLabel = new SectionLabelViewer(parent);

		FormData data = new FormData();
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(FORMLAYOUT_FULL);
		if (null == top) {
			data.top = new FormAttachment(0);
		} else {
			data.top = new FormAttachment(top.sectionLabel);
		}
		sectionLabel.setLayoutData(data);
	}

	/**
	 * Creates and lays out the trace view. The view is populated in the
	 * creation of trace lines.
	 * 
	 * @param parent
	 *            the right pane of the window
	 */
	private void createTraceView(final Composite parent) {
		SectionTraceViewer topTraces = null;
		if (top != null) {
			topTraces = top.sectionTrace;
		}
		sectionTrace = new SectionTraceViewer(parent, topTraces, last,
				sectionLabel.getHeaderHeight());
	}

	/**
	 * Creates the trace lines from the model (eventually; currently uses dummy
	 * data), populating the label and trace views in the process.
	 * 
	 * @param labels
	 *            the label view to populate
	 * @param traces
	 *            the trace view to populate
	 * @param model
	 *            the model containing data on the whole trace
	 * @param listeners
	 * 			  collection of (mouse) listeners for the trace lines
	 */
	private void createTraceLines(final Composite labels,
			final Composite traces, 
			final TraceModel model, 
			TraceListeners listeners) {
		TraceLineViewer traceLine = null;
		for (SampleLine line : section.getLines()) {				
			if (line.getCount() > 1) {
                traceLine = new TraceLineViewer(traceLine, labels, traces,
                        line, zoomData, model, listeners);
            }
		}
	}
}
