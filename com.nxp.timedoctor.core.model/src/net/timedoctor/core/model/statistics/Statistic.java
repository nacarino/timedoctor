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

import java.util.ArrayList;
import java.util.List;

public class Statistic {
	private Statistic parent = null;
	private String name = "";
	private final List<Statistic> children = new ArrayList<Statistic>();
	
	protected Statistic(final Statistic parent, final String name) {
		this.parent = parent;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Statistic getParent() {
		return parent;
	}
	
	public List<Statistic> getChildren() {
		return children;
	}
	
	protected void addChild(final Statistic child) {
		children.add(child);
	}
	
	public void init(final double firstSampleTime, 
			final double windowStartTime, 
			final double windowEndTime) {
	}

	public void update(final double activeStartTime, 
			final double activeEndTime) {	
	}
	
	public void consolidate() {
	}
	
	protected double clipExecTime(final double clipStartTime, 
			final double clipEndTime, 
			final double startTime, 
			final double endTime) {
		return Math.max(0, Math.min( 
				Math.min(endTime - startTime, clipEndTime - startTime), 
				endTime - clipStartTime));
	}	
}
