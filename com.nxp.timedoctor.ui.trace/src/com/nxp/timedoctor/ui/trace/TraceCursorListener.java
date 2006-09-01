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

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Specific <code>MouseMoveListener</code>. Sets the mouse cursor to a
 * vertical line with an arrow at the mouse position.
 * <p>
 * The listener assumes a specific hierarchy of controls, and compensates for
 * the offsets introduced by each control. (to be cleaned up sometime)
 * <p>
 * Problem is that tooltips appear at the bottom of the vertical line, not at
 * the cursor specified hotspot. This may be a problem for description pop-ups
 * in TimeDoctor. Additional problem of this approach is that we cannot use it
 * to shows cursors in different trace views of which only one has the focus.
 */
public class TraceCursorListener implements MouseMoveListener {
	/**
	 * Height of the cursor extending into the ruler.
	 */
	public static final int CURSOR_HEADER_HEIGHT = 30;

	/**
	 * Width of the cursor image.
	 */
	private static final int CURSOR_WIDTH = 5;

	/**
	 * Width of the cursor vertical line.
	 */
	private static final int CURSOR_LINE_WIDTH = 1;

	/**
	 * Height of the cursor arrow image.
	 */
	private static final int CURSOR_ARROW_HEIGHT = 9;

	/**
	 * The maximum integer value permissible in an RGB color definition.
	 */
	private static final int RGB_MAX = 255;

	/**
	 * Arrow shaped mask for the mouse cursor For the selected palette, the
	 * cursor color depends on the mask as follows: image=1, mask=0 =>
	 * transparent image=0, mask=0 => transparent image=1, mask=1 => black
	 * image=0, mask=1 => red.
	 */
	private static final byte[] ARROW_MASK = { (byte) 0x0, (byte) 0x0,
			(byte) 0x0, (byte) 0x00, (byte) 0x01, (byte) 0x0, (byte) 0x0,
			(byte) 0x0, (byte) 0x01, (byte) 0x01, (byte) 0x0, (byte) 0x0,
			(byte) 0x1, (byte) 0x01, (byte) 0x01, (byte) 0x0, (byte) 0x1,
			(byte) 0x1, (byte) 0x01, (byte) 0x01, (byte) 0x0, (byte) 0x1,
			(byte) 0x1, (byte) 0x01, (byte) 0x01, (byte) 0x0, (byte) 0x1,
			(byte) 0x1, (byte) 0x01, (byte) 0x01, (byte) 0x0, (byte) 0x0,
			(byte) 0x1, (byte) 0x01, (byte) 0x01, (byte) 0x0, (byte) 0x0,
			(byte) 0x0, (byte) 0x01, (byte) 0x01, (byte) 0x0, (byte) 0x0,
			(byte) 0x0, (byte) 0x00, (byte) 0x01 };

	/**
	 * The color palette for creation of the cursor.
	 */
	private PaletteData palette;

	/**
	 * The cursor itself.
	 */
	private Cursor cursor;

	/**
	 * Constructor.
	 */
	public TraceCursorListener() {
		// Create monochrome palette (red color).
		palette = new PaletteData(new RGB[] { new RGB(RGB_MAX, 0, 0) });
	}

	/**
	 * Set the mouse cursor to a 1-pixel wide vertical line an arrow at the
	 * location of the cursor.
	 * 
	 * @param e
	 *            an event containing information about the mouse move
	 */
	public final void mouseMove(final MouseEvent e) {		
		final Control currentWidget = (Control) e.widget;

		if (e.display.getActiveShell() == null) {
			// Window does not have focus, so use the default cursor
			currentWidget.setCursor(null);
			return;
		}
		
		final Composite parent1 = currentWidget.getParent();
		final Composite parent2 = parent1.getParent();
		final Composite parent3 = parent2.getParent();
		final Composite parent4 = parent3.getParent();
		final Composite parent5 = parent4.getParent();
		final Composite parent6 = parent5.getParent();

		final int cursorHeight = parent3.getParent().getClientArea().height
				+ CURSOR_HEADER_HEIGHT;

		// Create monochrome image of 5 pixels wide, all pixels initialized to
		// 0.
		final ImageData cursorData = new ImageData(CURSOR_WIDTH, cursorHeight,
				CURSOR_LINE_WIDTH, palette);

		// Vertical position of the mouse cursor within the scrolled trace pane
		final int offset = e.y // offset within canvas
				+ currentWidget.getLocation().y // Offset of other trace lines
				+ parent1.getLocation().y // Offset of other sections
				+ parent2.getLocation().y // offset of vertical scrollbar
				+ parent6.getLocation().y // offset of ?
				+ CURSOR_HEADER_HEIGHT;

		// Create a mask that shows an arrow at the cursor position and a
		// vertical line of 1 pixel wide
		ImageData maskData = new ImageData(CURSOR_WIDTH, cursorHeight, 1,
				palette);
		maskData.setPixels(0, offset - (CURSOR_ARROW_HEIGHT / 2),
				ARROW_MASK.length, ARROW_MASK, 0);
		for (int j = 0; j < cursorHeight; j++) {
			maskData.setPixel(0, j, 1);
		}

		// Add mask data to the image
		// This is the only way to get a colored (not black/white) cursor
		// (if the platform supports it).
		cursorData.maskData = maskData.data;
		cursorData.maskPad = maskData.scanlinePad;

		// Set the updated cursor
		if (cursor != null) {
			cursor.dispose();
		}
		cursor = new Cursor(currentWidget.getDisplay(), cursorData, 0, offset);
		currentWidget.setCursor(cursor);
	}
}
