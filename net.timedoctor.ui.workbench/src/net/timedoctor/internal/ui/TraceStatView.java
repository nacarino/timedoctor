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
package net.timedoctor.internal.ui;

import net.timedoctor.ui.statistics.IStatisticsViewPage;
import net.timedoctor.ui.statistics.TraceStatisticsPage;

public class TraceStatView extends StatisticsView {
	public static final String ID = "net.timedoctor.ui.workbench.TraceStatView";
	
	/**
	 * The constructor.
	 */
	public TraceStatView() {
	}

	@Override
	protected IStatisticsViewPage getPage(final TraceEditor editor) {
		TraceStatisticsPage viewer = new TraceStatisticsPage();
		viewer.setModels(editor.getZoomModel(), editor.getTraceModel());
		
		return viewer;
	}
}