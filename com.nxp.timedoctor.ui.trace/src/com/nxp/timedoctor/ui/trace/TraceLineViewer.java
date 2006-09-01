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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

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
	private Label label;

	/**
	 * Separator below the label, used for drag & drop.
	 */
	private Label bottomSeparator;

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
	 * The model component containing zoom/scroll values.
	 */
	private ZoomModel zoom;

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
	public TraceLineViewer(final TraceLineViewer topLine,
			final Composite sectionLabel, final Composite sectionTrace,
			final SampleLine sampleLine, final ZoomModel zoomData,
			final TraceModel model) {
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
			topSeparator.setData("trace", topPadding);
		} else {
			createLabel(sectionLabel);
			label.setData("top", topLine.bottomSeparator);
			topLine.bottomSeparator.setData("bottom", label);
		}

		// Create separator below the label
		bottomSeparator = createSeparator(sectionLabel);
		label.setData("bottom", bottomSeparator);

		createTrace(sectionTrace);
	}

	/**
	 * Creates this line's label, using the given text, in the given composite.
	 * 
	 * @param sectionLabel
	 *            the labels composite
	 */
	private void createLabel(final Composite sectionLabel) {
		label = new Label(sectionLabel, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		label.setText(line.getName());
		// Small text font to allow minimal trace line height
		label.setFont(new Font(sectionLabel.getDisplay(), "Arial",
				LABEL_FONT_SIZE, SWT.NORMAL));

		final LabelSelectListener selectListener = new LabelSelectListener();
		label.addMouseListener(selectListener);

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
		Label separator = new Label(sectionLabel, SWT.HORIZONTAL);
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
	private void createTrace(final Composite sectionTrace) {
		// Add padding on top to ensure alignment of traces and labels
		if (sectionTrace.getChildren().length == 0) {
			Label topPadding = new Label(sectionTrace, SWT.NONE);
			GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
			data.heightHint = SEPARATOR_HEIGHT;
			topPadding.setLayoutData(data);

			topPadding.setBackground(topPadding.getDisplay().getSystemColor(
					SWT.COLOR_WHITE));
		}

		/*
		 * Enables line selection from canvas by using a label linked to the
		 * canvas by alling getData, the setting (if the label is not null) that
		 * label to be selected.
		 */
		MouseListener selectListener = new MouseListener() {
			public void mouseDoubleClick(final MouseEvent e) {
			}

			public void mouseDown(final MouseEvent e) {
				LabelSelectListener.select(label, e.display);
			}

			public void mouseUp(final MouseEvent e) {
			}
		};

		// Changes cursor to a line with an arrow
		TraceCursorListener cursorListener = new TraceCursorListener();

		trace = TraceCanvas.createCanvas(sectionTrace, line, zoom, model);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		// Since the labels define the height of the trace section,
		// we must ensure that the initial trace height is equal
		// to or less than the height of the labels for proper layout.
		data.heightHint = SEPARATOR_HEIGHT;
		trace.setLayoutData(data);

		// Exclude a label from layout, and reduce height of parent
		// if (i == 3) {
		// ((GridData) trace.getLayoutData()).exclude = true;
		// // label.getParent().getParent().layout();
		// }

		trace.addMouseListener(selectListener);
		trace.addMouseMoveListener(cursorListener);

		// Used for drag & drop
		label.setData("trace", trace);
		bottomSeparator.setData("trace", trace);
	}

	/**
	 * Setup drag and drop for the trace label to allow reordering of trace
	 * lines.
	 */
	private void setupLabelDND() {
		DragSourceAdapter dragListener = new DragSourceAdapter() {
			public final void dragSetData(final DragSourceEvent event) {
				// Provide the transfer data of the requested type
				if (TextTransfer.getInstance()
						.isSupportedType(event.dataType)) {
					Label sourceLabel = ((Label) ((DragSource) event.widget)
							.getControl());
					event.data = sourceLabel.getText(); // Transfer the label's
					// text for now
				}
			}

			public final void dragStart(final DragSourceEvent event) {
				// Store the source label for reference during
				// the drop operation
				dragSourceLabel = ((Control) ((DragSource) event.widget)
						.getControl());
			}
		};

		DropTargetAdapter dropListener = new DropTargetAdapter() {
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

			public final void dragLeave(final DropTargetEvent event) {
				deselectDropTarget(getTargetSeparator(event));
			}

			public final void dragOver(final DropTargetEvent event) {
				if (dropValid(event)) {
					selectDropTarget(getTargetSeparator(event));
				}
			}

			public final void drop(final DropTargetEvent event) {
				moveBelow(getTargetSeparator(event));
			}

			private Control getTargetLabel(final DropTargetEvent event) {
				DropTarget target = (DropTarget) event.widget;
				return (Control) target.getControl();
			}

			private Control getTargetSeparator(final DropTargetEvent event) {
				Control targetLabel = getTargetLabel(event);
				Control targetSeparator;
				if (targetLabel.getLocation().y
						< dragSourceLabel.getLocation().y) {
					targetSeparator = (Control) targetLabel.getData("top");
				} else {
					targetSeparator = (Control) targetLabel.getData("bottom");
				}
				return targetSeparator;
			}

			private boolean dropValid(final DropTargetEvent event) {
				Control targetLabel = getTargetLabel(event);
				return ((dragSourceLabel.getParent() == targetLabel.getParent())
						&& (dragSourceLabel != targetLabel));
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

			public final void dragLeave(final DropTargetEvent event) {
				deselectDropTarget(getTargetSeparator(event));
			}

			public final void dragOver(final DropTargetEvent event) {
				if (dropValid(event)) {
					selectDropTarget(getTargetSeparator(event));
				}
			}

			public final void drop(final DropTargetEvent event) {
				moveBelow(getTargetSeparator(event));
			}

			private boolean dropValid(final DropTargetEvent event) {
				Control targetSeparator = getTargetSeparator(event);
				Control top = (Control) dragSourceLabel.getData("top");
				Control bottom = (Control) dragSourceLabel.getData("bottom");
				return ((dragSourceLabel.getParent() == targetSeparator
						.getParent())
						&& (top != targetSeparator)
						&& (bottom != targetSeparator));
			}

			private Control getTargetSeparator(final DropTargetEvent event) {
				DropTarget target = (DropTarget) event.widget;
				return (Control) target.getControl();
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
		targetSeparator.setBackground(targetSeparator.getParent()
				.getBackground());
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
		Control targetTrace = (Control) targetSeparator.getData("trace");
		Control sourceTrace = (Control) dragSourceLabel.getData("trace");
		sourceTrace.moveBelow(targetTrace);
		targetTrace.getParent().layout();
	}
}
