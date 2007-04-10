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

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
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
public class SectionViewer implements IExpandClient{

	private TraceModel traceModel;
	private SectionHeader sectionHeader;
	private ZoomModel zoomModel;
	private HashMap<SampleLine, TraceLineViewer> traceLineViewerMap = new HashMap<SampleLine, TraceLineViewer>();
	private boolean isExpanded = true;
	private Composite traceHeader;
	private MainViewer mainViewer;
	
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
	 * @param zoomModel
	 *            <code>Observable</code> containing current zoom/scroll data
	 * @param traceModel
	 *            the model containing data for the whole trace
	 */
	public SectionViewer(final MainViewer mainViewer,
			final Composite leftPane, 
			final Composite rightPane,
			final ZoomModel zoomModel, 
			final TraceModel traceModel) {
		this.mainViewer = mainViewer;
		this.traceModel = traceModel;
		this.zoomModel = zoomModel;
				
		createLabelView(leftPane);
		createTraceView(rightPane);
		
		// Top separator in the section
		TraceLineSeparator separator = new TraceLineSeparator(this, leftPane, rightPane);
		separator.setBackground(rightPane.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		sectionHeader.addExpandClient(this);
	}

	/**
	 * Sets the section's header color to the given color.
	 * 
	 * @param color
	 *            the header color
	 */
	public final void setHeaderColor(final Color color) {
		sectionHeader.setBackground(color);
		traceHeader.setBackground(color);
	}

	/**
	 * Sets the section title to the given string.
	 * 
	 * @param text
	 *            the section title to be displayed in the header
	 */
	public final void setHeaderText(final String text) {
		sectionHeader.setText(text);
	}

	/**
	 * Creates the label view for this section, handling the layout. The label
	 * view is populated in the creation of trace lines.
	 * 
	 * @param parent
	 *            the left pane of the window
	 */
	private void createLabelView(final Composite parent) {
		sectionHeader = new SectionHeader(parent);
		
		GridData headerGridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		sectionHeader.setLayoutData(headerGridData);
	}

	/**
	 * Creates and lays out the trace view. The view is populated in the
	 * creation of trace lines.
	 * 
	 * @param parent
	 *            the right pane of the window
	 */
	private void createTraceView(final Composite parent) {
		traceHeader = new Composite(parent, SWT.NONE);
		GridData headerGridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		headerGridData.heightHint = sectionHeader.getHeaderHeight();
		traceHeader.setLayoutData(headerGridData);
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
	 */
	public void createTraceLines(final Composite labelPane,
			final Composite tracePane, 
			final Section section, 
			final TraceCursorListener traceCursorListener) {
				
		for (SampleLine line : section.getLines()) {			
			new TraceLineViewer(this,					
					labelPane, 
					tracePane,
					line,
					zoomModel, 
					traceModel, 
					traceCursorListener);
		}
	}
	
	public void addTraceLineViewer(final TraceLineViewer traceLineViewer) {
		traceLineViewerMap.put(traceLineViewer.getLine(), traceLineViewer);
	}

	public void removeTraceLineViewer(final TraceLineViewer traceLineViewer) {
		traceLineViewerMap.remove(traceLineViewer.getLine());
	}

	/**
	 * Collapse the section by hiding all trace lines 
	 */
	public void collapse() {
		isExpanded = false;
		
		for (TraceLineViewer traceLineViewer : traceLineViewerMap.values()) {
			traceLineViewer.setVisible(isExpanded);
		}
		layout();
	}

	/**
	 * Expand the section by showing all trace lines of interest
	 */
	public void expand() {
		isExpanded = true;
		updateAutoHide();
		layout();
	}
	
	public void updateAutoHide() {
		if (isExpanded) {
			for (TraceLineViewer traceLineViewer : traceLineViewerMap.values()) {
				traceLineViewer.updateAutoHide();
			}
		}
	}
	
	public void layout() {
		mainViewer.layout();
	}
	
	/**
	 * Selects or deselects a line
	 * 
	 * @param line The {@link SampleLine} to select or deselect
	 * @param select true to select, false to deselect
	 */
	public void selectLine(final SampleLine line, boolean select) {
		traceLineViewerMap.get(line).selectLine(select);
	}
}