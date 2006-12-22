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

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;

public class TraceLineSeparator {
	/**
	 * The height in pixels of the separator between labels.
	 */
	private static final int SEPARATOR_HEIGHT = 2;
	
	private Label labelSeparator;
	private Sash traceSeparator;
	private boolean isVisible = true;

	/**
	 * Constructs a new TraceLineSeparator.
	 * 
	 */
	public TraceLineSeparator(final Composite labelPane,
			final Composite tracePane) {

		createContents(labelPane, tracePane);
	}

	/**
	 * Creates a separator between trace labels used to highlight the selected
	 * position during drag of trace lines for reordering the trace lines within
	 * a section.
	 */
	private void createContents(final Composite labelPane, 
			final Composite tracePane) {
		labelSeparator = new Label(labelPane, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		data.heightHint = SEPARATOR_HEIGHT;
		labelSeparator.setLayoutData(data);

		labelSeparator.setBackground(labelPane.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		labelSeparator.setForeground(labelPane.getDisplay().getSystemColor(
				SWT.COLOR_BLUE));

		traceSeparator = new Sash(tracePane, SWT.HORIZONTAL);
		GridData traceGridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		traceGridData.heightHint = SEPARATOR_HEIGHT;
		traceSeparator.setLayoutData(traceGridData);
		
		// Disable sash functionality by default, only enable if a 
		// selection listener is registered
		// This ensures the header separator of a section does not 
		// have sash functionality
		traceSeparator.setEnabled(false);		
	}
	
	public void setReorderListener(final LabelReorderListener listener) {
		// Link back to this class as the drop target in the reorder listener
		labelSeparator.setData(this);

		// Allow data to be moved from the drag source
		int operations = DND.DROP_MOVE;

		// Provide data in Text format
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		// Accept data in text format
		DropTarget target = new DropTarget(labelSeparator, operations);
		target.setTransfer(types);
		target.addDropListener(listener);
	}
	
	/**
	 * Visually highlight the selected separator.
	 *
	 * @param isHighlighted true if the drop target must be accentuated
	 */
	public void highlightDropTarget(final boolean isHighlighted) {
		if (isHighlighted) {
			labelSeparator.setBackground(labelSeparator.getDisplay()
				.getSystemColor(SWT.COLOR_BLUE));
		}
		else {
			labelSeparator.setBackground(labelSeparator.getDisplay()
					.getSystemColor(SWT.COLOR_WHITE));			
		}
	}
	
	public void moveBelow(final TraceLineSeparator targetSeparator) {
		labelSeparator.moveBelow(targetSeparator.labelSeparator);
		traceSeparator.moveBelow(targetSeparator.traceSeparator);
	}	

	// Bit ugly to move other controls here, but only way to hide all access to internals
	public void moveLineBelow(final Control label, final Control trace) {
		label.moveBelow(labelSeparator);
		trace.moveBelow(traceSeparator);
	}

	/**
	 * This method will show or hide the separator  
	 *
	 * @param visible
	 * 			Boolean value which specifies whether the line should be hidden or not
	 */
	public void setVisible(final boolean visible) {
		if (isVisible != visible) {
			GridData separatorGridData = (GridData) labelSeparator.getLayoutData();
			separatorGridData.exclude = !visible;
			labelSeparator.setVisible(visible);

			GridData sashGridData = (GridData) traceSeparator.getLayoutData();
			sashGridData.exclude = !visible;
			traceSeparator.setVisible(visible);
			
			isVisible = visible;
		}
	}

	public void setBackground(final Color color) {
		traceSeparator.setBackground(color);		
	}
	
	public void addMouseListener(final MouseListener listener) {		
		traceSeparator.addMouseListener(listener);
		setEnabled(true);
	}
	
	public void addSelectionListener(final SelectionListener listener) {
		traceSeparator.addSelectionListener(listener);
		setEnabled(true);
	}
	
	private void setEnabled(final boolean enabled) {
		traceSeparator.setEnabled(enabled);		
	}
}
