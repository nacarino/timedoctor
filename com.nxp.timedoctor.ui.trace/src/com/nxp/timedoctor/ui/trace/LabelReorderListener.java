/*******************************************************************************
 * Copyright (c) 2006 NXP Semiconductors B.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NXP Semiconductors B.V. - initial API and implementation
 *******************************************************************************/
package com.nxp.timedoctor.ui.trace;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Control;

public class LabelReorderListener implements DragSourceListener, DropTargetListener {
	private class LabelDragSource {
		public Control control;
		public int drawIndex;
		public TraceLineViewer traceLineViewer;

		public LabelDragSource(final Control controls[], final DragSourceEvent event) {
			// Store the drag source for reference during
			// the drop operation
			control = ((DragSource) event.widget).getControl();
			drawIndex = getControlIndex(controls, control);
			if (!(control.getData() instanceof TraceLineViewer)) {
				traceLineViewer = null;
			}
			traceLineViewer = (TraceLineViewer) control.getData();
		}
		
		public void moveBelow(final SeparatorDropTarget separatorDropTarget) {
			traceLineViewer.moveBelow(separatorDropTarget.traceLineSeparator);
		}
	}
	
	private class SeparatorDropTarget {
		public Control control;
		public TraceLineSeparator traceLineSeparator;
		
		public SeparatorDropTarget(final Control controls[], 
				final LabelDragSource labelDragSource,
				final DropTargetEvent event) {
			Control selectedControl = ((DropTarget) event.widget).getControl();
			int selectedIndex = getControlIndex(controls, selectedControl);
			
			// If the selected control is a Label, select the closest visible
			// separator above or below the label
			if (selectedControl.getData() instanceof TraceLineViewer) {
				int dropTargetIndex;
				if (selectedControl.getLocation().y < labelDragSource.control.getLocation().y) {
					dropTargetIndex = getTopControlIndex(controls, selectedIndex);
				} else {
					dropTargetIndex = getBottomControlIndex(controls, selectedIndex);
				}
				control = controls[dropTargetIndex];
			}		
			else {
				control = selectedControl;
			}
			if (!(control.getData() instanceof TraceLineSeparator)) {
				traceLineSeparator = null;
			}		
			traceLineSeparator = (TraceLineSeparator) control.getData();
		}
		
		public void highlight(final boolean isHighlighted) {
			traceLineSeparator.highlightDropTarget(isHighlighted);
		}
	}

	private static Control controls[];
	private static LabelDragSource labelDragSource;
	private SeparatorDropTarget separatorDropTarget;
	
	public void dragFinished(final DragSourceEvent event) {
	}

	public void dragSetData(final DragSourceEvent event) {
		// Provide the transfer data of the requested type
		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			CLabel sourceLabel = ((CLabel) ((DragSource) event.widget)
					.getControl());
			// Transfer the label's text
			event.data = sourceLabel.getText();
		}
	}

	public void dragStart(final DragSourceEvent event) {
		controls = ((DragSource) event.widget).getControl().getParent().getChildren();
		labelDragSource = new LabelDragSource(controls, event);
	}

	public void dragEnter(final DropTargetEvent event) {
		// Set visual indication that only moves are allowed
		if (event.detail == DND.DROP_DEFAULT) {
			if ((event.operations & DND.DROP_MOVE) != 0) {
				event.detail = DND.DROP_MOVE;
			} else {
				event.detail = DND.DROP_NONE;
			}
		}

		separatorDropTarget = new SeparatorDropTarget(controls, labelDragSource, event);
		if (!dropValid(controls, labelDragSource, separatorDropTarget)) {
			event.detail = DND.DROP_NONE;
		}
	}

	public void dragLeave(final DropTargetEvent event) {		
		separatorDropTarget.highlight(false);
	}

	public void dragOperationChanged(final DropTargetEvent event) {
	}

	public void dragOver(final DropTargetEvent event) {
		if (dropValid(controls, labelDragSource, separatorDropTarget)) {
			separatorDropTarget.highlight(true);
		}
	}

	public void drop(final DropTargetEvent event) {		
		labelDragSource.moveBelow(separatorDropTarget);
	}

	public void dropAccept(final DropTargetEvent event) {
	}

	// Find index i of selected control in the drawing order
	private int getControlIndex(final Control controls[], final Control selectedControl) {
		int index = 0;
		for (; (index < controls.length) && (controls[index] != selectedControl); index++) {
			;
		}
		return index;
	}
	
	// Find index i of the nearest visible separator above the selected control 
	// in the drawing order
	private int getTopControlIndex(final Control controls[], final int index) {
		int topIndex = index - 1;
		for (; topIndex >= 0; topIndex--) {
			// Only look for separators
			if (controls[topIndex].getData() instanceof TraceLineSeparator) {
				// Only look for visible separators
				if (controls[topIndex].isVisible()) {
					break;
				}
			}
		}
		return topIndex;		
	}

	// Find index i of the nearest visible separator below the selected control 
	// in the drawing order	
	private int getBottomControlIndex(final Control controls[], final int index) {
		int bottomIndex = index + 1;
		for (; bottomIndex < controls.length; bottomIndex++) {
			// Only look for separators
			if (controls[bottomIndex].getData() instanceof TraceLineSeparator) {
				// Only look for visible separators
				if (controls[bottomIndex].isVisible()) {
					break;
				}
			}
		}
		return bottomIndex;		
	}

	private boolean dropValid(final Control controls[],
			final LabelDragSource dragSource,
			final SeparatorDropTarget dropTarget) {
		Control dragSourceTop = controls[getTopControlIndex(controls, dragSource.drawIndex)];
		Control dragSourceBottom = controls[getBottomControlIndex(controls, dragSource.drawIndex)];
		
		return ((dragSource.control.getParent() == dropTarget.control.getParent()) 
				&& (dragSource.control != dropTarget.control)
				&& (dragSourceTop != dropTarget.control)
				&& (dragSourceBottom != dropTarget.control));
	}
}
