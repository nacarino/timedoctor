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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.ui.trace.canvases.TraceCanvas;

/**
 * Creates the label and trace to view a <code>SampleLine</code>, and places
 * them in the right composites in the gui, retaining symbolic links for
 * organizational purposes.
 */
public class TraceLineViewer implements ISashClient {
	/**
	 * The font size in points of the label's text.
	 */
	private static final int LABEL_FONT_SIZE = 8;

	/**
	 * Static variable to track which label in the entire editor is selected.
	 */
	private static CLabel selectedLabel = null;

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
		
		createLabel(labelPane);		
		createTrace(tracePane, traceCursorListener);
		
		traceLineSeparator = new TraceLineSeparator(labelPane, tracePane);
		
		SashListener sashListener = new SashListener(this, SWT.HORIZONTAL);
		traceLineSeparator.addSelectionListener(sashListener);
		traceLineSeparator.addMouseListener(sashListener);
		
		setupReordering();
				
		// Set default height to minimum needed for label text
		setHeight(0); 
	}

	/**
	 * Creates this line's label, using the given text, in the given composite.
	 * 
	 * @param labelPane
	 *            the labels composite
	 */
	private void createLabel(final Composite labelPane) {
		Image icon = null;
		String cpuName = null;

		if (line.getCPU() != null)
		{
			cpuName = line.getCPU().getName();
		}
		if (cpuName != null) {
			CpuLabel cpuLabel = new CpuLabel(labelPane, line.getType(), cpuName);
			icon = cpuLabel.getImage();
		}

		label = new CLabel(labelPane, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		label.setBackground(labelPane.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));

		label.setImage(icon);
		label.setText(line.getName());

		// Small text font to allow minimal trace line height
		label.setFont(new Font(labelPane.getDisplay(), "Tahoma",
				LABEL_FONT_SIZE, SWT.NORMAL));

		label.addMouseListener(new TraceLineSelectListener(this, line, zoom));
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
		final GridData traceGridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		// Since the labels define the height of the trace section,
		// we must ensure that the initial trace height is equal
		// to or less than the height of the labels for proper layout.
		//traceGridData.heightHint = 16; // TODO
		trace.setLayoutData(traceGridData);

		trace.addMouseListener(new TraceLineSelectListener(this, line, zoom));
		trace.addMouseMoveListener(traceCursorListener);
		trace.addMouseTrackListener(traceCursorListener);	
		trace.addMouseListener(traceCursorListener);
		trace.addMouseListener(new TraceZoomListener(zoom));
		trace.addMouseMoveListener(new TraceToolTipListener(line, zoom));
	}

	public void moveBelow(final TraceLineSeparator separator) {
		traceLineSeparator.moveBelow(separator);
		separator.moveLineBelow(label, trace);
		layout();
	}
	
	/**
	 * Sets line selection to the line associated with the given label.
	 * Unselects whichever label is stored in <code>selected</code>, and sets
	 * <code>selected</code> to be <code>label</code>.
	 * 
	 * @param label
	 *            the label to be set as selected
	 * @param display
	 *            the display object associated with the label
	 */
	public void selectLine(final Display display) {
		// Check if the selected label still exists, it may belong to a
		// label of another editor that has been closed in the meantime
		if ((selectedLabel != null) && !selectedLabel.isDisposed()) {
			selectedLabel.setBackground(selectedLabel.getDisplay().getSystemColor(SWT.COLOR_WHITE)); 
			selectedLabel.setForeground(selectedLabel.getParent().getForeground());			
		}
		label.setBackground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		label.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		selectedLabel = label;
	}
	
	public final void setDefaultSashOffset() {
		setHeight(0);
	}

	public final boolean setSashOffset(final int offset) {
		int height = offset - trace.getLocation().y;
		if (height >= label.computeSize(SWT.DEFAULT, SWT.DEFAULT, false).y) {
			setHeight(height);
			return true;
		}
		else {
			return false;
		}
	}

	public void setHeight(final int height) {
		int minHeight = label.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		int lineHeight = Math.max(height, minHeight);
		GridData labelGridData = (GridData) label.getLayoutData();
		labelGridData.heightHint = lineHeight;
			
		GridData traceGridData = (GridData) trace.getLayoutData();
		traceGridData.heightHint = lineHeight;
			
		// relayout and update vertical scrollbar, and left scroll setting
		sectionViewer.layout();
	}
	
	/**
	 * This method will decide whether to show or hide a line.
	 */
	public void updateAutoHide() {
		boolean hasSamples = line.hasSamples(zoom.getStartTime(), zoom
				.getEndTime());
		setVisible(hasSamples);
		traceLineSeparator.setVisible(hasSamples);
	}
	
	/**
	 * This method will show or hide a line and its associated separator  
	 *
	 * @param visible
	 * 			Boolean value which specifies whether the line should be hidden or not
	 */
	public void setVisible(final boolean visible) {
		traceLineSeparator.setVisible(visible);
		
		if (isVisible != visible) {
			GridData traceGridData = (GridData) trace.getLayoutData();
			traceGridData.exclude = !visible;
			trace.setVisible(visible);

			GridData labelGridData = (GridData) label.getLayoutData();
			labelGridData.exclude = !visible;
			label.setVisible(visible);

			isVisible = visible;
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
}
