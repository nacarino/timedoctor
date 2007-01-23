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
package com.nxp.timedoctor.ui.statistics;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.model.statistics.StatisticsTimeModel;
import com.nxp.timedoctor.core.model.statistics.TraceStatistic;

//TODO: button to select between seconds, cycles, or %
public class TraceStatViewer {
	private TraceModel traceModel;
	private ZoomModel zoomModel;
	private StatisticsTimeModel timeModel;
	
	private StatTimeViewer timeViewer;
	private TraceStatTableViewer tableViewer;
	
	private Observer timeObserver = null;
	
	/**
	 * The constructor.
	 */
	public TraceStatViewer(final Composite parent) {
		GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginHeight = 0;
		parentLayout.marginWidth = 0;
		parentLayout.verticalSpacing = 0;
		parent.setLayout(parentLayout);
		
		timeViewer = new StatTimeViewer(parent);
		timeViewer.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		
		tableViewer = new TraceStatTableViewer(parent);
		tableViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer.setInput(null);
	}

	public void setModels(final TraceModel traceModel, 
			final ZoomModel zoomModel,
			final StatisticsTimeModel timeModel) {
		this.traceModel = traceModel;
		this.zoomModel = zoomModel;
		this.timeModel = timeModel;

		timeViewer.setTimeModel(timeModel);
		
		if ((traceModel != null) && (timeModel != null)) {		
			tableViewer.setInput(createTraceStatistic());
			selectionChanged();
		} else {
			tableViewer.setInput(null);
			tableViewer.refresh();
		}					
	}
	
	public void selectionChanged() {
		if ((zoomModel == null) || (traceModel == null) || (timeModel == null)) {
			return;
		}
		
		// TODO check ISelection on what has changed, only update accordingly
		// Now, assume the ISelection event is sent when the line has changed, or the selected
		// zoom window has changed
		timeModel.setTimes(zoomModel.getStartTime(), zoomModel.getEndTime());
	}
	

	private TraceStatistic createTraceStatistic() {
		if (timeObserver != null) {
			timeModel.deleteObserver(timeObserver);
		}
		
		final TraceStatistic traceStat = new TraceStatistic(traceModel);	
		
		// Recalculate when the start and end times change, either by ISelection events,
		// or by local events from the StatTimeViewer
		timeObserver = new Observer() {
			public void update(final Observable o, final Object arg) {
				// Show statistics for the current zoom range
				traceStat.calculate(timeModel.getStartTime(), timeModel
						.getEndTime());										
				tableViewer.refresh();
			}
		};
		timeModel.addObserver(timeObserver);
		
		return traceStat;
	}
}
