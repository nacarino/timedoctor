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

public class InterruptStatistic extends Statistic {
	private double windowStartTime;
	private double windowEndTime;
	
	private int nTotal;
	private int nMin;
	private int nAvg;
	private int nMax;
	
	private int execNInterrupts;
	private int windowExecNInterrupts;
	
	private int nExecutions;
	
	public InterruptStatistic(final Statistic parent, final String name) {
		super(parent, name);
	}
	
	@Override
	public void init(final double firstSampleTime,
			final double windowStartTime,
			final double windowEndTime) {
		this.windowStartTime = windowStartTime;
		this.windowEndTime = windowEndTime;
		
		nTotal = 0;
		nMin = Integer.MAX_VALUE;
		nAvg = 0;
		nMax = 0;
		
		execNInterrupts = 0;
		windowExecNInterrupts = 0;
		
		nExecutions = 0;
	}
	
	@Override
	public void update(final double activeStartTime, final double activeEndTime) {
		execNInterrupts++;
		windowExecNInterrupts = clipNInterrupts(windowExecNInterrupts, activeStartTime);
	}
	
	@Override
	public void consolidate() {
		nMin = Math.min(nMin, execNInterrupts);
		nMax = Math.max(nMax, execNInterrupts);
		
		nAvg += execNInterrupts;
		nTotal += windowExecNInterrupts;

		// Reset interval variables for next interval
		execNInterrupts = 0;
		windowExecNInterrupts = 0;

		nExecutions++;
	}
	
	public int getNTotal() {
		return nTotal;
	}
	
	public int getNMax() {
		return nMax;
	}
	
	public int getNAvg() {
		return ((nExecutions==0) ? 0 : (nAvg / nExecutions));
	}
	
	public int getNMin() {
		return ((nMin == Integer.MAX_VALUE) ? 0 : nMin);
	}
	
	public double getLoad() {
		double timeInterval = windowEndTime - windowStartTime;
		return ((timeInterval == 0d) ? 0d : nTotal / timeInterval);
	}
	
	private int clipNInterrupts(final int nInterrupts, final double activeStartTime) {
		if ((activeStartTime >= windowStartTime) && (activeStartTime <= windowEndTime)) {
			return nInterrupts + 1;
		}
		return nInterrupts;
	}
}

