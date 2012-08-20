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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import net.timedoctor.core.model.statistics.TaskStatistic;

public class TraceStatLabelProvider extends LabelProvider implements ITableLabelProvider {
	private final int TASK_COLUMN = 0;
	private final int EXECUTIONS_COLUMN = 1;
	private final int EXEC_RATE_COLUMN = 2;
	private final int LOAD_COLUMN = 3;
	private final int INT_RATE_COLUMN = 4;	
	
	public String getColumnText(final Object obj, final int columnIndex) {		
		TaskStatistic task = (TaskStatistic) obj;
		switch (columnIndex) {
		case TASK_COLUMN:
			return task.getName();
		case EXECUTIONS_COLUMN:
			return String.valueOf(task.getExecutionsStatistic().getNTotal());
		case EXEC_RATE_COLUMN:
			return doubleToIntStr(task.getExecutionsStatistic().getLoad());
		case LOAD_COLUMN:
			return doubleToIntStr(100 * task.getExExecTimeStatistic().getLoad());
		case INT_RATE_COLUMN:
			return doubleToIntStr(task.getInterruptStatistic().getLoad());
		}
		return null;
	}

	public Image getColumnImage(final Object obj, final int columnIndex) {
		return null;
	}

	private String doubleToIntStr(final double val) {
		NumberFormat format = new DecimalFormat("0.##");
		return format.format(val);
	}	
}
