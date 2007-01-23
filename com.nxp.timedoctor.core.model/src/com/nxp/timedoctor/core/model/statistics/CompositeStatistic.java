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

public class CompositeStatistic extends Statistic {
	public CompositeStatistic(final Statistic parent, final String name) {
		super(parent, name);
	}
	
	@Override
	public void init(final double firstSampleTime,
			final double windowStartTime,
			final double windowEndTime) {
		for (Statistic child : getChildren()) {
			child.init(firstSampleTime, windowStartTime, windowEndTime);
		}		
	}
	
	@Override
	public void update(final double activeStartTime, 
			final double activeEndTime) {	
		for (Statistic child : getChildren()) {
			child.update(activeStartTime, activeEndTime);
		}		
	}
	
	@Override
	public void consolidate() {
		for (Statistic child : getChildren()) {
			child.consolidate();
		}
	}
}

