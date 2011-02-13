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
package com.nxp.timedoctor.ui.trace.canvases;

import org.eclipse.swt.events.PaintEvent;

import com.nxp.timedoctor.ui.trace.Colors;

/**
 * Contains the code to paint a queue.
 */
public class TracePaintListener {

	/**
	 * The minimum allowed x-value, for use in the <code>boundedInt</code>
	 * function.
	 */
	private static final int X_MIN = -100;

	/**
	 * The maximum allowed x-value, for use in the <code>boundedInt</code>
	 * function.
	 */
	private static final int X_MAX = 100000;

	/**
	 * Sapcing in pixels between grid lines.
	 */
	private static final int GRID_SPACING = 10;

	protected void drawGridLines(final PaintEvent e, 
			final int canvasHeight) {
		e.gc.setForeground(Colors.getColorRegistry().get(Colors.LIGHT_YELLOW));
				
		for (int y = GRID_SPACING; y <= canvasHeight; y += GRID_SPACING) {
			e.gc.drawLine(e.x, canvasHeight - y, 
					e.x + e.width, canvasHeight - y);
		}
	}

	/**
	 * Ensures the given value is within the valid x-values and casts it to an
	 * int. If the value is too low, returns <code>X_MIN</code>. If it's too
	 * high, returns <code>X_MAX</code>.
	 * 
	 * @param val
	 *            the value to be checked and casted
	 * @return <code>value</code>, <code>X_MIN</code>, or
	 *         <code>X_MAX</code>
	 */
	protected int boundedInt(final double val) {
		return (int) Math.min(X_MAX, Math.max(X_MIN, val));
	}
}
