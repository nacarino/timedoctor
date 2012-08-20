/*******************************************************************************
 * Copyright (c) 2006-2012 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.ui.trace;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.SampleLine.LineType;

/**
 * Class to get cpu labels as images
 */
public class CPULabel extends CLabel {
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
	
	private SampleLine line;

	private final Color[] color = new Color[2];

	private Image cpuLabelImage = null;

	/**
	 * Constructor to create the CpuLabel
	 * 
	 * @param parent
	 *            Parent composite
	 * @param line
	 *            The {@link SampleLine} object for drawing the label
	 */
	public CPULabel(final Composite parent, final SampleLine line) {
		super(parent, SWT.NONE);
		
		this.parent = parent;
		this.line   = line;
		
		setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		setFont(Fonts.getFontRegistry().get(Fonts.TRACE_LABEL_FONT));
		
		setText(line.getName());
		cpuLabelImage = createLabelImage();
		setImage(cpuLabelImage);
		
		if (cpuLabelImage != null) {
			addDisposeListener(new DisposeListener(){
				public void widgetDisposed(DisposeEvent e) {
					releaseResources();
				}				
			});
		}
	}

	private void releaseResources() {
		if (cpuLabelImage != null && !cpuLabelImage.isDisposed()) {
			cpuLabelImage.dispose();
		}
	}

	/**
	 * Creates the image
	 */
	private Image createLabelImage() {
		if ((line.getCPU() == null) || (line.getCPU().getName() == null)) {			
			return null;
		}
		
		initializeImageColors(line.getType());
		final String cpuName = line.getCPU().getName();
		
		final Point point = getCpuNameBoudingBox(cpuName);
		final int  width  = point.x + HOR_SPACE;
		final int  height = point.y + VERT_SPACE;		
		
		final Font  font  = Fonts.getFontRegistry().getItalic(Fonts.TRACE_ICON_FONT);		
		final Image image = new Image(parent.getDisplay(), width, height);
		
		GC gc = new GC(image);
		gc.setAntialias(SWT.ON);
		gc.setFont(font);
		gc.setBackground(color[0]);
	
		// draws a filled rectangle with border color : color[0]
		gc.drawRoundRectangle(0, 0, width - 1, height - 1, ARC_WIDTH_HEIGHT, ARC_WIDTH_HEIGHT);
		gc.setBackground(color[1]);
	
		// draws a filled rectangle inside previous rectangle
		gc.fillRoundRectangle(1, 1, width - 2, height - 2, ARC_WIDTH_HEIGHT - 2, ARC_WIDTH_HEIGHT - 2);
		gc.setForeground(color[0]);
	
		// Writes the CPU name with the border color
		gc.drawString(cpuName, 2, 2);
		gc.dispose();
	
		PaletteData palette = new PaletteData(new RGB[] {new RGB(0x0, 0x0, 0x0), new RGB(0xff, 0xff, 0xff) });
		ImageData maskData = new ImageData(width, height, 1, palette);
	
		ImageData imageData = image.getImageData();
		Image mask = new Image(parent.getDisplay(), maskData);
		gc = new GC(mask);
		gc.setAntialias(SWT.ON);
		gc.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(color[0]);
		gc.fillRoundRectangle(0, 0, width, height, ARC_WIDTH_HEIGHT, ARC_WIDTH_HEIGHT);
		maskData = mask.getImageData();
		
		//Free up the image and gc resources
		image.dispose();
		mask.dispose();
		gc.dispose();
		
		return new Image(parent.getDisplay(), imageData, maskData);
	}
	
	private Point getCpuNameBoudingBox(final String cpuName) {
		Point point;
		
		final Font  font  = new Font(this.parent.getDisplay(), "Arial", ICON_FONT_SIZE, SWT.NORMAL);
		final Image image = new Image(this.parent.getDisplay(), 1, 1);
		
		GC gc = new GC(image);
		gc.setFont(font);
		point = gc.textExtent(cpuName);
		
		//free resources
		image.dispose();
		font.dispose();
		gc.dispose();
		
		return point;
	}

	private void initializeImageColors(final LineType type) {
		switch (type) {
		case TASKS:
			color[0] = Colors.getColorRegistry().get(Colors.DARK_BLUE);
			color[1] = Colors.getColorRegistry().get(Colors.LIGHT_BLUE);
			break;
		case ISRS:
			color[0] = Colors.getColorRegistry().get(Colors.DARK_GREEN);
			color[1] = Colors.getColorRegistry().get(Colors.MINT_CREAM);
			break;
		case SEMAPHORES:
			color[0] = Colors.getColorRegistry().get(Colors.DARK_VIOLET);
			color[1] = Colors.getColorRegistry().get(Colors.THISTLE);
			break;
		case QUEUES:
			color[0] = Colors.getColorRegistry().get(Colors.DARK_RED);
			color[1] = Colors.getColorRegistry().get(Colors.MISTY_ROSE);
			break;
		case EVENTS:
			color[0] = Colors.getColorRegistry().get(Colors.DARK_MAGENTA);
			color[1] = Colors.getColorRegistry().get(Colors.LIGHT_PINK);
			break;
		case VALUES:
			color[0] = Colors.getColorRegistry().get(Colors.DARK_MAGENTA);
			color[1] = Colors.getColorRegistry().get(Colors.LIGHT_CYAN);
			break;
		case CYCLES:
			color[0] = Colors.getColorRegistry().get(Colors.DARK_MAGENTA);
			color[1] = Colors.getColorRegistry().get(Colors.LIGHT_CYAN);
			break;
		case NOTES:
			color[0] = Colors.getColorRegistry().get(Colors.DARK_GOLDENROD);
			color[1] = Colors.getColorRegistry().get(Colors.PALE_GOLDENROD);
			break;
		case AGENTS:
			color[0] = Colors.getColorRegistry().get(Colors.SEA_GREEN);
			color[1] = Colors.getColorRegistry().get(Colors.MINT_CREAM);
			break;
		case MEM_CYCLES:
			color[0] = Colors.getColorRegistry().get(Colors.DARK_MAGENTA);
			color[1] = Colors.getColorRegistry().get(Colors.LIGHT_CYAN);
			break;
		case PORTS:
			color[0] = Colors.getColorRegistry().get(Colors.DARK_MAGENTA);
			color[1] = Colors.getColorRegistry().get(Colors.LIGHT_CYAN);
			break;
		default:
		color[0] = null;
		color[1] = null;
		break;
		}
	}
}
