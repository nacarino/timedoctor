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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.SampleLine.LineType;

/**
 * Class to get cpu labels as images
 */
public class CpuLabel {
	/**
	 * Font size used in the icon images to display CPU name
	 */
	private static final int ICON_FONT_SIZE = 6;

	/**
	 * Vertical spacing size in the CPU Label design
	 */
	private static final int VERT_SPACE = 4;

	/**
	 * Horizontal spacing size in the CPU Label design
	 */
	private static final int HOR_SPACE = 6;

	/**
	 * CPU icons Arc width and Arc Height
	 */
	private static final int ARC_WIDTH_HEIGHT = 6;

	private Composite parent;

	private String cpuName;

	private final Color[] color = new Color[2];

	private Image cpuLabelImage = null;

	/**
	 * Constructor to create the CpuLabel
	 * 
	 * @param parent
	 *            Parent composite
	 * @param lineType
	 *            type of sample line for the cpu (TASKS, QUEUES, etc.)
	 * @param cpuName
	 *            name of the cpu
	 */
	public CpuLabel(final Composite parent, final LineType lineType,
			final String cpuName) {
		this.parent = parent;
		this.cpuName = cpuName;

		setImageColors(lineType);
		createLabelImage();
	}

	/**
	 * Returns the cpuLabel as an image
	 * 
	 * @return returns image
	 */
	public Image getImage() {
		return cpuLabelImage;
	}

	/**
	 * Creates the image
	 */
	private void createLabelImage() {
		final Point point = getCpuNameBoudingBox();
		int width = point.x + HOR_SPACE;
		int height = point.y + VERT_SPACE;

		drawCpuLabelImage(width, height);
	}

	/**
	 * This method creates cpu Labels for different sections
	 * 
	 * @param parent
	 *            Labels composite.
	 * @param cpuName
	 *            Name of the cpu present in the input line.
	 * @param width
	 *            width of the cpu name.
	 * @param height
	 *            height of the cpu name.
	 * @return the created the icon.
	 */
	private void drawCpuLabelImage(final int width, final int height) {
		final Font font = new Font(parent.getDisplay(), "Tahoma",
				ICON_FONT_SIZE, SWT.ITALIC);
		
		Image image = new Image(parent.getDisplay(), width, height);
		GC gc = new GC(image);
		gc.setAntialias(SWT.ON);
		gc.setFont(font);
		gc.setBackground(color[0]);
	
		// draws a filled rectangle with border color : color[0]
		gc.drawRoundRectangle(0, 0, width - 1, height - 1, ARC_WIDTH_HEIGHT,
				ARC_WIDTH_HEIGHT);
		gc.setBackground(color[1]);
	
		// draws a filled rectangle inside previous rectangle
		gc.fillRoundRectangle(1, 1, width - 2, height - 2,
				ARC_WIDTH_HEIGHT - 2, ARC_WIDTH_HEIGHT - 2);
		gc.setForeground(color[0]);
	
		// Writes the CPU name with the border color
		gc.drawString(cpuName, 2, 2);
		gc.dispose();
	
		PaletteData palette = new PaletteData(new RGB[] {
				new RGB(0x0, 0x0, 0x0), new RGB(0xff, 0xff, 0xff) });
		ImageData maskData = new ImageData(width, height, 1, palette);
	
		ImageData imageData = image.getImageData();
		Image mask = new Image(parent.getDisplay(), maskData);
		gc = new GC(mask);
		gc.setAntialias(SWT.ON);
		gc
				.setBackground(parent.getDisplay().getSystemColor(
						SWT.COLOR_WHITE));
		gc.setForeground(color[0]);
		gc.fillRoundRectangle(0, 0, width, height, ARC_WIDTH_HEIGHT,
				ARC_WIDTH_HEIGHT);
		gc.dispose();
		maskData = mask.getImageData();
	
		cpuLabelImage = new Image(parent.getDisplay(), imageData, maskData);
	}

	/**
	 * Returns the Point co-ordinates for drawing the cpu name
	 * 
	 * @return Point
	 */
	private Point getCpuNameBoudingBox() {
		Point point;
		final Font font = new Font(this.parent.getDisplay(), "Arial",
				ICON_FONT_SIZE, SWT.NORMAL);
		Image image = new Image(this.parent.getDisplay(), 1, 1);
		GC gc = new GC(image);
		gc.setFont(font);
		point = gc.textExtent(this.cpuName);
		image.dispose();
		gc.dispose();
		
		return point;
	}

	/**
	 * Finds the corresponding Color for drawing cpu labels
	 * 
	 * @param type
	 * 		The type of sample line (TASKS, QUEUES, etc.)
	 */
	private void setImageColors(final LineType type) {
		switch (type) {
		case TASKS:
			color[0] = new Color(parent.getDisplay(), Colors.DARK_BLUE);
			color[1] = new Color(parent.getDisplay(), Colors.LIGHT_BLUE);
			break;
		case ISRS:
			color[0] = new Color(parent.getDisplay(), Colors.DARK_GREEN);
			color[1] = new Color(parent.getDisplay(), Colors.MINT_CREAM);
			break;
		case SEMAPHORES:
			color[0] = new Color(parent.getDisplay(), Colors.DARK_VIOLET);
			color[1] = new Color(parent.getDisplay(), Colors.THISTLE);
			break;
		case QUEUES:
			color[0] = new Color(parent.getDisplay(), Colors.DARK_RED);
			color[1] = new Color(parent.getDisplay(), Colors.MISTY_ROSE);
			break;
		case EVENTS:
			color[0] = new Color(parent.getDisplay(), Colors.DARK_MAGENTA);
			color[1] = new Color(parent.getDisplay(), Colors.LIGHT_PINK);
			break;
		case VALUES:
			color[0] = new Color(parent.getDisplay(), Colors.DARK_MAGENTA);
			color[1] = new Color(parent.getDisplay(), Colors.LIGHT_CYAN);
			break;
		case CYCLES:
			color[0] = new Color(parent.getDisplay(), Colors.DARK_MAGENTA);
			color[1] = new Color(parent.getDisplay(), Colors.LIGHT_CYAN);
			break;
		case NOTES:
			color[0] = new Color(parent.getDisplay(), Colors.DARK_GOLDENROD);
			color[1] = new Color(parent.getDisplay(), Colors.PALE_GOLDENROD);
			break;
		case AGENTS:
			color[0] = new Color(parent.getDisplay(), Colors.SEA_GREEN);
			color[1] = new Color(parent.getDisplay(), Colors.MINT_CREAM);
		case MEM_CYCLES:
			color[0] = new Color(parent.getDisplay(), Colors.DARK_MAGENTA);
			color[1] = new Color(parent.getDisplay(), Colors.LIGHT_CYAN);
			break;
		case PORTS:
			color[0] = new Color(parent.getDisplay(), Colors.DARK_MAGENTA);
			color[1] = new Color(parent.getDisplay(), Colors.LIGHT_CYAN);
			break;
		}
	}
}
