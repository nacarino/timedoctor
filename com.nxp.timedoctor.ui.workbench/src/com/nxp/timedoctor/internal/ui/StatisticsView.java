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

public class StatisticsView extends ViewPart implements IPartListener2, ISelectionListener {
	
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
	}

	/**
	 * Needed when the user selects an editor
	 */
	public void partActivated(final IWorkbenchPartReference partRef) {
		if ((partRef.getPart(true) instanceof IEditorPart)
			&& (getViewSite().getPage().isPartVisible(this))) {
				editorActivated(getViewSite().getPage().getActiveEditor());
		}
	}

	/** 
	 * Only needed when the view or editor is brought to top programatically
	 * (when done by user partActivated is also called)
	 */
	public void partBroughtToTop(final IWorkbenchPartReference partRef) {
		if (partRef.getPart(true) == StatisticsView.this) {
			editorActivated(getViewSite().getPage().getActiveEditor());
		}		
	}

	/**
	 * Needed when the last editor is closed
	 */
	public void partClosed(final IWorkbenchPartReference partRef) {
		if (partRef.getPart(true) instanceof IEditorPart) {
			IEditorPart editor = getViewSite().getPage().getActiveEditor();
			if (editor == null) {
				editorClosed();
			}
		}
	}

	public void partDeactivated(final IWorkbenchPartReference partRef) {
	}

	public void partHidden(final IWorkbenchPartReference partRef) {
	}

	public void partInputChanged(final IWorkbenchPartReference partRef) {
	}

	/**
	 * Needed when the statistics view is opened
	 */
	public void partOpened(final IWorkbenchPartReference partRef) {
		if (partRef.getPart(true) == StatisticsView.this) {
			editorActivated(getViewSite().getPage().getActiveEditor());
		}
	}

	/**
	 * Needed when a view is hidden behind another view and the TraceEditor
	 * changed while it was hidden
	 */
	public void partVisible(final IWorkbenchPartReference partRef) {
		if (partRef.getPart(true) == StatisticsView.this) {
			editorActivated(getViewSite().getPage().getActiveEditor());
		}
	}
	
	protected void editorActivated(final IEditorPart editor) {
	}
	
	protected void editorClosed() {
	}
}