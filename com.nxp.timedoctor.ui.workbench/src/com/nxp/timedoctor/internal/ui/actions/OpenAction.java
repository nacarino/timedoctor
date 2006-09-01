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
package com.nxp.timedoctor.internal.ui.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.nxp.timedoctor.internal.ui.TraceEditor;
import com.nxp.timedoctor.internal.ui.TraceEditorInput;

public class OpenAction extends Action implements IWorkbenchAction {
	
	public final static String ID = "com.nxp.timedoctor.ui.trace";
	
	private final IWorkbenchWindow window;

	public OpenAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Open Trace...");
		setToolTipText("Open a TimeDoctor trace file.");
		// TODO add image
	}

	public void run() {
		FileDialog fileDialog = new FileDialog(window.getShell(), SWT.OPEN | SWT.MULTI);
		fileDialog.setFilterNames(new String[] {
				"TimeDoctor Trace Files (*.tdi)", "All Files (*.*)" });
		fileDialog.setFilterExtensions(new String[] { "*.tdi", "*.*" });
		fileDialog.setFileName(null);

		fileDialog.open();
		
		String[] fileNames = fileDialog.getFileNames();
		
		if ( fileNames != null && fileNames.length > 0) {
			int numberOfFilesNotFound = 0;
			StringBuffer notFound= new StringBuffer();
			
			String filePath     = fileDialog.getFilterPath();			
			IWorkbenchPage page = window.getActivePage();
			
			for ( int i = 0; i < fileNames.length; i++ ) {
				TraceEditorInput input = new TraceEditorInput(fileNames[i], filePath);
				File ioFile = input.getPath().toFile();
				
				if (ioFile.exists() && !ioFile.isDirectory()) {
					try {
						page.openEditor(input, TraceEditor.ID);
					} catch (PartInitException e) {
						MessageDialog.openError(this.window.getShell(), "Error", e.getMessage());
					}
				} else {
					if (++numberOfFilesNotFound > 1)
						notFound.append('\n');
					notFound.append(input.getName());
				}
				
				if (numberOfFilesNotFound > 0) {
					MessageDialog.openError(this.window.getShell(), "Error", "The file(s) " + notFound.toString() + " could not be found");
				}
			}
		}
	}

	public void dispose() {
	}
}
