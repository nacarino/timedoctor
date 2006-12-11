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
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.ui.trace.canvases.TraceCanvas;

/**
 * Creates the label and trace to view a <code>SampleLine</code>, and places
 * them in the right composites in the gui, retaining symbolic links for
 * organizational purposes.
 */
public class TraceLineViewer {
	/**
	 * The font size in points of the label's text.
	 */
	private static final int LABEL_FONT_SIZE = 8;

	/**
	 * The height in pixels of the separator between labels.
	 */
	private static final int SEPARATOR_HEIGHT = 2;

	/**
	 * The trace line that is selected during a drag operation for reordering
	 * the trace lines.
	 */
	private static Control dragSourceLabel;

	/**
	 * The label containing the name of the line.
	 */
	private CLabel label;

	/**
	 * Separator below the label, used for drag & drop.
	 */
	private Label bottomSeparator;

	/**
	 * Canvas of the sample line associated with the label.
	 */
	private TraceCanvas trace;

	private Sash traceSash;
	
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

	/**
	 * Static variable to track which label in the entire editor is selected.
	 */
	private static CLabel selectedLabel = null;
	
	private Label separator;
	
	private boolean isVisible = true;
	
	private MainViewer mainViewer;
	
	/**
	 * Constructs a new TraceLineViewer.
	 * 
	 * @param topLine
	 *            the trace line above this line, <code>null</code> if this
	 *            line is the first in the section.
	 * @param sectionLabel
	 *            the labels composite
	 * @param sectionTrace
	 *            the traces composite
	 * @param sampleLine
	 *            the sample line containing data for this line
	 * @param zoomData
	 *            the observable model part containing zoom/scroll data
	 * @param model
	 *            model containing data on the whole trace
	 */
	public TraceLineViewer(final MainViewer mainViewer,
			final TraceLineViewer topLine,
			final Composite sectionLabel, final Composite sectionTrace,
			final SampleLine sampleLine, final ZoomModel zoomData,
			final TraceModel model, final TraceCursorListener traceCursorListener) {

		this.mainViewer = mainViewer;
		this.line = sampleLine;
		this.zoom = zoomData;
		this.model = model;

		if (null == topLine) {
			// separator above first label
			Label topSeparator = createSeparator(sectionLabel);
			createLabel(sectionLabel);
			label.setData("top", topSeparator);
			topSeparator.setData("bottom", label);

			// Padding above first trace line to align with the label
			Label topPadding = createTracePadding(sectionTrace);

			// Add this padding as "trace" to the top separator to have
			// something that traces can be moved below during drag & drop.
			topSeparator.setData("sash", topPadding);
		} else {
			createLabel(sectionLabel);
			label.setData("top", topLine.bottomSeparator);
			topLine.bottomSeparator.setData("bottom", label);
		}

		// Create separator below the label
		bottomSeparator = createSeparator(sectionLabel);
		label.setData("bottom", bottomSeparator);

		createTrace(sectionTrace, traceCursorListener);
		
		// Set default height to minimum needed for label text
		setHeight(0); 
	}

	/**
	 * Creates this line's label, using the given text, in the given composite.
	 * 
	 * @param sectionLabel
	 *            the labels composite
	 */
	private void createLabel(final Composite sectionLabel) {
		Image icon = null;
		String cpuName = null;

		if (line.getCPU() != null)
		{
			cpuName = line.getCPU().getName();
		}
		if (cpuName != null) {
			CpuLabel cpuLabel = new CpuLabel(sectionLabel, line.getType(), cpuName);
			icon = cpuLabel.getImage();
		}

		label = new CLabel(sectionLabel, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		label.setBackground(sectionLabel.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));

		label.setImage(icon);
		label.setText(line.getName());

		// Small text font to allow minimal trace line height
		label.setFont(new Font(sectionLabel.getDisplay(), "Tahoma",
				LABEL_FONT_SIZE, SWT.NORMAL));

		label.addMouseListener(new TraceLineSelectListener(this, line, zoom));

		setupLabelDND();
	}

	/**
	 * Creates a separator between trace labels used to highlight the selected
	 * position during drag of trace lines for reordering the trace lines within
	 * a section.
	 * 
	 * @param sectionLabel
	 *            the labels composite
	 * @return the created separator
	 */
	private Label createSeparator(final Composite sectionLabel) {
		separator = new Label(sectionLabel, SWT.HORIZONTAL);
		separator.setBackground(sectionLabel.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		separator.setForeground(sectionLabel.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		data.heightHint = SEPARATOR_HEIGHT;
		separator.setLayoutData(data);

		setupSeparatorDND(separator);

		return separator;
	}

	/**
	 * Padding above first trace to align with labels.
	 * 
	 * @param sectionTrace
	 *            the trace composite
	 * @return the label that forms the padding
	 */
	private Label createTracePadding(final Composite sectionTrace) {
		Label topPadding = new Label(sectionTrace, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		data.heightHint = SEPARATOR_HEIGHT;
		topPadding.setLayoutData(data);

		topPadding.setBackground(topPadding.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		return topPadding;
	}

	/**
	 * Creates the line's trace and places it in the traces composite.
	 * 
	 * @param sectionTrace
	 *            the traces composite
	 */
	private void createTrace(final Composite sectionTrace, 
			final TraceCursorListener traceCursorListener) {
		// Add padding on top to ensure alignment of traces and labels
		if (sectionTrace.getChildren().length == 0) {
			Label topPadding = new Label(sectionTrace, SWT.NONE);
			GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
			data.heightHint = SEPARATOR_HEIGHT;
			topPadding.setLayoutData(data);

			topPadding.setBackground(topPadding.getDisplay().getSystemColor(
					SWT.COLOR_WHITE));
		}

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
		
		// sash below each trace
		traceSash = new Sash(sectionTrace, SWT.HORIZONTAL);
		GridData sashGridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		sashGridData.heightHint = SEPARATOR_HEIGHT;
		traceSash.setLayoutData(sashGridData);
		
		traceSash.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				// Ensure that select action does not execute upon double-click in linux
				if (e.detail == SWT.DRAG) {
					int height = e.y - trace.getLocation().y;
					if (height >= label.computeSize(SWT.DEFAULT, SWT.DEFAULT).y) {
						setHeight(height); 				
					}
					else {
						e.doit = false;
					}
				}
			}
		});
		
		traceSash.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				setHeight(0);				 
			}
		});
		
		// Used for drag & drop
		label.setData("trace", trace);
		label.setData("sash", traceSash);
		bottomSeparator.setData("sash", traceSash);
	}

	
	/**
	 * Setup drag and drop for the trace label to allow reordering of trace
	 * lines.
	 */
	private void setupLabelDND() {
		DragSourceAdapter dragListener = new DragSourceAdapter() {
			@Override
			public final void dragSetData(final DragSourceEvent event) {
				// Provide the transfer data of the requested type
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					CLabel sourceLabel = ((CLabel) ((DragSource) event.widget)
							.getControl());
					event.data = sourceLabel.getText(); // Transfer the label's
					// text for now
				}
			}

			@Override
			public final void dragStart(final DragSourceEvent event) {
				// Store the source label for reference during
				// the drop operation
				dragSourceLabel = (((DragSource) event.widget)
						.getControl());
			}
		};

		DropTargetAdapter dropListener = new DropTargetAdapter() {
			@Override
			public final void dragEnter(final DropTargetEvent event) {
				// Set visual indication that only moves are allowed
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_MOVE) != 0) {
						event.detail = DND.DROP_MOVE;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}

				if (!dropValid(event)) {
					event.detail = DND.DROP_NONE;
				}
			}

			@Override
			public final void dragLeave(final DropTargetEvent event) {
				deselectDropTarget(getTargetSeparator(event));
			}

			@Override
			public final void dragOver(final DropTargetEvent event) {
				if (dropValid(event)) {
					selectDropTarget(getTargetSeparator(event));
				}
			}

			@Override
			public final void drop(final DropTargetEvent event) {
				moveBelow(getTargetSeparator(event));
			}

			private Control getTargetLabel(final DropTargetEvent event) {
				DropTarget target = (DropTarget) event.widget;
				return target.getControl();
			}

			private Control getTargetSeparator(final DropTargetEvent event) {
				Control targetLabel = getTargetLabel(event);
				Control targetSeparator;
				if (targetLabel.getLocation().y < dragSourceLabel.getLocation().y) {
					targetSeparator = (Control) targetLabel.getData("top");
				} else {
					targetSeparator = (Control) targetLabel.getData("bottom");

				}
				return targetSeparator;
			}

			private boolean dropValid(final DropTargetEvent event) {
				Control targetLabel = getTargetLabel(event);
				return ((dragSourceLabel.getParent() == targetLabel.getParent()) && (dragSourceLabel != targetLabel));
			}
		};

		// Set label as drag source
		// Allow data to be moved from the drag source
		int operations = DND.DROP_MOVE;

		// Provide data in Text format
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		DragSource source = new DragSource(label, operations);

		source.setTransfer(types);
		source.addDragListener(dragListener);

		// drop support in label section
		// Allow data to be moved to the drop target
		DropTarget target = new DropTarget(label, operations);

		// Receive data in Text format
		target.setTransfer(types);
		target.addDropListener(dropListener);
	}

	/**
	 * Setup drag and drop for the separator between trace labels to allow
	 * reordering of trace lines.
	 * 
	 * @param separator
	 *            the separator that requires drag & drop
	 */
	private void setupSeparatorDND(final Control separator) {
		DropTargetAdapter dropListener = new DropTargetAdapter() {
			@Override
			public final void dragEnter(final DropTargetEvent event) {
				// Set visual indication that only moves are allowed
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_MOVE) != 0) {
						event.detail = DND.DROP_MOVE;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}

				if (!dropValid(event)) {
					event.detail = DND.DROP_NONE;
				}

			}

			@Override
			public final void dragLeave(final DropTargetEvent event) {
				deselectDropTarget(getTargetSeparator(event));

			}

			@Override
			public final void dragOver(final DropTargetEvent event) {
				if (dropValid(event)) {
					selectDropTarget(getTargetSeparator(event));
				}
			}

			@Override
			public final void drop(final DropTargetEvent event) {

				moveBelow(getTargetSeparator(event));

			}

			private boolean dropValid(final DropTargetEvent event) {
				Control targetSeparator = getTargetSeparator(event);
				Control top = (Control) dragSourceLabel.getData("top");
				Control bottom = (Control) dragSourceLabel.getData("bottom");
				return ((dragSourceLabel.getParent() == targetSeparator
						.getParent())
						&& (top != targetSeparator) && (bottom != targetSeparator));
			}

			private Control getTargetSeparator(final DropTargetEvent event) {
				DropTarget target = (DropTarget) event.widget;
				return target.getControl();

			}
		};

		// Set label as drag source
		// Allow data to be moved from the drag source
		int operations = DND.DROP_MOVE;

		// Provide data in Text format
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		// drop support in label section
		// Allow data to be moved to the drop target
		DropTarget target = new DropTarget(separator, operations);

		// Receive data in Text format
		target.setTransfer(types);
		target.addDropListener(dropListener);
	}

	/**
	 * Visually highlight the selected separator.
	 * 
	 * @param targetSeparator
	 *            the selected separator
	 */
	private void selectDropTarget(final Control targetSeparator) {
		targetSeparator.setBackground(targetSeparator.getDisplay()
				.getSystemColor(SWT.COLOR_BLUE));
	}

	/**
	 * Remove highlight from the selected separator.
	 * 
	 * @param targetSeparator
	 *            the selected separator
	 */
	private void deselectDropTarget(final Control targetSeparator) {
		targetSeparator.setBackground(targetSeparator.getDisplay()
				.getSystemColor(SWT.COLOR_WHITE));
	}

	/**
	 * Move the label and separator of the selected <code>dragSourceLine</code>
	 * below the given target separator. Also move the sample line of the
	 * <code>dragSourceLine</code> below the sample line associated with the
	 * target separator
	 * 
	 * @param targetSeparator
	 *            the separator selected by drag & drop
	 */
	private void moveBelow(final Control targetSeparator) {
		Control sourceBottom = (Control) dragSourceLabel.getData("bottom");
		Control sourceAbove = (Control) dragSourceLabel.getData("top");
		Control sourceBelow = (Control) sourceBottom.getData("bottom");
		Control targetBelow = (Control) targetSeparator.getData("bottom");

		// Reorder the labels
		dragSourceLabel.moveBelow(targetSeparator);
		sourceBottom.moveBelow(dragSourceLabel);
		targetSeparator.getParent().layout();

		// Fix label references
		if (null != targetBelow) {
			targetBelow.setData("top", sourceBottom);
		}
		targetSeparator.setData("bottom", dragSourceLabel);

		if (null != sourceBelow) {
			sourceBelow.setData("top", sourceAbove);
		}
		sourceAbove.setData("bottom", sourceBelow);

		dragSourceLabel.setData("top", targetSeparator);
		sourceBottom.setData("bottom", targetBelow);

		// Reorder traces
		Control targetSash = (Control) targetSeparator.getData("sash");
		Control sourceSash = (Control) dragSourceLabel.getData("sash");
		Control sourceTrace = (Control) dragSourceLabel.getData("trace");
		sourceTrace.moveBelow(targetSash);
		sourceSash.moveBelow(sourceTrace);
		targetSash.getParent().layout();
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

	public void setHeight(final int height) {
		int minHeight = label.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		int lineHeight = Math.max(height, minHeight);
		GridData labelGridData = (GridData) label.getLayoutData();
		labelGridData.heightHint = lineHeight;
			
		GridData traceGridData = (GridData) trace.getLayoutData();
		traceGridData.heightHint = lineHeight;
			
		// relayout and update vertical scrollbar, and left scroll setting
		// TODO do not depend on SectionViewer class, better MVC
		mainViewer.layout();
	}
	
	/**
	 * This method will decide whether to show or hide a line.
	 */
	public void updateVisibility() {
		boolean hasSamples = line.hasSamples(zoom.getStartTime(), zoom
				.getEndTime());

		if ((hasSamples == true) && (isVisible == false)) {
			showLine(hasSamples);
		} else if ((hasSamples == false) && (isVisible == true)) {
			showLine(hasSamples);
		}
	}

	/**
	 * This method will show or hide a line  
	 *
	 * @param visible
	 * 			Boolean value which specifies whether the line should be hidden or not
	 */
	public void showLine(final boolean visible) {
		GridData traceGridData = (GridData) trace.getLayoutData();
		traceGridData.exclude = !visible;
		trace.setVisible(visible);

		GridData sashGridData = (GridData) traceSash.getLayoutData();
		sashGridData.exclude = !visible;
		traceSash.setVisible(visible);
		
		GridData labelGridData = (GridData) label.getLayoutData();
		labelGridData.exclude = !visible;
		label.setVisible(visible);

		GridData separatorGridData = (GridData) separator.getLayoutData();
		separatorGridData.exclude = !visible;
		separator.setVisible(visible);

		isVisible = visible;
	}
}
