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

import org.eclipse.ui.part.IPage;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.ZoomModel;

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
	
	public void copyToClipboard();
	
	public void print();
}
