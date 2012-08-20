/*******************************************************************************
 * Copyright (c) 2007-2012 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NXP Semiconductors B.V. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.ui.statistics;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.ZoomModel;
import net.timedoctor.core.model.SampleLine.LineType;
import net.timedoctor.core.model.statistics.StatisticsTimeModel;
import net.timedoctor.core.model.statistics.TaskStatistic;
import net.timedoctor.ui.statistics.actions.CopyAction;
import net.timedoctor.ui.statistics.actions.PrintAction;

public class LineStatisticsPage implements IStatisticsViewPage, Observer {
	private static final String DEFAULT_LABEL = "Select a TASK/ISR/AGENT trace-line";
	
	private TraceModel          traceModel;
	private ZoomModel           zoomModel;
	private StatisticsTimeModel timeModel;
	
	private CLabel             taskLabel;
	private StatTimeViewer     timeViewer;
	private LineStatTreeViewer treeViewer;
	
	private Composite topComposite;

	private TaskStatistic taskStat;
	
	private IAction copyAction;
	private IAction printAction;
	
	/**
	 * The constructor.
	 */
	public LineStatisticsPage() {
		copyAction = new CopyAction(this);
		printAction = new PrintAction(this);
	}

	/* (non-Javadoc)
	 * @see net.timedoctor.ui.statistics.IStatisticsViewPage#setModels(net.timedoctor.core.model.ZoomModel, net.timedoctor.core.model.TraceModel)
	 */
	public void setModels(ZoomModel zoomModel, TraceModel traceModel) {
		this.zoomModel  = zoomModel;
		this.traceModel = traceModel;		
		this.timeModel  = new StatisticsTimeModel();
		
		this.timeModel.addObserver(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		topComposite = new Composite(parent, SWT.NONE);
		
		GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginHeight = 0;
		parentLayout.marginWidth = 0;
		parentLayout.verticalSpacing = 0;
		topComposite.setLayout(parentLayout);
		
		taskLabel = new CLabel(topComposite,SWT.CENTER | SWT.BORDER);
		taskLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		timeViewer = new StatTimeViewer(topComposite, timeModel);
		timeViewer.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		
		treeViewer = new LineStatTreeViewer(topComposite);
		treeViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		selectLine(zoomModel.getSelectedLine());
	}
	
	public void update(Observable o, Object arg) {
		if (taskStat != null) {
			taskStat.calculate(timeModel.getStartTime(), timeModel.getEndTime());
			treeViewer.setInput(taskStat);
		}
	}

	/* (non-Javadoc)
	 * @see net.timedoctor.ui.statistics.IStatisticsViewPage#selectLine(net.timedoctor.core.model.SampleLine)
	 */
	public void selectLine(final SampleLine sampleLine) {
		if (sampleLine == null) {
			defaultView();
			return;
		}
		
		LineType type = sampleLine.getType();
		if ((type == LineType.TASKS) || (type == LineType.ISRS) || (type == LineType.AGENTS)) {
			updateView(sampleLine);
		} else {
			defaultView();
		}
	}

	private void defaultView() {
		treeViewer.setInput(null);
		timeViewer.enableWidgets(false);
		updateLabel(null);
	}

	private void updateView(final SampleLine sampleLine) {
		taskStat = new TaskStatistic(null, traceModel, sampleLine);
		// Update selected time window
		timeModel.setTimes(zoomModel.getStartTime(), zoomModel.getEndTime());
		taskStat.calculate(timeModel.getStartTime(), timeModel.getEndTime());
		treeViewer.setInput(taskStat);
		
		timeViewer.enableWidgets(true);
		updateLabel(sampleLine);
	}

	private void updateLabel(final SampleLine line) {
		if (line == null) {
			taskLabel.setText(DEFAULT_LABEL);
		} else {
			taskLabel.setText("Statistics for " + line.getName());
		}
		
		enableActions(line != null);
	}
	
	private void enableActions(boolean enable) {
		copyAction.setEnabled(enable);
		printAction.setEnabled(enable);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	public void dispose() {
		this.timeModel.deleteObserver(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#getControl()
	 */
	public Control getControl() {
		return topComposite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#setActionBars(org.eclipse.ui.IActionBars)
	 */
	public void setActionBars(IActionBars actionBars) {
		// TODO: button to select between seconds, cycles, or %
		actionBars.getToolBarManager().add(copyAction);
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
		
		actionBars.getToolBarManager().add(printAction);
		actionBars.setGlobalActionHandler(ActionFactory.PRINT.getId(), printAction);
		
		actionBars.updateActionBars();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	public void setFocus() {
		// Do nothing
	}

	public void copyToClipboard() {
		treeViewer.copy();
	}

	public void print() {
		treeViewer.print();
	}
}
