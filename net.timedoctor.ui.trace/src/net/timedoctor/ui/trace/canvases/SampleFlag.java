/*******************************************************************************
 * Copyright (c) 2006-2013 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.ui.trace.canvases;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

/**
 * Contains the code to draw flags.
 */
final class SampleFlag {

    /**
     * The width of the flag
     */
    private static final double FLAG_WIDTH = 3.4;

    /**
     * Draws a flag at the specified position. This function is called to draw a
     * start/stop flag for semaphore, event and note.
     * @param e
     *            the paint event object
     * @param color
     *            the color used to draw the border of the flag
     * @param fillColor
     *            the color used to fill the flag
     * @param x
     *            the X co-ordinate where the flag is to be drawn
     * @param y
     *            the Y co-ordinate where the flag is to be drawn 
     * @param height
     * 			  the height of the flag
     * 
     */
    public void draw(final PaintEvent e, final Color color,
            final Color fillColor, final int x, final int y, final int height) {

        int[] points = { x, y, x + (int) (2 * FLAG_WIDTH),
                y + (int) (FLAG_WIDTH), x, y + (int) (2 * FLAG_WIDTH) };

        e.gc.setForeground(color);
        e.gc.setBackground(fillColor);

        e.gc.fillPolygon(points);
        e.gc.drawPolygon(points);
        e.gc.drawLine(x, y, x, y + height);
    }
}
