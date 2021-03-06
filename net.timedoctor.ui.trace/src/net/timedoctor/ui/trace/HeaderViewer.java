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
package net.timedoctor.ui.trace;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import net.timedoctor.core.model.SampleCPU;
import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.ZoomModel;

/**
 * Header view containing a logo canvas (for eventual product logo as well as
 * spacing) and a ruler, with a disabled sash between them to synchronize
 * position with the sash in the lower pane.
 */
public class HeaderViewer implements Observer {
	/**
	 * Constant for use in form layouts, to indicate that a given attachment is
	 * to be at 100% of the parent's client area.
	 */
	private static final int FORMLAYOUT_FULL = 100;

	/**
	 * The height of the header in pixels.
	 */
	private static final int HEADER_HEIGHT = 42;
	
	/**
	 * The logo composite, for spacing in the header (and eventual presence of a
	 * logo).
	 */
	private Text logo;

	/**
	 * The ruler canvas.
	 */
	private Canvas ruler;

	private ZoomModel zoom;

	private SampleLine selectedLine = null;
	
	private Composite rulerPane;
	
	/**
	 * Constructs a header view in the given parent, and creates the contents of
	 * the header.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param data
	 *            <code>Observable</code> containing zoom and scroll data
	 */
	public HeaderViewer(final Composite leftPane,
			final Composite rightPane,
			final TraceCursorFactory traceCursorFactory,
			final ZoomModel zoom) {
		this.zoom = zoom;

		createContents(leftPane, rightPane, traceCursorFactory);

		zoom.addObserver(this);
	}

	/**
	 * Creates and lays out the header contents.
	 * 
	 * @param parent
	 *            the composite in which the header is situated.
	 * @param zoomData
	 *            <code>Observable</code> containing current zoom and scroll
	 *            data
	 */
	private void createContents(final Composite leftPane,
			final Composite rightPane,
			final TraceCursorFactory traceCursorFactory) {
		logo = new Text(leftPane, SWT.LEFT | SWT.MULTI | SWT.READ_ONLY);
		GridData logoGridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		logoGridData.heightHint = HEADER_HEIGHT;
		logo.setLayoutData(logoGridData);

		logo.setForeground(logo.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		logo.setFont(Fonts.getFontRegistry().get(Fonts.HEADER_LOGO_FONT));
				
		// Ruler pane
		rulerPane = new Composite(rightPane, SWT.NONE);
		rulerPane.setLayout(new FormLayout());

		GridData rulerPaneGridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		rulerPaneGridData.heightHint = HEADER_HEIGHT;
		rulerPane.setLayoutData(rulerPaneGridData);
		
		traceCursorFactory.setRulerPane(rulerPane);

		// ruler
		ruler = new Canvas(rulerPane, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		FormData rulerFormData = new FormData();
		rulerFormData.left = new FormAttachment(0);
		rulerFormData.right = new FormAttachment(FORMLAYOUT_FULL);
		rulerFormData.top = new FormAttachment(0);
		rulerFormData.bottom = new FormAttachment(FORMLAYOUT_FULL);
		ruler.setLayoutData(rulerFormData);

		ruler.addControlListener(new ControlListener(){

			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				Rectangle bounds = ((Canvas) e.widget).getClientArea();
				zoom.setWidth(bounds.width);
			}
		});
		
		// Paint ruler
		RulerPaintListener rulerPaintListener =
			new RulerPaintListener(zoom);
		ruler.addPaintListener(rulerPaintListener);
	}

	/**
	 * Called when the zoom or scroll changes to redraw the ruler.
	 * 
	 * @param o
	 *            the <code>Observable</code> calling the update
	 * @param data
	 *            has no effect
	 */
	public final void update(final Observable o, final Object data) {		
		updateLogo();
	
		ruler.redraw();
		ruler.update();
	}

	private void updateLogo() {		
		SampleLine line = zoom.getSelectedLine();
		if ((line != null) && (line != selectedLine)) {
			SampleCPU cpu = line.getCPU();
			
			String cpuFreqStr = String.format("CPU freq: %.0fHz", cpu.getClocksPerSec());
			String memFreqStr = String.format("\nMemory freq: %.0fHz", cpu.getMemClocksPerSec());
			logo.setText(cpuFreqStr + memFreqStr);
			
			selectedLine = line;
		}
	}
	
	/**
	 * Returns an {@link Image} containing the screenshot of the current visible
	 * portion
	 * 
	 * @return
	 * 			The {@link Image} screenshot. The image resource must be disposed by the caller.
	 */
	public Image getScreenShot() {
		//Capture logo
		final Point logoSize = logo.getSize();
		final GC logoGc = new GC(logo);
		final Image logoImage = new Image(logo.getDisplay(), logoSize.x, logoSize.y);
		logoGc.copyArea(logoImage, 0, 0);

		//Capture ruler
		final Point rulerSize = rulerPane.getSize();
		final GC rulerGc = new GC(rulerPane);
		final Image rulerImage = new Image(rulerPane.getDisplay(), rulerSize.x, rulerSize.y);
		rulerGc.copyArea(rulerImage, 0, 0);

		//Merge the both
		final Rectangle logoRect = logoImage.getBounds();
		final Rectangle rulerRect = rulerImage.getBounds();

		final Image mergedImage = new Image(rulerPane.getDisplay(), logoRect.width + rulerRect.width, Math.min(logoRect.height, rulerRect.height));
		final GC mergedGc = new GC(mergedImage);
		mergedGc.drawImage(logoImage, 0, 0);
		mergedGc.drawImage(rulerImage, logoRect.width, 0);

		// Dispose resources
		logoGc.dispose();
		logoImage.dispose();

		rulerGc.dispose();
		rulerImage.dispose();

		mergedGc.dispose();

		return mergedImage; //Should be disposed by the caller
	}
}
