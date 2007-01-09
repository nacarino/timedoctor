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

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Slider;

import com.nxp.timedoctor.core.model.Section;
import com.nxp.timedoctor.core.model.SectionList;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.model.SampleLine.LineType;
import com.nxp.timedoctor.ui.trace.TraceCursorFactory.CursorType;

/**
 * The main view, containing sashes, sections, labels, and traces. Vertical
 * scrolling is automatic when the content is larger than the client area.
 */
public class MainViewer implements IScrollClient, Observer {
	
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
	private Composite leftContent;

	/**
	 * The right pane, containing traces and sashes.
	 */
	private Composite rightContent;

	/**
	 * Scrolled composite to handle the scrolling of trace lines. Automatically
	 * synchronizes the left pane.
	 */
	private ScrolledComposite verticalScroll;

	/**
	 * A slider to serve as the horizontal scrollbar, allowing horizontal
	 * scrolling to be implemented manually and vertical scrolling to be done
	 * automatically.
	 */
	private Slider horizontalScroll;

	/**
	 * The traceModel from which to retrieve data.
	 */
	private TraceModel traceModel;

	/**
	 * Model component containing data on the zoomModel factor and horizontal offset
	 * due to scrolling of trace lines.
	 */
	private ZoomModel zoomModel;
		
	/** 
	 * Zoom factor multiplied by MAX_HOR_SCROLL.
	 * Used to check if the zoomFactor has changed 
	 * and the horizontal scrollbar needs to be updated.
	 */
	private int zoomPercentage = 0;
	
	private ArrayList<SectionViewer> sectionViewerArrayList;
	
	/**
	 * Constructs the MainViewer in the given parent, setting up vertical
	 * scrolling and creating the contents.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param traceModel
	 *            the traceModel containing trace data to display
	 * @param zoomModel
	 *            the traceModel component containing zoomModel data for this trace
	 */
	public MainViewer(final Composite leftPane,
			final Composite rightPane, 
			final TraceCursorFactory traceCursorFactory,
			final TraceModel traceModel,
			final ZoomModel zoomModel) {
		this.traceModel = traceModel;
		this.zoomModel = zoomModel;
		
		sectionViewerArrayList = new ArrayList<SectionViewer>();
		
		zoomModel.setTimes(0, traceModel.getEndTime() / 2);
		zoomModel.addObserver(this);

		createContents(leftPane, rightPane, traceCursorFactory);
	}

	/**
	 * Creates the contents of the MainViewer and lays them out.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createContents(final Composite leftPane,
			final Composite rightPane,
			final TraceCursorFactory traceCursorFactory) {
		leftContent = new Composite(leftPane, SWT.NONE);
		GridLayout leftContentLayout = new GridLayout(1, false);
		leftContentLayout.marginHeight = 0;
		leftContentLayout.marginWidth = 0;
		leftContentLayout.verticalSpacing = 0;
		leftContent.setLayout(leftContentLayout);
				
		leftContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	
		leftContent.setBackground(leftContent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		verticalScroll = new ScrolledComposite(rightPane, SWT.V_SCROLL);
		verticalScroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		rightContent = new Composite(verticalScroll, SWT.NONE);
		GridLayout rightContentLayout = new GridLayout(1, false);
		rightContentLayout.marginHeight = 0;
		rightContentLayout.marginWidth = 0;
		rightContentLayout.verticalSpacing = 0;
		rightContent.setLayout(rightContentLayout);

		rightContent.setBackground(rightContent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		horizontalScroll = new Slider(rightPane, SWT.HORIZONTAL);
		horizontalScroll.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));

		// Create cursor and baseline
		traceCursorFactory.setTracePane(rightContent);
		TimeLine traceCursor = traceCursorFactory.createTraceCursor(CursorType.CURSOR);
		TimeLine baseLine = traceCursorFactory.createTraceCursor(CursorType.BASELINE);
		TraceCursorListener traceCursorListener = new TraceCursorListener(traceCursorFactory, traceCursor, baseLine, zoomModel);

		createTraceLines(traceCursorListener);
		
		initializeScrollbars();		
	}

	/**
	 * Creates the trace lines (label and canvas) in the main view.
	 * 
	 * @param traceSelectListener
	 * 			  The TraceSelectListener object
	 */
	private void createTraceLines(final TraceCursorListener traceCursorListener) {
		// Add lines in the order of lineType.
		for (LineType type : LineType.values()) {
			if (type != LineType.PORTS) {
				SectionList sectionList = traceModel.getSections();
				Section s = sectionList.getSection(type);				
				if (s != null) {
					SectionViewer sectionViewer = createSectionViewer(type.toString(), type.ordinal());
					sectionViewer.createTraceLines(leftContent, rightContent,
							s, traceCursorListener);
				}
			}
		}

		// Create extra composite at the bottom of the trace pane,
		// to allow the sash below the last trace line to be dragged down
		Composite traceBottom = new Composite(rightContent, SWT.NONE);
		GridData traceBottomGridData = new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 1);
		traceBottomGridData.heightHint = 1;		
		traceBottom.setLayoutData(traceBottomGridData);
		traceBottom.setBackground(rightContent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	private SectionViewer createSectionViewer(final String headerText,
			final int colorIndex) {
		SectionViewer sectionViewer = new SectionViewer(this, leftContent, rightContent, zoomModel, traceModel);
		sectionViewerArrayList.add(sectionViewer);
		sectionViewer.setHeaderText(headerText);		
		sectionViewer.setHeaderColor(createSectionColor(colorIndex));
		return sectionViewer;
	}

	private Color createSectionColor(final int index) {
		RGB rgb = COLORS[index % COLORS.length];
		return new Color(leftContent.getDisplay(), rgb);
	}
	
	/**
	 * Initializes vertical and horizontal scrolling.
	 */
	private void initializeScrollbars() {
		verticalScroll.setMinWidth(0);
		verticalScroll.setExpandHorizontal(true);
		verticalScroll.setMinHeight(rightContent.computeSize(SWT.DEFAULT,
				SWT.DEFAULT).y);
		verticalScroll.setExpandVertical(true);
		verticalScroll.setContent(rightContent);

		intializeVerticalScroll();
		initializeHorizontalScroll();
	}

	/**
	 * Initializes automatic vertical scrolling in the scrolled composite.
	 */
	private void intializeVerticalScroll() {
		ScrollListener verticalScrollListener = new ScrollListener(this);
		verticalScroll.getVerticalBar().addSelectionListener(verticalScrollListener);
		verticalScroll.addControlListener(verticalScrollListener);
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
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Slider slider = (Slider) e.widget;

				int selection = slider.getSelection();

				// Update zoomModel with selection
				double oldStartTime = zoomModel.getStartTime();
				double oldEndTime = zoomModel.getEndTime();
				double interval = oldEndTime - oldStartTime;
				
				double startTime = selection * traceModel.getEndTime() / (HOR_SCROLL_MAX);
				double endTime = startTime + interval;
				zoomModel.setTimes(startTime, endTime);
			}
		});
	}

	/**
	 * Updates scrolling when the zoomModel or scroll is changed by another part of
	 * the view.
	 * 
	 * @param o
	 *            the <code>Observable</code> calling the update
	 * @param data
	 *            has no effect
	 */
	public final void update(final Observable o, final Object data) {
		setHorizontalScroll();
		updateAutoHide();	
	}

	private void updateAutoHide() {
		for (SectionViewer currentSection : sectionViewerArrayList) {
			currentSection.updateAutoHide();
		}
		layout();
	}
	                               
	private void setHorizontalScroll() {
		// MR would be more accurate and faster to store the zoomModel factor in the zoomModel
		double modelEndTime = traceModel.getEndTime();
		double zoomEndTime = zoomModel.getEndTime();
		double zoomStartTime = zoomModel.getStartTime();
		double zoomInterval = zoomEndTime - zoomStartTime;		
		double zoomFactor = zoomInterval / modelEndTime;
		int newZoomPercentage = (int) (zoomFactor * (HOR_SCROLL_MAX));
		int selection = (int) (zoomStartTime * (HOR_SCROLL_MAX) / modelEndTime);
		
		// Should only be executed on zoomModel, not on scroll to 
		// avoid ping-pong between update and the scrollbar selection listener
		if (newZoomPercentage != zoomPercentage) {
			zoomPercentage = newZoomPercentage;
			GridData horScrollGridData = ((GridData)horizontalScroll.getLayoutData());
			boolean newExclude = (zoomPercentage >= HOR_SCROLL_MAX);
			
			if (horScrollGridData.exclude != newExclude) {
				horScrollGridData.exclude = newExclude; 
				rightContent.getParent().getParent().layout(false);
			}
		}
		
		// Side effect: calls scrollbar selection listener
		horizontalScroll.setThumb(zoomPercentage);
		horizontalScroll.setSelection(selection);		
	}
	
	public void layout() {
		rightContent.layout();
		leftContent.layout();
		
		updateVerticalScrollBar();
		leftContent.update();
	}
	
	/**
	 * Updates the vertical scrollbar to take into account any expand/collapse
	 * events.
	 */
	private void updateVerticalScrollBar() {
		int height = rightContent.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		verticalScroll.setMinHeight(height);
		
		ScrollBar bar = verticalScroll.getVerticalBar();
		int selection = bar.getSelection();
		setScroll(selection);
	}
	
	public void setScroll(final int selection) {
		((GridData) leftContent.getLayoutData()).verticalIndent = - selection;
		leftContent.getParent().layout(false);
	}	
}
