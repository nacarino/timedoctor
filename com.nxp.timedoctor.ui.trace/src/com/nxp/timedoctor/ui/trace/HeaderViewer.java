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

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;

import com.nxp.timedoctor.core.model.SampleCPU;
import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.ZoomModel;

/**
 * Header view containing a logo canvas (for eventual product logo as well as
 * spacing) and a ruler, with a disabled sash between them to synchronize
 * position with the sash in the lower pane.
 */
public class HeaderViewer extends Composite implements ISashClient, Observer {

	private static final int LOGO_FONT_SIZE = 8;
	
	/**
	 * Constant for use in form layouts, to indicate that a given attachment is
	 * to be at 100% of the parent's client area.
	 */
	private static final int FORMLAYOUT_FULL = 100;

	/**
	 * The logo composite, for spacing in the header (and eventual presence of a
	 * logo).
	 */
	private Text logo;

	/**
	 * The header sash, for synchronization with lower pane's sash.
	 */
	private Sash headerSash;

	/**
	 * The sash listener to synchronize the two sashes.
	 */
	private SashSyncListener sashListener;

	/**
	 * The ruler canvas.
	 */
	private Canvas ruler;

	private ZoomModel zoom;
	
	/**
	 * Constructs a header view in the given parent, and creates the contents of
	 * the header.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param data
	 *            <code>Observable</code> containing zoom and scroll data
	 */
	public HeaderViewer(final Composite parent, 
			final TraceCursorFactory traceCursorFactory,
			final ZoomModel zoom) {
		super(parent, SWT.NONE);
		this.zoom = zoom;

		setLayout(new FormLayout());
		createContents(parent, traceCursorFactory);

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
	private void createContents(final Composite parent,
			final TraceCursorFactory traceCursorFactory) {
		logo = new Text(this, SWT.LEFT | SWT.MULTI | SWT.READ_ONLY);
			
		logo.setForeground(logo.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		logo.setFont(new Font(getDisplay(), "Tahoma",
				LOGO_FONT_SIZE, SWT.NORMAL));
		
		headerSash = new Sash(this, SWT.VERTICAL);
		FormData sashFormData = new FormData();
		sashFormData.top = new FormAttachment(0);
		sashFormData.bottom = new FormAttachment(FORMLAYOUT_FULL);
		headerSash.setLayoutData(sashFormData);
		
		sashListener = new SashSyncListener(this, null, SWT.VERTICAL, false);
		headerSash.addSelectionListener(sashListener);
		headerSash.addMouseListener(sashListener);

		FormData logoFormData = new FormData();
		logoFormData.left = new FormAttachment(0);
		logoFormData.top = new FormAttachment(0);
		logoFormData.bottom = new FormAttachment(FORMLAYOUT_FULL);
		logoFormData.right = new FormAttachment(headerSash);
		logo.setLayoutData(logoFormData);
		
		// Ruler pane
		Composite rulerPane = new Composite(this, SWT.NONE);
		rulerPane.setLayout(new FormLayout());

		FormData rulerPaneFormData = new FormData();
		rulerPaneFormData.left = new FormAttachment(headerSash);
		rulerPaneFormData.right = new FormAttachment(FORMLAYOUT_FULL);
		rulerPaneFormData.top = new FormAttachment(0);
		rulerPaneFormData.bottom = new FormAttachment(FORMLAYOUT_FULL);
		rulerPane.setLayoutData(rulerPaneFormData);
		
		traceCursorFactory.setRulerPane(rulerPane);

		// ruler
		ruler = new Canvas(rulerPane, SWT.DOUBLE_BUFFERED);
		FormData rulerFormData = new FormData();
		rulerFormData.left = new FormAttachment(0);
		rulerFormData.right = new FormAttachment(FORMLAYOUT_FULL);
		rulerFormData.top = new FormAttachment(0);
		rulerFormData.bottom = new FormAttachment(FORMLAYOUT_FULL);
		ruler.setLayoutData(rulerFormData);

		// Paint ruler
		RulerPaintListener rulerPaintListener =
			new RulerPaintListener(zoom);
		ruler.addPaintListener(rulerPaintListener);
	}

	// Handle sash between left and right panes

	/**
	 * Adds the given SashClient to its sash listener for synchronization.
	 * 
	 * @param client
	 *            the client to be added
	 */
	public final void addSashClient(final ISashClient client) {
		sashListener.addClient(client);
	}

	/**
	 * Returns the current size of the logo composite, which determines the
	 * sash's minimum x-position.
	 * 
	 * @return the minimum sash offset
	 */
	public final int getMinSashOffset() {
		return 0;
	}

	/**
	 * Sets the sash offset.
	 * 
	 * @param offset
	 *            sets the sash offset from the left of the client area.
	 */
	public final void setSashOffset(final int offset) {
		((FormData) headerSash.getLayoutData()).left = new FormAttachment(0, offset);
		layout(true);
		update();
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
		SampleLine selectedLine = zoom.getSelectedLine();
		if (selectedLine != null) {
			SampleCPU cpu = selectedLine.getCPU();
			
			String cpuFreqStr = String.format("CPU freq: %.0fHz", cpu.getClocksPerSec());
			String memFreqStr = String.format("\nMemory freq: %.0fHz", cpu.getMemClocksPerSec());
			logo.setText(cpuFreqStr + memFreqStr);
		}
	}
}
