/*******************************************************************************
 * Copyright (c) 2007 NXP Semiconductors B.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NXP Semiconductors B.V. - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package com.nxp.timedoctor.ui.trace;

import org.eclipse.jface.viewers.ISelection;

import com.nxp.timedoctor.core.model.SampleLine;

/**
 * A {@link ISelection}, representing selection of a {@link SampleLine}
 *
 */
public class TraceSelection implements ISelection {
	private boolean isEmpty = true;
	private SampleLine line = null;
	
	/**
	 * Default constructor.
	 * {@link #isEmpty()} would return true
	 */
	public TraceSelection() {
		isEmpty = true;
	}
	
	/**
	 * Constructor used to mention what {@link SampleLine} is selected
	 * 
	 * @param line The {@link SampleLine} that is selected
	 * 			   If line is null, {@link #isEmpty()} would return true
	 */
	public TraceSelection(final SampleLine line) {
		isEmpty = (line == null);
		this.line = line;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	public boolean isEmpty() {
		return isEmpty;
	}
	
	/**
	 * Returns the {@link SampleLine} of this {@link TraceSelection}
	 * 
	 * @return The {@link SampleLine}, can be null, if initialized so.
	 */
	public SampleLine getLine() {
		return line;
	}
}
