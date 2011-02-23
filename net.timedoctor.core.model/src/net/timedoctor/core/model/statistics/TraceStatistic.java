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
package net.timedoctor.core.model.statistics;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.Section;
import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.SampleLine.LineType;

public class TraceStatistic extends Statistic {
	private TraceModel traceModel;

	public TraceStatistic(final TraceModel traceModel) {
		super(null, "");
		this.traceModel = traceModel;
		
		createContents();
	}

	private void createContents() {
		Section tasks = traceModel.getSections().getSection(LineType.TASKS);
		if (tasks != null) {
			for (SampleLine line : tasks.getLines()) {
				Statistic task = new TaskStatistic(this, traceModel, line);
				addChild(task);			
			}
		}
	}
	
	public void calculate(final double windowStartTime, final double windowEndTime) {
		for (Statistic task : getChildren()) {
			((TaskStatistic) task).calculate(windowStartTime, windowEndTime);
		}
	}
}
