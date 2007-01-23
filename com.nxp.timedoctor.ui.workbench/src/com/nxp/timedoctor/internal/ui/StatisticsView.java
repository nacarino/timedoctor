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
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.model.statistics.StatisticsTimeModel;
import com.nxp.timedoctor.ui.statistics.StatisticsViewer;

public class StatisticsView extends ViewPart implements IPartListener2, ISelectionListener {
	public static final String ID = "com.nxp.timedoctor.ui.workbench.StatisticsView";
	
	private StatisticsViewer viewer;
	
	/**
	 * The constructor.
	 */
	public StatisticsView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		viewer = new StatisticsViewer(parent);
					
		getSite().getPage().addPartListener(this);
		
		// add this view as a selection listener to the workbench page,
		// which only listens to selection changes from the trace editor
		//getSite().getPage().addSelectionListener(TraceEditor.ID, (ISelectionListener) this);
		// Temporarily listen to all selection events (as the TraceEditor does not have a selectionProvider yet)
		getSite().getPage().addSelectionListener(this);
	}
	
	@Override
	public void dispose() {
		getSite().getPage().removePartListener(this);
		getSite().getPage().removeSelectionListener(this);
	}

	@Override
	public void setFocus() {
	}

	public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
		viewer.selectionChanged();
	}

	public void partActivated(final IWorkbenchPartReference partRef) {
		if (partRef.getPart(true) instanceof IEditorPart) {
			IEditorPart editor = getViewSite().getPage().getActiveEditor();
			editorActivated(editor);
		}
	}

	public void partBroughtToTop(final IWorkbenchPartReference partRef) {
		if (partRef.getPart(true) == StatisticsView.this) {
			IEditorPart editor = getViewSite().getPage().getActiveEditor();
			editorActivated(editor);
		}		
	}

	public void partClosed(final IWorkbenchPartReference partRef) {
		if (partRef.getPart(true) instanceof IEditorPart) {
			IEditorPart editor = getViewSite().getPage().getActiveEditor();
			if (editor == null) {
				viewer.setModels(null, null, null);
			}
		}
	}

	public void partDeactivated(final IWorkbenchPartReference partRef) {
	}

	public void partHidden(final IWorkbenchPartReference partRef) {
	}

	public void partInputChanged(final IWorkbenchPartReference partRef) {
	}

	public void partOpened(final IWorkbenchPartReference partRef) {
		if (partRef.getPart(true) == StatisticsView.this) {
			IEditorPart editor = getViewSite().getPage().getActiveEditor();
			editorActivated(editor);
		}
	}

	public void partVisible(final IWorkbenchPartReference partRef) {
	}
	
	private void editorActivated(final IEditorPart editor) {
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
}