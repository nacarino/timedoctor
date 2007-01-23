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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.nxp.timedoctor.core.model.Times;
import com.nxp.timedoctor.core.model.statistics.CounterStatistic;
import com.nxp.timedoctor.core.model.statistics.ExecutionStatistic;
import com.nxp.timedoctor.core.model.statistics.InterruptStatistic;
import com.nxp.timedoctor.core.model.statistics.Statistic;

public class StatisticsLabelProvider extends LabelProvider implements ITableLabelProvider {
	private final int TOTAL_COLUMN = 1;
	private final int LOAD_COLUMN = 2;
	private final int MIN_COLUMN = 3;
	private final int AVG_COLUMN = 4;
	private final int MAX_COLUMN = 5;
	
	public String getColumnText(final Object obj, final int columnIndex) {
		if (columnIndex == 0) {
			return ((Statistic) obj).getName();
		} else if (obj instanceof ExecutionStatistic) {
			return getExecutionStr((ExecutionStatistic) obj, columnIndex);
		} else if (obj instanceof InterruptStatistic) {
			return getInterruptStr((InterruptStatistic) obj, columnIndex);
		} else if (obj instanceof CounterStatistic) {
			return getCounterStr((CounterStatistic) obj, columnIndex);			
		}		
		return null;
	}

	public Image getColumnImage(final Object obj, final int columnIndex) {
		return null;
	}

	private String getExecutionStr(final ExecutionStatistic stat, final int columnIndex) {
		switch (columnIndex) {
		case TOTAL_COLUMN:
			return Times.timeToString(stat.getTotalTime());
		case LOAD_COLUMN:
			return percentageStr(stat.getLoad());
		case MIN_COLUMN: 
			return Times.timeToString(stat.getMinTime());
		case AVG_COLUMN: 
			return Times.timeToString(stat.getAvgTime());
		case MAX_COLUMN: 
			return Times.timeToString(stat.getMaxTime());
		}
		return null;
	}
	
	private String getInterruptStr(final InterruptStatistic stat, final int columnIndex) {
		switch (columnIndex) {
		case TOTAL_COLUMN:
			return String.valueOf(stat.getNTotal());
		case LOAD_COLUMN:
			return doubleToIntStr(stat.getLoad()) + " #/s";
		case MIN_COLUMN: 
			return String.valueOf(stat.getNMin());
		case AVG_COLUMN: 
			return String.valueOf(stat.getNAvg());
		case MAX_COLUMN: 
			return String.valueOf(stat.getNMax());
		}
		return null;
	}
	
	private String getCounterStr(final CounterStatistic stat, final int columnIndex) {
		switch (columnIndex) {
		case TOTAL_COLUMN:
			return cycleToStr(stat.getTotalCountVal());
		case LOAD_COLUMN:
			return frequencyStr(stat.getLoad());
		case MIN_COLUMN: 
			return cycleToStr(stat.getMinCountVal());
		case AVG_COLUMN: 
			return cycleToStr(stat.getAvgCountVal());
		case MAX_COLUMN: 
			return cycleToStr(stat.getMaxCountVal());
		}
		return null;
	}

	private String percentageStr(final double percent) {		
		NumberFormat timeFormat = new DecimalFormat("0.##%");
		return timeFormat.format(percent);
	}

	private String frequencyStr(final double frequency) {		
		NumberFormat timeFormat = new DecimalFormat("0.### Mcycles/sec");
		return timeFormat.format(frequency / 1000000d);
	}
	
	private String cycleToStr(final double cycle) {
		return doubleToIntStr(cycle) + " cycles";
	}
	
	private String doubleToIntStr(final double val) {
		return String.valueOf((long) val);
	}	
}
