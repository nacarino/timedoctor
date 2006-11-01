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

import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Slider;

import com.nxp.timedoctor.core.model.Section;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.model.SampleLine.LineType;
import com.nxp.timedoctor.ui.trace.TraceCursorFactory.CursorType;

/**
 * The main view, containing sashes, sections, labels, and traces. Vertical
 * scrolling is automatic when the content is larger than the client area.
 */
public class MainViewer extends Composite implements ISashClient, Observer {

	/**
	 * Constant for use in FormAttachments to indicate the percentage should be
	 * full right or bottom.
	 */
	private static final int FORMLAYOUT_FULL = 100;
	
	/**
	 * Horizontal scrollbar settings.
	 * Use a large number for the maximum range for accuracy in translating to time units
	 */
	private static final int HOR_SCROLL_MAX = 1000000;
	private static final int HOR_SCROLL_INCREMENT = 10000;
	private static final int HOR_SCROLL_PAGE = 100000;
	
	/**
	 * Array of colors to be used in setting section header colors based on
	 * section type. Indexed by type ordinal.
	 */
	private static final RGB[] COLORS = { Colors.DARK_BLUE, Colors.DARK_GREEN,
			Colors.DARK_VIOLET, Colors.DARK_RED, Colors.DARK_MAGENTA,
			Colors.DARK_CYAN, Colors.DARK_CYAN, Colors.DARK_GOLDENROD,
			Colors.SEA_GREEN, Colors.DARK_CYAN };

	/**
	 * The left pane, containing labels and collapsible header bars.
	 */
	private Composite leftPane;

	/**
	 * The right pane, containing traces and sashes.
	 */
	private Composite rightContent;

	/**
	 * Scrolled composite to handle the scrolling of trace lines. Automatically
	 * synchronizes the left pane.
	 */
	private ScrolledComposite rightScroll;

	/**
	 * A slider to serve as the horizontal scrollbar, allowing horizontal
	 * scrolling to be implemented manually and vertical scrolling to be done
	 * automatically.
	 */
	private Slider horizontalScroll;

	/**
	 * The listener to synchronize the main sash with the header sash.
	 */
	private SashSyncListener sashListener;

	/**
	 * The model from which to retrieve data.
	 */
	private TraceModel model;

	/**
	 * Model component containing data on the zoom factor and horizontal offset
	 * due to scrolling of trace lines.
	 */
	private ZoomModel zoom;
		
	/** 
	 * Zoom factor multiplied by MAX_HOR_SCROLL.
	 * Used to check if the zoomFactor has changed 
	 * and the horizontal scrollbar needs to be updated.
	 */
	private int zoomPercentage = 0;
	
	/**
	 * Constructs the MainViewer in the given parent, setting up vertical
	 * scrolling and creating the contents.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param model
	 *            the model containing trace data to display
	 * @param zoom
	 *            the model component containing zoom data for this trace
	 */
	public MainViewer(final Composite parent, 
			TraceCursorFactory traceCursorFactory,
			final TraceModel model,
			final ZoomModel zoom) {
		super(parent, SWT.NONE);

		this.model = model;
		this.zoom = zoom;
		
		zoom.setTimes(0, model.getEndTime() / 2);
		zoom.addObserver(this);

		createContents(parent, traceCursorFactory);
	}

	/**
	 * Creates the contents of the MainViewer and lays them out.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createContents(final Composite parent,
			TraceCursorFactory traceCursorFactory) {
		setLayout(new FormLayout());
		
		leftPane = new Composite(this, SWT.NONE);
		leftPane.setLayout(new FormLayout());

		FormData data = new FormData();
		data.left = new FormAttachment(0);
		data.top = new FormAttachment(0);
		data.bottom = new FormAttachment(FORMLAYOUT_FULL);
		leftPane.setLayoutData(data);

		Sash mainSash = new Sash(this, SWT.VERTICAL);
		data = new FormData();
		data.left = new FormAttachment(leftPane);
		data.top = new FormAttachment(0);
		data.bottom = new FormAttachment(FORMLAYOUT_FULL);
		mainSash.setLayoutData(data);

		sashListener = new SashSyncListener(this, null, SWT.VERTICAL, false);
		mainSash.addSelectionListener(sashListener);
		mainSash.addMouseListener(sashListener);

		Composite rightPane = new Composite(this, SWT.NONE);
		data = new FormData();
		data.left = new FormAttachment(mainSash);
		data.right = new FormAttachment(FORMLAYOUT_FULL);
		data.top = new FormAttachment(0);
		data.bottom = new FormAttachment(FORMLAYOUT_FULL);
		rightPane.setLayoutData(data);

		rightPane.setLayout(new FormLayout());

		rightScroll = new ScrolledComposite(rightPane, SWT.V_SCROLL);
		horizontalScroll = new Slider(rightPane, SWT.HORIZONTAL);

		data = new FormData();
		data.top = new FormAttachment(0);
		data.bottom = new FormAttachment(horizontalScroll);
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(FORMLAYOUT_FULL);
		rightScroll.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(FORMLAYOUT_FULL);
		data.bottom = new FormAttachment(FORMLAYOUT_FULL);
		horizontalScroll.setLayoutData(data);

		rightContent = new Composite(rightScroll, SWT.NONE);
		rightContent.setLayout(new FormLayout());

		// Create cursor and baseline
		traceCursorFactory.setTracePane(rightContent);
		TimeLine traceCursor = traceCursorFactory.createTraceCursor(CursorType.CURSOR);
		TimeLine baseLine = traceCursorFactory.createTraceCursor(CursorType.BASELINE);
		TraceCursorListener traceCursorListener = new TraceCursorListener(traceCursorFactory, traceCursor, baseLine, zoom);
		
		createTraceLines(leftPane, rightContent, traceCursorListener);

		initializeScrollbars();		
	}

	// Handle sash between left and right panes

	/**
	 * Creates the trace lines (label and canvas) in the main view.
	 * 
	 * @param left
	 *            the left (label) composite
	 * @param right
	 *            the right (canvas) composite
	 * @param traceSelectListener 
	 * 			  The TraceSelectListener object
	 */
	private void createTraceLines(final Composite left,
			final Composite right,
			final TraceCursorListener traceCursorListener) {
		// Checkstyle incompatible with J2SE5 type parameterization
		Collection < Section > sections = model.getSections().values();
		SectionViewer lastSection = null;
		if (sections.size() != 0) {
			Iterator < Section > iter = sections.iterator();
			for (int i = 0; i < sections.size(); i++) {
				Section s = iter.next();
				if (s.getType() != LineType.PORTS) {
					boolean last = (i == (sections.size() - 1));
					SectionViewer section = new SectionViewer(left, right,
							lastSection, last, s, zoom, model, traceCursorListener);
					LineType type = s.getType();
					section.setHeaderText(type.toString());
					section.setHeaderColor(new Color(getDisplay(), COLORS[type
							.ordinal()]));
					lastSection = section;
				}
			}
		}
	}

	/**
	 * Initializes vertical and horizontal scrolling.
	 */
	private void initializeScrollbars() {
		rightScroll.setMinWidth(0);
		rightScroll.setExpandHorizontal(true);
		rightScroll.setMinHeight(rightContent.computeSize(SWT.DEFAULT,
				SWT.DEFAULT).y);
		rightScroll.setExpandVertical(true);
		rightScroll.setContent(rightContent);

		intializeVerticalScroll();
		initializeHorizontalScroll();
	}

	/**
	 * Initializes automatic vertical scrolling in the scrolled composite.
	 */
	private void intializeVerticalScroll() {
		rightScroll.setData(leftPane);

		final ScrollBar rightScrollBar = rightScroll.getVerticalBar();

		rightScrollBar.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			public void widgetSelected(final SelectionEvent e) {
				int selection = rightScrollBar.getSelection();
				setVerticalScroll(selection);
			}
		});

		rightScroll.addControlListener(new ControlListener() {
			public void controlResized(final ControlEvent e) {
				ScrollBar bar = ((ScrolledComposite) e.widget).getVerticalBar();
				int selection = 0;
				if (bar.getVisible()) {
					selection = rightScrollBar.getSelection();
				}
				setVerticalScroll(selection);
			}

			public void controlMoved(final ControlEvent e) {
			}
		});
	}

	private void setVerticalScroll(final int selection) {
		((FormData) leftPane.getLayoutData()).top = new FormAttachment(0,
				0 - selection);
		leftPane.getParent().layout(false);
	}
	
	/**
	 * Initializes horizontal scrolling using a manual slider and updating the
	 * start and end times of the visible portion of the trace lines.
	 */
	private void initializeHorizontalScroll() {
		horizontalScroll.setMaximum(HOR_SCROLL_MAX);
		horizontalScroll.setIncrement(HOR_SCROLL_INCREMENT);
		horizontalScroll.setPageIncrement(HOR_SCROLL_PAGE);
		setHorizontalScroll();
		
		horizontalScroll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				Slider slider = (Slider) e.widget;

				int selection = slider.getSelection();

				// Update zoom with selection
				double oldStartTime = zoom.getStartTime();
				double oldEndTime = zoom.getEndTime();
				double interval = oldEndTime - oldStartTime;
				
				double startTime = selection * model.getEndTime() / ((double) HOR_SCROLL_MAX);
				double endTime = startTime + interval;
				zoom.setTimes(startTime, endTime);
			}
		});
	}

	/**
	 * Adds a SashClient to its sash listener for sash synchronization.
	 * 
	 * @param client
	 *            the client to be added
	 */
	public final void addSashClient(final ISashClient client) {
		sashListener.addClient(client);
	}

	/**
	 * Returns the minimum sash offset from the left of the parent's client
	 * area.
	 * 
	 * @return the minimum sash offset in pixels
	 */
	public final int getMinSashOffset() {
		return leftPane.computeSize(SWT.DEFAULT, SWT.DEFAULT, false).x;
	}

	/**
	 * Sets the sash offset to the given value.
	 * 
	 * @param offset
	 *            the offset in pixels from the left of the parent's client area
	 */
	public final void setSashOffset(final int offset) {
		((FormData) leftPane.getLayoutData()).width = offset;
		layout(true);
	}

	/**
	 * Updates scrolling when the zoom or scroll is changed by another part of
	 * the view.
	 * 
	 * @param o
	 *            the <code>Observable</code> calling the update
	 * @param data
	 *            has no effect
	 */
	public final void update(final Observable o, final Object data) {
		setHorizontalScroll();
	}

	private void setHorizontalScroll() {
		// MR would be more accurate and faster to store the zoom factor in the zoomModel
		double modelEndTime = model.getEndTime();
		double zoomEndTime = zoom.getEndTime();
		double zoomStartTime = zoom.getStartTime();
		double zoomInterval = zoomEndTime - zoomStartTime;		
		double zoomFactor = zoomInterval / modelEndTime;
		int newZoomPercentage = (int) (zoomFactor * ((double) HOR_SCROLL_MAX));
		int selection = (int) (zoomStartTime * ((double) HOR_SCROLL_MAX) / modelEndTime);
		
		// Should only be executed on zoom, not on scroll to 
		// avoid ping-pong between update and the scrollbar selection listener
		if (newZoomPercentage != zoomPercentage) {
			zoomPercentage = newZoomPercentage;
			if (zoomPercentage >= HOR_SCROLL_MAX) {
				horizontalScroll.setVisible(false);
			} else {
				horizontalScroll.setVisible(true);
			}
		}
		
		// Side effect: calls scrollbar selection listener
		horizontalScroll.setThumb(zoomPercentage);
		horizontalScroll.setSelection(selection);		
	}
}
