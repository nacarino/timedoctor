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
package net.timedoctor.internal.ui.outline;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.Section;
import net.timedoctor.core.model.SectionList;
import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.SampleLine.LineType;
import net.timedoctor.internal.ui.TraceEditor;
import net.timedoctor.ui.ITimeDoctorUIConstants;

/**
 * An OutLine view provider for the <code>TraceEditor</code>
 *
 */
public class TraceOutlinePage extends Page implements IContentOutlinePage,
		ISelectionChangedListener, Observer {
	private TraceModel traceModel;
	private CheckboxTreeViewer fTreeViewer;
	private ListenerList selectionListenerList = new ListenerList();
	private TraceEditor fTraceEditor;
	private boolean fLinkWithEditor = false;
	
	/**
	 * Constructor for the {@link TraceOutlinePage}
	 * @param traceEditor The {@link TraceEditor} to which the OutlinePage belongs to
	 */
	public TraceOutlinePage(final TraceEditor traceEditor) {
		super();
		
		fTraceEditor = traceEditor;
		this.traceModel = traceEditor.getTraceModel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#init(org.eclipse.ui.part.IPageSite)
	 */
	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		installLinkWithEditorButton();
		
		fTreeViewer = new ContainerCheckedTreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		fTreeViewer.setAutoExpandLevel(CheckboxTreeViewer.ALL_LEVELS);
		fTreeViewer.setUseHashlookup(true);
		
		fTreeViewer.setContentProvider(new TraceOutlineContentProvider());
		fTreeViewer.setLabelProvider(new TraceOutlineLabelProvider());
		
		fTreeViewer.setInput(traceModel);
		
		fTreeViewer.addCheckStateListener(new ICheckStateListener(){
			public void checkStateChanged(CheckStateChangedEvent event) {
				final Object source = event.getElement();
				final boolean checked = event.getChecked();
				
				if ( source instanceof Section ) {
					Section section = (Section)source;
					
					for (final SampleLine line:section.getLines()) {
						line.setVisible(checked);
					}
				} else {
					SampleLine line = (SampleLine)source;
					line.setVisible(checked);
				}
				
				traceModel.setChanged();
			}
		});
		
		fTreeViewer.addTreeListener(new ITreeViewerListener(){
			public void treeCollapsed(TreeExpansionEvent event) {}

			public void treeExpanded(TreeExpansionEvent event) {
				updateCheckState((Section)event.getElement());
			}
		});
				
		updateCheckState();
		
		if (fLinkWithEditor) {
			setSelection(fTraceEditor.getSelectionProvider().getSelection());
		}
		
		traceModel.addObserver(this);
		getSite().setSelectionProvider(this);
		addSelectionChangedListener(fTraceEditor);
		fTreeViewer.addSelectionChangedListener(this);
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
			fTreeViewer.setChecked(line, line.isVisible());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(final SelectionChangedEvent event) {
		setFocus();

		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		Object selected = selection.getFirstElement();
		if (selected instanceof SampleLine) {
			fireSelectionChanged(selection);
		}
	}

	private ISelection getVisibleSampleLineAsSelection(final ISelection sel){
		if (sel instanceof IStructuredSelection) {
			final Object selected = ((IStructuredSelection) sel).getFirstElement();			
			if (selected instanceof SampleLine && ((SampleLine)selected).isVisible()) {
				return new StructuredSelection(selected);
			}
		}		
		return StructuredSelection.EMPTY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return getVisibleSampleLineAsSelection(fTreeViewer.getSelection());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		if (fTreeViewer != null && fLinkWithEditor) {
			ISelection sel = getVisibleSampleLineAsSelection(selection);
			
			if (!sel.isEmpty()) {
				fTreeViewer.removeSelectionChangedListener(this);
				fTreeViewer.setSelection(sel, true);
				fTreeViewer.addSelectionChangedListener(this);
			}
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
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (o instanceof TraceModel) {
			fTreeViewer.refresh();
			
			UIJob job = new UIJob("Update tree") {
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					updateCheckState(); //Can take long if the tree is big
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	@Override
	public void dispose() {
		if (fTraceEditor != null) {
			removeSelectionChangedListener(fTraceEditor);
		}
		
		if (fTreeViewer != null) {
			fTreeViewer.removeSelectionChangedListener(this);
			fTreeViewer.getControl().dispose();
			fTreeViewer = null;
		}
		
		traceModel.deleteObserver(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#setFocus()
	 */
	@Override
	public void setFocus() {
		if ( fTreeViewer != null ) {
			fTreeViewer.getControl().setFocus();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		if ( fTreeViewer != null )
			return fTreeViewer.getControl();
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

		linkWithEditorAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(ITimeDoctorUIConstants.TD_UI_PLUGIN,
													ITimeDoctorUIConstants.LOCAL_TOOLBAR_ENABLED_IMG_PATH + "link_with_editor.gif"));
		linkWithEditorAction.setToolTipText("Link with Editor");
		getSite().getActionBars().getToolBarManager().add(linkWithEditorAction);
	}
	
	private void updateLink() {
		setSelection(fTraceEditor.getSelectionProvider().getSelection());
	}
}
