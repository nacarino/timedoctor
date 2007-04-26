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
package com.nxp.timedoctor.internal.ui.outline;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.Section;
import com.nxp.timedoctor.core.model.SectionList;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.SampleLine.LineType;
import com.nxp.timedoctor.internal.ui.TraceEditor;
import com.nxp.timedoctor.ui.trace.TraceSelection;

/**
 * An OutLine view provider for the <code>TraceEditor</code>. It extends the {@link ContentOutlinePage}
 * and implements {@link ITraceVisibleListener}. 
 *
 */
public class TraceOutlinePage extends Page implements IContentOutlinePage,
		ISelectionChangedListener, ISelectionListener, Observer {
	private TraceModel traceModel;
	private CheckboxTreeViewer treeTraceViewer;
	private ListenerList selectionListenerList = new ListenerList();
	private ISelectionProvider editorSelectionProvider;
	
	private boolean fLinkWithEditor = true;
	
	/**
	 * Constructor for the {@link TraceOutlinePage}
	 * @param traceEditor The {@link TraceEditor} to which the OutlinePage belongs to
	 */
	public TraceOutlinePage(final TraceEditor traceEditor) {
		super();
		
		this.traceModel = traceEditor.getTraceModel();
		this.editorSelectionProvider = traceEditor.getSelectionProvider();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#init(org.eclipse.ui.part.IPageSite)
	 */
	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		installLinkWithEditorButton();
		
		treeTraceViewer = new ContainerCheckedTreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		treeTraceViewer.setAutoExpandLevel(CheckboxTreeViewer.ALL_LEVELS);
		treeTraceViewer.setUseHashlookup(true);
		
		treeTraceViewer.setContentProvider(new TraceOutlineContentProvider());
		treeTraceViewer.setLabelProvider(new TraceOutlineLabelProvider());
		
		treeTraceViewer.setInput(traceModel);
		treeTraceViewer.expandAll();
		
		treeTraceViewer.addSelectionChangedListener(this);
		
		treeTraceViewer.addCheckStateListener(new ICheckStateListener(){
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (!fLinkWithEditor)
					return;
				
				final Object source = event.getElement();
				
				if ( source instanceof Section ) {
					Section section = (Section)source;
					
					for (final SampleLine line:section.getLines()) {
						line.setVisible(event.getChecked());
					}
				} else {
					SampleLine line = (SampleLine)source;
					line.setVisible(event.getChecked());
				}
				
				traceModel.setChanged();
			}
		});
		
		treeTraceViewer.addTreeListener(new ITreeViewerListener(){
			public void treeCollapsed(TreeExpansionEvent event) {}

			public void treeExpanded(TreeExpansionEvent event) {
				updateCheckState((Section)event.getElement());
			}
		});
				
		updateCheckState();
		setSelection(editorSelectionProvider.getSelection());
		
		traceModel.addObserver(this);
		getSite().setSelectionProvider(this);
		getSite().getPage().addSelectionListener(this);
	}
	
	private void updateCheckState() {
		SectionList list = traceModel.getSections();
		Section section;
	
		for (LineType type : LineType.values()) {
			if ((section = list.getSection(type)) != null) {
				updateCheckState(section);
			}
		}
	}

	private void updateCheckState(final Section section) {
		for (final SampleLine line : section.getLines()) {
			treeTraceViewer.setChecked(line, line.isVisible());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(final SelectionChangedEvent event) {
		//Selection changes in TreeViewer 
		setFocus();
		final TraceSelection formTraceSelection = formTraceSelection(event.getSelection());
		
		if (formTraceSelection.isEmpty()) {
			setEmptySelection();
		} else {
			if (fLinkWithEditor)
				fireSelectionChanged(formTraceSelection);
		}
	}

	private void setEmptySelection() {
		treeTraceViewer.removeSelectionChangedListener(this);
		treeTraceViewer.setSelection(TreeSelection.EMPTY);
		treeTraceViewer.addSelectionChangedListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#getSelection()
	 */
	public ISelection getSelection() {
		return formTraceSelection(treeTraceViewer.getSelection());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		if (selection instanceof TraceSelection) {
			treeTraceViewer.removeSelectionChangedListener(this);
			treeTraceViewer.setSelection(formTreeSelection(selection), true);
			treeTraceViewer.addSelectionChangedListener(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListenerList.add(listener);
	}
	
	private void fireSelectionChanged(ISelection selection) {
		//create an event
	    final SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
	
	    // fire the event
	    Object[] listeners = selectionListenerList.getListeners();
	    for (int i = 0; i < listeners.length; ++i) {
	        final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
	        SafeRunner.run(new SafeRunnable() {
	            public void run() {
	                l.selectionChanged(event);
	            }
	        });
	    }
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListenerList.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof TraceEditor) {
			if (fLinkWithEditor)
				setSelection(selection);
		} else {
			setSelection(selection);
		}
	}

	private ITreeSelection formTreeSelection(final ISelection selection) {
		if (selection.isEmpty()) {
			return TreeSelection.EMPTY;
		}
		
		TraceSelection sel = (TraceSelection)selection;
		
		TreePath treePath = new TreePath(new Object[] { sel.getLine() });
		TreeSelection treeSelection = new TreeSelection(treePath);
		
		return treeSelection;
	}

	private TraceSelection formTraceSelection(final ISelection selection) {
		final Object selected = ((IStructuredSelection) selection).getFirstElement();
		
		if (selected instanceof SampleLine) {
			return new TraceSelection((SampleLine)selected);
		} else {
			return new TraceSelection();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (o instanceof TraceModel && fLinkWithEditor) {
			treeTraceViewer.refresh();
			updateCheckState();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		treeTraceViewer = null;
		getSite().getPage().removeSelectionListener(this);
		traceModel.deleteObserver(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#setFocus()
	 */
	@Override
	public void setFocus() {
		if ( treeTraceViewer != null ) {
			treeTraceViewer.getControl().setFocus();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#getControl()
	 */
	@Override
	public Control getControl() {
		if ( treeTraceViewer != null )
			return treeTraceViewer.getControl();
		return null;
	}
	
	private void installLinkWithEditorButton() {
		IAction linkWithEditorAction = new Action("Link", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				fLinkWithEditor = !fLinkWithEditor;
				if (fLinkWithEditor) {
					updateLink();
				}
			}
		};
		
		linkWithEditorAction.setChecked(fLinkWithEditor);

		linkWithEditorAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("com.nxp.timedoctor.ui", "icons/link_with_editor.gif"));
		linkWithEditorAction.setToolTipText("Link with Editor");
		getSite().getActionBars().getToolBarManager().add(linkWithEditorAction);
	}
	
	private void updateLink() {
		treeTraceViewer.refresh();
		updateCheckState();
		setSelection(editorSelectionProvider.getSelection());
	}
}
