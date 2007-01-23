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
package com.nxp.timedoctor.internal.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.model.statistics.StatisticsTimeModel;
import com.nxp.timedoctor.ui.statistics.TraceStatViewer;

public class TraceStatView extends StatisticsView {
	public static final String ID = "com.nxp.timedoctor.ui.workbench.TraceStatView";
	
	private TraceStatViewer viewer;
	
	/**
	 * The constructor.
	 */
	public TraceStatView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);
		
		viewer = new TraceStatViewer(parent);
	}
	
	@Override
	public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
		viewer.selectionChanged();
	}
	
	@Override
	protected void editorActivated(final IEditorPart editor) {
		if (!getViewSite().getPage().isPartVisible(this)) {
			return;
		}

		if (editor instanceof TraceEditor) {				
			TraceModel traceModel = ((TraceEditor)editor).getTraceModel();				
			ZoomModel zoomModel = ((TraceEditor)editor).getZoomModel();
			StatisticsTimeModel timeModel = new StatisticsTimeModel();
			viewer.setModels(traceModel, zoomModel, timeModel);
		}
	}
	
	@Override
	protected void editorClosed() {
		viewer.setModels(null, null, null);
	}
}