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
import net.timedoctor.core.model.Sample.SampleType;
import net.timedoctor.core.model.SampleLine.LineType;

public class TaskStatistic extends Statistic {
	private TraceModel traceModel;
	private SampleLine line;

	private InterruptStatistic executionStat;
	private ExecutionStatistic incExecutionStat;
	private ExecutionStatistic exExecutionStat;
	private InterruptStatistic interruptStat;
	private ExecutionStatistic intExecutionStat;
	private CompositeStatistic counterStats;
	
	public TaskStatistic(final Statistic parent,
			final TraceModel traceModel, 
			final SampleLine line) {
		super(parent, line.getName());
		this.traceModel = traceModel;
		this.line = line;
		
		createContents();
	}

	private void createContents() {		
		Statistic executions = new CompositeStatistic(this, "Executions");
		executionStat = new InterruptStatistic(this, "Nr. executions");
		executions.addChild(executionStat);
		incExecutionStat = new ExecutionStatistic(executions, "Time inc. interrupts");
		executions.addChild(incExecutionStat);
		exExecutionStat = new ExecutionStatistic(executions, "Time ex. interrupts");
		executions.addChild(exExecutionStat);
		addChild(executions);

		Statistic interrupts = new CompositeStatistic(this, "Interrupts");
		addChild(interrupts);		
		interruptStat = new InterruptStatistic(this, "Nr. interrupts");
		interrupts.addChild(interruptStat);
		intExecutionStat = new ExecutionStatistic(interrupts, "Interrupt time");
		interrupts.addChild(intExecutionStat);
		
		counterStats = new CompositeStatistic(this, "Counters");
		Section cycles = traceModel.getSections().getSection(LineType.CYCLES);
		if (cycles != null) {
			for (SampleLine line : cycles.getLines()) {
				counterStats.addChild(new CounterStatistic(executions, line));
			}
			addChild(counterStats);
		}
	}
	
	public void calculate(final double windowStartTime, final double windowEndTime) {
		// Find first relevant start sample
		int startIndex = line.binarySearch(windowStartTime);
		while ((startIndex>0) && (line.getSample(startIndex).type != SampleType.START)) {
			startIndex--;
		}
		double firstSampleTime = line.getSample(startIndex).time;
		
		executionStat.init(firstSampleTime, windowStartTime, windowEndTime);
		exExecutionStat.init(firstSampleTime, windowStartTime, windowEndTime);
		incExecutionStat.init(firstSampleTime, windowStartTime, windowEndTime);
		
		interruptStat.init(firstSampleTime, windowStartTime, windowEndTime);
		intExecutionStat.init(firstSampleTime, windowStartTime, windowEndTime);		
		
		counterStats.init(firstSampleTime, windowStartTime, windowEndTime);

		// Scan all samples for this task within the interval (firstStartTime, endTime).
		for (; startIndex < line.getCount(); startIndex++) {
			// Find matching start/stop pair
			// Walk over start samples, look up the matching stop sample
			if (line.getSample(startIndex).type != SampleType.START) {
				continue;
			}
			int stopIndex = (int) line.getSample(startIndex).val;			
			if (line.getSample(stopIndex).type != SampleType.STOP) {
				break;
			}
			
			// Task execution start/end time (including interruptions)
			double execStartTime = line.getSample(startIndex).time;
			double execEndTime = line.getSample(stopIndex).time;
									
			// Restrict measurement to the given window
			if (execStartTime > windowEndTime) {
				break;
			}
			
			executionStat.update(execStartTime, execEndTime);
			incExecutionStat.update(execStartTime, execEndTime);
			
			// Walk over all possible interruptions within the task activation
			for (; startIndex < stopIndex; startIndex++) {
				if (line.getSample(startIndex).type == SampleType.SUSPEND) {
					int resumeIndex = (int) line.getSample(startIndex).val;
					double suspendTime = line.getSample(startIndex).time;
					double resumeTime = line.getSample(resumeIndex).time;
					
					// Only count first level interrupt, not subsequennt interruptions of this
					// interrupt as the task is suspended anyhow
					intExecutionStat.update(suspendTime, resumeTime);
					interruptStat.update(suspendTime, 0);
					
					for (startIndex++; startIndex < resumeIndex; startIndex++) {
						if (line.getSample(startIndex).type == SampleType.SUSPEND) {
							suspendTime = line.getSample(startIndex).time;
							// Only count interrupts, no need to compute resume time here.
							interruptStat.update(suspendTime, 0);
						}
					}
					
					startIndex = resumeIndex - 1;
				} else {
					// Active slice of the task execution period (excl. interrupts)
					double activeStartTime = line.getSample(startIndex).time;
					double activeEndTime = line.getSample(startIndex + 1).time;
										
					counterStats.update(activeStartTime, activeEndTime);
					exExecutionStat.update(activeStartTime, activeEndTime);
				}
			}
			
			// Consolidate all children for the task execution interval
			executionStat.consolidate();
			incExecutionStat.consolidate();
			exExecutionStat.consolidate();
			
			interruptStat.consolidate();
			intExecutionStat.consolidate();

			counterStats.consolidate();
		}
	}
	
	public InterruptStatistic getInterruptStatistic() {
		return interruptStat;
	}
	
	public InterruptStatistic getExecutionsStatistic() {
		return executionStat;
	}
	
	public ExecutionStatistic getExExecTimeStatistic() {
		return exExecutionStat;
	}
}
