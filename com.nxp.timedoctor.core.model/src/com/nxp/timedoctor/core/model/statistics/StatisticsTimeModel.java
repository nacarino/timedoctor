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

import java.util.Observable;

public class StatisticsTimeModel extends Observable {
	private double startTime = 0;
	private double endTime = 0;

	public final void setStartTime(final double time) {
		if (time != startTime) {
			this.startTime = time;
			setChanged();
			notifyObservers();
		}
	}

	public final void setEndTime(final double time) {
		if (time != endTime) {
			this.endTime = time;
			setChanged();
			notifyObservers();
		}
	}

	public final void setTimes(final double start, final double end) {
		if ((start != end) && ((start != startTime) || (end != endTime))) {
			this.startTime = start;
			this.endTime = end;
			setChanged();
			notifyObservers();
		}
	}

	public final double getStartTime() {
		return startTime;
	}

	public final double getEndTime() {
		return endTime;
	}
}
