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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.ZoomModel;
import net.timedoctor.ui.trace.canvases.TraceCanvas;

/**
 * Creates the label and trace to view a <code>SampleLine</code>, and places
 * them in the right composites in the gui, retaining symbolic links for
 * organizational purposes.
 */
public class TraceLineViewer implements ISashClient {
	private TraceLineSeparator traceLineSeparator;
	
	/**
	 * The label containing the name of the line.
	 */
	private CLabel label;

	/**
	 * Canvas of the sample line associated with the label.
	 */
	private TraceCanvas trace;

	/**
	 * Model, to be passed on to canvases for use in computing the full trace
	 * width.
	 */
	private TraceModel model;

	/**
	 * The sample line for which this provides a view.
	 */
	private SampleLine line;

	/**
	 * The model component containing zoom/scroll values
	 */
	private ZoomModel zoom;

	private boolean isVisible = true;
	
	private SectionViewer sectionViewer;

	private IPreferenceStore preferenceStore;
	
	/**
	 * Constructs a new TraceLineViewer.
	 * 
	 * @param topLine
	 *            the trace line above this line, <code>null</code> if this
	 *            line is the first in the section.
	 * @param labelPane
	 *            the labels composite
	 * @param tracePane
	 *            the traces composite
	 * @param sampleLine
	 *            the sample line containing data for this line
	 * @param zoomData
	 *            the observable model part containing zoom/scroll data
	 * @param model
	 *            model containing data on the whole trace
	 */
	public TraceLineViewer(final SectionViewer sectionViewer,
			final Composite labelPane,
			final Composite tracePane,
			final SampleLine sampleLine, 
			final ZoomModel zoomData,
			final TraceModel model, 
			final TraceCursorListener traceCursorListener) {

		this.sectionViewer = sectionViewer;
		this.line = sampleLine;
		this.zoom = zoomData;
		this.model = model;
		
		sectionViewer.addTraceLineViewer(this);
		
		createLabel(labelPane);		
		createTrace(tracePane, traceCursorListener);
		
		traceLineSeparator = new TraceLineSeparator(sectionViewer, labelPane, tracePane);
		
		SashListener sashListener = new SashListener(this, SWT.HORIZONTAL);
		traceLineSeparator.addSelectionListener(sashListener);
		traceLineSeparator.addMouseListener(sashListener);
		
		setupReordering();
				
		setMinHeight();
		preferenceStore = TracePluginActivator.getDefault().getPreferenceStore(); 
	}

	/**
	 * Creates this line's label, using the given text, in the given composite.
	 * 
	 * @param labelPane
	 *            the labels composite
	 */
	private void createLabel(final Composite labelPane) {
		label = new CPULabel(labelPane, line);
		
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		label.addMouseListener(new TraceLineSelectListener(line, zoom));
	}

	/**
	 * Creates the line's trace and places it in the traces composite.
	 * 
	 * @param sectionTrace
	 *            the traces composite
	 */
	private void createTrace(final Composite sectionTrace, 
			final TraceCursorListener traceCursorListener) {

		trace = TraceCanvas.createCanvas(sectionTrace, line, zoom, model);
		trace.setMinHeight(label.computeSize(SWT.DEFAULT, SWT.DEFAULT).y); //Set the initial height same as that of the label 
		
		final GridData traceGridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		trace.setLayoutData(traceGridData);

		trace.addMouseListener(new TraceLineSelectListener(line, zoom));
		
		trace.addMouseMoveListener(traceCursorListener);
		trace.addMouseTrackListener(traceCursorListener);
		trace.addMouseListener(traceCursorListener);
		
		TraceZoomListener zoomListener = new TraceZoomListener(zoom, model);
		trace.addMouseListener(zoomListener);
		trace.addMouseMoveListener(zoomListener);
				
		trace.addMouseMoveListener(new TraceToolTipListener(line, zoom));
	}

	public void moveBelow(final TraceLineSeparator separator) {
		SectionViewer targetSectionViewer = separator.getSectionViewer();
		if (sectionViewer != targetSectionViewer) {
			sectionViewer.removeTraceLineViewer(this);
			targetSectionViewer.addTraceLineViewer(this);
			
			sectionViewer = targetSectionViewer;
			traceLineSeparator.setSectionViewer(targetSectionViewer);
		}
		traceLineSeparator.moveBelow(separator);
		separator.moveLineBelow(label, trace);
		layout();
		model.setChanged(); //Notify TraceModel listeners
	}
	
	public final void setDefaultSashOffset() {
		setMinHeight();
		
		// relayout and update vertical scrollbar, and left scroll setting
		sectionViewer.layout();
	}

	private void setMinHeight() {
		setHeight(trace.getHeight());
	}

	public final boolean setSashOffset(final int offset) {
		int height = offset - trace.getLocation().y;		
		int minHeight = trace.getHeight();
		int newHeight = Math.max(height, minHeight);
		setHeight(newHeight);
		
		// relayout and update vertical scrollbar, and left scroll setting
		sectionViewer.layout();
		return (height >= minHeight);
	}	
	
	public void setHeight(final int height) {
		GridData labelGridData = (GridData) label.getLayoutData();
		labelGridData.heightHint = height;
			
		GridData traceGridData = (GridData) trace.getLayoutData();
		traceGridData.heightHint = height;
	}
	
	/**
	 * This method will decide whether to show or hide a line.
	 */
	public void updateAutoHide() {
		if (getAutoHidePreference()) {
			boolean hasSamples = line.hasSamples(zoom.getStartTime(), zoom.getEndTime());
			setVisible(hasSamples);
		} else if (getHideEmptyLinePreference()) {
			setVisible(line.getCount() > 2);
		} else {
			setVisible(true);
		}
	}
	
	private boolean getAutoHidePreference() {
		return preferenceStore.getBoolean(TracePluginActivator.AUTO_HIDE_PREFERENCE);
	}
	
	private boolean getHideEmptyLinePreference() {
		return preferenceStore.getBoolean(TracePluginActivator.HIDE_EMPTY_LINE_PREFERENCE);
	}
	
	public void updateVisibility() {
		setVisible(line.isVisible());
	}
	
	/**
	 * This method will show or hide a line and its associated separator  
	 *
	 * @param visible
	 * 			Boolean value which specifies whether the line should be hidden or not
	 */
	public void setVisible(final boolean visible) {
		if (isVisible != visible) {
			traceLineSeparator.setVisible(visible);
			
			GridData traceGridData = (GridData) trace.getLayoutData();
			traceGridData.exclude = !visible;
			trace.setVisible(visible);

			GridData labelGridData = (GridData) label.getLayoutData();
			labelGridData.exclude = !visible;
			label.setVisible(visible);

			isVisible = visible;
			line.setVisible(visible);
		}
		
		if (visible) {
			setHeight(trace.getHeight());
		} else {
			selectLine(false);
		}
	}

	private void setupReordering() {
		// Link back to this class as the drag source in the reorder listener
		label.setData(this);

		// Allow data to be moved from the drag source
		int operations = DND.DROP_MOVE;

		// Provide data in Text format
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		
		LabelReorderListener reorderListener = new LabelReorderListener();
		
		DragSource source = new DragSource(label, operations);
		source.setTransfer(types);
		source.addDragListener(reorderListener);
		
		// Accept data in text format
		DropTarget target = new DropTarget(label, operations);
		target.setTransfer(types);
		target.addDropListener(reorderListener);
	}
	
	private void layout() {
		label.getParent().layout();
		trace.getParent().layout();
	}
	
	/**
	 * Returns the {@link SampleLine} which is represented by this {@link TraceLineViewer}
	 * @return The {@link SampleLine}
	 */
	public SampleLine getLine() {
		return line;
	}
	
	/**
	 * Selects or deselects this {@link TraceLineViewer}
	 *  
	 * @param select true, to select; false, to deselect
	 * 
	 * @return The selected {@link Control} or null, if unable to select
	 */
	public Control selectLine(boolean select) {
		if (select){
			label.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
			label.setForeground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		} else {
			label.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			label.setForeground(label.getParent().getForeground());
		}
		
		return isVisible ? trace : null;
	}
}