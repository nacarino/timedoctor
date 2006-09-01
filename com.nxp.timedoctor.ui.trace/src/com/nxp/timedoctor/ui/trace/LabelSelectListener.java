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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * Listener to handle the selection of labels.
 */
public class LabelSelectListener implements MouseListener {

	/**
	 * Static variable to track which label in the entire editor is selected.
	 */
	private static Label selected = null;

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
	public static void select(final Label label, final Display display) {
		if (selected != null) {
			selected.setBackground(selected.getParent().getBackground());
			selected.setForeground(selected.getParent().getForeground());
		}
		label.setBackground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		label.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		selected = label;
	}

	/**
	 * Label does nothing on double-click.
	 * 
	 * @param e
	 *            MouseEvent containing detailed information about the event
	 */
	public final void mouseDoubleClick(final MouseEvent e) {
	}

	/**
	 * Label does nothing on mouse up.
	 * 
	 * @param e
	 *            MouseEvent containing detailed information about the event
	 */
	public final void mouseUp(final MouseEvent e) {
	}

	/**
	 * Selects the label of a mouseDown event.
	 * 
	 * @param e
	 *            MouseEvent containing detailed information about the event
	 */
	public final void mouseDown(final MouseEvent e) {
		select((Label) e.widget, e.display);
	}
}
