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
	private TraceLineViewer traceLineViewer;
	private static Control dragSourceControl;

	public LabelReorderListener(final TraceLineViewer traceLineViewer) {
		this.traceLineViewer = traceLineViewer;
	}
	
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
		// Store the drag source for reference during
		// the drop operation
		dragSourceControl = ((DragSource) event.widget).getControl();
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

		if (!dropValid(event)) {
			event.detail = DND.DROP_NONE;
		}
	}

	public void dragLeave(final DropTargetEvent event) {
		traceLineViewer.deselectDropTarget(getDropTargetControl(event));
	}

	public void dragOperationChanged(final DropTargetEvent event) {
	}

	public void dragOver(final DropTargetEvent event) {
		if (dropValid(event)) {
			traceLineViewer.selectDropTarget(getDropTargetControl(event));
		}
	}

	public void drop(final DropTargetEvent event) {
		traceLineViewer.moveBelow(dragSourceControl, getDropTargetControl(event));
	}

	public void dropAccept(final DropTargetEvent event) {
	}

	private boolean dropValid(final DropTargetEvent event) {
		Control dropTargetControl = getDropTargetControl(event);
		Control dragSourceTop = (Control) dragSourceControl.getData("top");
		Control dragSourceBottom = (Control) dragSourceControl.getData("bottom");

		return ((dragSourceControl.getParent() == dropTargetControl.getParent()) 
				&& (dragSourceControl != dropTargetControl)
				&& (dragSourceTop != dropTargetControl) 
				&& (dragSourceBottom != dropTargetControl));
	}
	
	private Control getDropTargetControl(final DropTargetEvent event) {
		Control selectedControl = ((DropTarget) event.widget).getControl();
		Control dropTargetControl = selectedControl;
		
		// Drop on separator above or below the selected label		
		if (selectedControl.getClass() == dragSourceControl.getClass()) {			
			if (selectedControl.getLocation().y < dragSourceControl.getLocation().y) {
				dropTargetControl = (Control) selectedControl.getData("top");
			} else {
				dropTargetControl = (Control) selectedControl.getData("bottom");
			}
		}
		return dropTargetControl;
	}
	

}
