/*******************************************************************************
 * Copyright (c) 2006 Royal Philips Electronics NV.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package com.nxp.timedoctor.internal.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.parser.Parser;
import com.nxp.timedoctor.internal.ui.actions.CopyAction;
import com.nxp.timedoctor.internal.ui.outline.TraceOutlinePage;
import com.nxp.timedoctor.ui.trace.TraceViewer;

/**
 * The main editor for TimeDoctor. Created upon open of a .tdi file, calls a parser on
 * the file and creates a traceModel, then creates and populates all subelements
 * necessary to display and manipulate the data.
 */
public class TraceEditor extends EditorPart implements ISelectionChangedListener {
	public final static String ID = "com.nxp.timedoctor.ui.workbench.TraceEditor";

	private IContentOutlinePage fOutlinePage = null;

	/**
	 * The view that holds all actual GUI elements.
	 */
	private TraceViewer traceViewer;

	/**
	 * The traceModel in which to store data during parsing, and retrieve during
	 * creation and manipulation of the view.
	 */
	private TraceModel traceModel;

	private ZoomModel zoomModel;

	/**
	 * *.tdi files cannot be saved. Throws an UnsupportedOperationException if
	 * called.
	 * 
	 * @param monitor
	 *            IProgressMonitor
	 */
	@Override
	public final void doSave(final IProgressMonitor monitor) {
		throw new UnsupportedOperationException("File save not permitted.");
	}

	/**
	 * *.tdi files cannot be saved. Throws an UnsupportedOperationException if
	 * called.
	 */
	@Override
	public final void doSaveAs() {
		throw new UnsupportedOperationException("File save not permitted.");
	}

	/**
	 * Initializes the editor. The input must be in the form of an
	 * <code>IStorageEditorInput</code>, or a <code>PartInitException</code>
	 * is thrown.
	 * 
	 * @param site
	 *            the site of the editor
	 * @param input
	 *            the input file source
	 * @throws PartInitException
	 *             if the input is not an IStorageEditorInput
	 */
	@Override
	public final void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		if ( !(input instanceof IPathEditorInput)) {
			throw new PartInitException("Invalid Input: Must have implemented IPathEditorInput");
		}
		
		setSite(site);
		setInput(input);
		
		super.setPartName(input.getName());

		traceModel = new TraceModel();
		zoomModel = new ZoomModel();
		
		IPathEditorInput iPath = (IPathEditorInput)input;
		File ioFile = iPath.getPath().toFile();
		
		final Parser parser = new Parser("Opening trace", traceModel, ioFile);

		IWorkbenchWindow window = this.getSite().getWorkbenchWindow();
		try {
			window.run(true, true, new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					parser.doParse(monitor);
				}
			});
		} catch (InvocationTargetException e) {
			// A parse exception
			MessageDialog.openError(this.getSite().getShell(), "Error", "Parse failed, reason: " + e.getCause().getMessage());
			throw new PartInitException(e.getCause().getMessage(), e.getCause());
		} catch (InterruptedException e) {
			// User pressed cancel
			super.getSite().getPage().closeEditor(this, false);
			return;
		}
		
		site.getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), new CopyAction(this));
	}

	/**
	 * (non-Javadoc)
	 * 
	 * Method declared on IAdaptable
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(final Class key) {
		if (key.equals(IContentOutlinePage.class)) {
			if (fOutlinePage == null) {
				fOutlinePage = createOutlinePage();
			}
			return fOutlinePage;
		}

		return super.getAdapter(key);
	}

	private TraceOutlinePage createOutlinePage() {
		TraceOutlinePage traceOutlinePage = new TraceOutlinePage(this);
		return traceOutlinePage;
	}
	
	/**
	 * Always returns false, because file itself cannot be modified.
	 * 
	 * @return false
	 */
	@Override
	public final boolean isDirty() {
		return false;
	}

	/**
	 * Returns false. Saving of files is not supported by this editor.
	 * 
	 * @return false
	 */
	@Override
	public final boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Creates the <code>TraceViewer</code>, which handles instantiating the
	 * rest of the data viewing GUI.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	@Override
	public final void createPartControl(final Composite parent) {
		traceViewer = new TraceViewer(parent, traceModel, zoomModel);
		
		getSite().setSelectionProvider(getSelectionProvider());
		getSelectionProvider().addSelectionChangedListener(this);
	}

	/**
	 * Empty method. <code>TraceViewer</code> has no setFocus method.
	 */
	@Override
	public final void setFocus() {
	}

	/**
	 * Returns the <code>ZoomModel</code> object associated with this editor,
	 * for use by actions.
	 * 
	 * @return this editor's <code>ZoomModel</code>
	 */
	public final ZoomModel getZoomModel() {
		return zoomModel;
	}

	/**
	 * Returns the <code>TraceModel</code> associated with this editor, for use by actions.
	 * 
	 * @return this editor's associated <code>TraceModel</code>
	 */
	public final TraceModel getTraceModel() {
		return traceModel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {		
		traceViewer.dispose();
		
		zoomModel.deleteObservers();
		traceModel.deleteObservers();
		
		fOutlinePage = null;
		traceModel = null;
		
		zoomModel = null;

		super.dispose();
	}

	/**
	 * Returns the {@link ISelectionProvider}
	 *  
	 * @return The {@link ISelectionProvider}
	 */
	public ISelectionProvider getSelectionProvider() {
		return traceViewer.getSelectionProvider();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSource().equals(getSelectionProvider())) {
			if (fOutlinePage != null) {
				fOutlinePage.removeSelectionChangedListener(this);
				fOutlinePage.setSelection(event.getSelection());
				fOutlinePage.addSelectionChangedListener(this);
			}
		} else {
			getSelectionProvider().removeSelectionChangedListener(this);
			getSelectionProvider().setSelection(event.getSelection());
			getSelectionProvider().addSelectionChangedListener(this);
		}	
	}
	
	/**
	 * Returns an {@link Image} containing the screenshot of the current visible
	 * portion
	 * 
	 * @return
	 * 			The {@link Image} screenshot. The image resource must be disposed by the caller.
	 */
	public Image getScreenShot() {
		return traceViewer.getScreenShot(); //Should be disposed by the caller
	}
}
