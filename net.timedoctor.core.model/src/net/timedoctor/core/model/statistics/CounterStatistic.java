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
package net.timedoctor.core.model.statistics;

import net.timedoctor.core.model.SampleLine;

public class CounterStatistic extends Statistic {
	private SampleLine line;
	private int index = 0;
	
	// Setup of the window
	double windowStartTime;
	double windowEndTime;
	
	// Counters for the time window
	private double minCountVal;
	private double totalCountVal;
	private double maxCountVal;
	
	// Counters per task execution 
	private double execCountVal;
	private double execCountTime;
	private double windowExecCountVal;
	private int nExecutions;	
		
	public CounterStatistic(final Statistic parent, final SampleLine line) {
		super(parent, line.getName());
		this.line = line;		
	}
	
	/**
	 * Return the sum of all counted values, 
	 * counted within the current time window.
	 * 
	 * @return current total counted value
	 */
	public double getTotalCountVal() {
		return totalCountVal;
	}

	public double getLoad() {
		double timeInterval = windowEndTime - windowStartTime;
		return ((timeInterval == 0d) ? 0d : totalCountVal / timeInterval);
	}
	
	/**
	 * Return the maximum counter value per task execution,
	 * counted within the current time window.
	 * 
	 * @return current maximum counter value.
	 */
	public double getMaxCountVal() {
		return maxCountVal;
	}
	
	/**
	 * Return the averge counter value per task execution,
	 * counted within the current time window.
	 * 
	 * @return current average counter value.
	 */
	public double getAvgCountVal() {
		return ((nExecutions==0) ? 0d : (totalCountVal / nExecutions));
	}
	
	/**
	 * Return the minimum counter value per task execution,
	 * counted within the current time window.
	 * 
	 * @return current minimum counter value.
	 */
	public double getMinCountVal() {
		return ((minCountVal == Double.MAX_VALUE) ? 0d : minCountVal);
	}

	/**
	 * (re-)initialize all counters for a new time window.
	 * 
	 * @param firstStartTime start time at which to look for the first sample.
	 * 		Note: this may be before the start time of the window.
	 */
	@Override
	public void init(final double firstSampleTime, 
			final double windowStartTime,
			final double windowEndTime) {
		index = line.binarySearch(firstSampleTime);
		
		this.windowStartTime = windowStartTime;
		this.windowEndTime = windowEndTime;
		
		minCountVal = Double.MAX_VALUE;
		maxCountVal = 0d;
		totalCountVal = 0d;

		execCountVal = 0d;
		execCountTime = 0d;
		
		windowExecCountVal = 0d;
		
		nExecutions = 0;
	}
	
	/**
	 * Update counter statistics for given active slice of task execution.
	 * 
	 * @param activeStartTime 
	 * 		Start time of an active slice of a single task execution
	 * @param activeEndTime 
	 * 		End time of the active slice started 
	 * 		with <code>activeStartTime</code>
	 * @param windowActiveStartTime
	 * 		Start time of the active slice restricted to the selected time
	 * 		window
	 * @param
	 * 		End time of the active slice restricted to the selected time
	 * 		window
	 */
	@Override
	public void update(final double activeStartTime, final double activeEndTime) {
		// Active slice within the selected time window
		double windowActiveStartTime = Math.max(activeStartTime, windowStartTime);
		double windowActiveEndTime = Math.min(activeEndTime, windowEndTime);
		
		for(; index < line.getCount()-1; index++)
		{
			double countSampleStartTime = line.getSample(index).time;
			double countSampleEndTime = line.getSample(index+1).time;
			if (activeStartTime >= countSampleEndTime) {
				continue;
			}
			if (countSampleStartTime >= activeEndTime) {
				break;
			}
			
			double countSampleTime = countSampleEndTime - countSampleStartTime;
			double countTime = clipExecTime(activeStartTime, activeEndTime, countSampleStartTime, countSampleEndTime);
			double countVal = line.getSample(index+1).val - line.getSample(index).val;
			countVal = clipCountVal(countVal, countTime, countSampleTime);
			execCountTime += countTime;
			execCountVal += countVal;

			double windowCountTime = clipExecTime(windowActiveStartTime, windowActiveEndTime, countSampleStartTime, countSampleEndTime);
			double windowCountVal = clipCountVal(countVal, windowCountTime, countSampleTime);
			windowExecCountVal += windowCountVal;

			if (countSampleEndTime > activeEndTime) {
				break;
			}
		}
	}

	/**
	 * Consolidate counter updates at the completion of a single task execution.
	 */
	@Override
	public void consolidate() {
		minCountVal = Math.min(minCountVal, execCountVal);
		maxCountVal = Math.max(maxCountVal, execCountVal);
		
		totalCountVal += windowExecCountVal;

		// Reset interval variables for next interval
		execCountVal = 0d;
		execCountTime = 0d;
		windowExecCountVal = 0d;

		nExecutions++;
	}
	
	/**
	 * Reduce sample value difference for the reduced count time
	 * if the count ended or started within the requested time interval.
	 * ASSUMPTION: counter increases linearly with time in the count interval!
	 */
	private double clipCountVal(final double countVal, 
			final double clippedCountTime, 
			final double countTime) {
		return ((countTime == 0d) ? countVal : countVal * clippedCountTime/countTime);
	}
}
