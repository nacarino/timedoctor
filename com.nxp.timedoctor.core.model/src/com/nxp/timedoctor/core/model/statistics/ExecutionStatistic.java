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
package com.nxp.timedoctor.core.model.statistics;

public class ExecutionStatistic extends Statistic {
	private double windowStartTime;
	private double windowEndTime;
	
	private double totalTime;
	private double minTime;
	private double avgTime;
	private double maxTime;
	private int nExecutions;
	
	private double execTime;
	private double windowExecTime;
	
	public ExecutionStatistic(final Statistic parent, final String name) {
		super(parent, name);
	}
	
	@Override
	public void init(final double firstSampleTime,
			final double windowStartTime,
			final double windowEndTime) {
		this.windowStartTime = windowStartTime;
		this.windowEndTime = windowEndTime;
		
		totalTime = 0d;		
		minTime = Double.MAX_VALUE;
		avgTime = 0d;
		maxTime = 0d;
		
		execTime = 0;
		windowExecTime = 0;
		
		nExecutions = 0;
	}
	
	@Override
	public void update(final double activeStartTime, final double activeEndTime) {
		execTime += activeEndTime - activeStartTime;
		windowExecTime += clipExecTime(windowStartTime, windowEndTime, activeStartTime, activeEndTime);
	}
	
	@Override
	public void consolidate() {
		minTime = Math.min(minTime, execTime);
		maxTime = Math.max(maxTime, execTime);
		
		avgTime += execTime;
		totalTime += windowExecTime;

		// Reset interval variables for next interval
		execTime = 0d;
		windowExecTime = 0d;

		nExecutions++;
	}
	
	public double getTotalTime() {
		return totalTime;
	}
	
	public double getLoad() {
		double timeInterval = windowEndTime - windowStartTime;
		return ((timeInterval == 0d) ? 0d : totalTime / timeInterval);
	}
		
	public double getMaxTime() {
		return maxTime;
	}
	
	public double getAvgTime() {
		return ((nExecutions==0) ? 0d : (avgTime / nExecutions));
	}
	
	public double getMinTime() {
		return ((minTime == Double.MAX_VALUE) ? 0d : minTime);
	}
}

