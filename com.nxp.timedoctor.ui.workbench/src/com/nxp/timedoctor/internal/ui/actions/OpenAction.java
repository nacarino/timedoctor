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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.nxp.timedoctor.internal.ui.TraceEditor;
import com.nxp.timedoctor.internal.ui.TraceEditorInput;

public class OpenAction extends Action implements IWorkbenchAction {
	
	public final static String ID = "com.nxp.timedoctor.ui.trace";
	
	private final IWorkbenchWindow window;

	public OpenAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Open Trace...");
		setToolTipText("Open a TimeDoctor trace file.");
		// TODO add image
	}

	@Override
	public void run() {
		FileDialog fileDialog = new FileDialog(window.getShell(), SWT.OPEN | SWT.MULTI);
		fileDialog.setFilterNames(new String[] {
				"TimeDoctor Trace Files (*.tdi)", "All Files (*.*)" });
		fileDialog.setFilterExtensions(new String[] { "*.tdi", "*.*" });
		fileDialog.setFileName(null);

		fileDialog.open();
		
		String[] fileNames = fileDialog.getFileNames();
		String filePath = fileDialog.getFilterPath();
		openFiles(filePath, fileNames);
	}

	private void openFiles(final String filePath, final String[] fileNames) {
		if ( (fileNames != null) && (fileNames.length > 0)) {
			int numberOfFilesNotFound = 0;
			StringBuffer notFound = new StringBuffer();
			
			for (String fileName : fileNames) {
				TraceEditorInput input = new TraceEditorInput(fileName, filePath);
				File file = input.getPath().toFile();
				
				if (file.exists() && !file.isDirectory()) {
					openFile(input);
				} else {
					if (++numberOfFilesNotFound > 1) {
						notFound.append('\n');
					}
					notFound.append(file.getAbsolutePath());
				}
				
				if (numberOfFilesNotFound > 0) {
					MessageDialog.openError(window.getShell(), "Error", "The file(s) " + notFound.toString() + " could not be found");
				}
			}
		}
	}

	private void openFile(final TraceEditorInput input) {
		try {
			window.getActivePage().openEditor(input, TraceEditor.ID);
		} catch (PartInitException e) {
			MessageDialog.openError(window.getShell(), "Error", e.getMessage());
		}
	}

	public void dispose() {
	}
}
