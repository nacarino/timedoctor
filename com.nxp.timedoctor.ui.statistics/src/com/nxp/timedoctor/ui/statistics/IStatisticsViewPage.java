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

import org.eclipse.ui.part.IPage;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;

public interface IStatisticsViewPage extends IPage {
	/**
	 * @param zoomModel
	 * @param traceModel
	 */
	public void setModels(ZoomModel zoomModel, TraceModel traceModel);
	
	/**
	 * @param line
	 */
	public void selectLine(SampleLine line);
}
