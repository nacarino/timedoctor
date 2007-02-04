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

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

// TODO lots of duplicate code with the OpenAction class, refactor?
public class FileDropListener extends DropTargetAdapter {

	private IWorkbenchWindow window;

	public FileDropListener(final IWorkbenchWindow window) {
		this.window = window;
	}
	
	@Override
	public void dragEnter(final DropTargetEvent event) {
		// Set visual indication that only links are allowed
		if (event.detail == DND.DROP_DEFAULT) {
			if ((event.operations & DND.DROP_LINK) != 0) {
				event.detail = DND.DROP_LINK;
			} else {
				event.detail = DND.DROP_NONE;
			}
		}
	}

	@Override
	public void drop(final DropTargetEvent event) {
		// Assumes only FileTransfers are supported
		FileTransfer fileTransfer = FileTransfer.getInstance();
		Object object = fileTransfer.nativeToJava(event.currentDataType);
		if (object instanceof String[]) {
			String[] fileStrings = (String[]) object;
			openFiles(fileStrings);
		}
	}

	private void openFiles(final String[] fileStrings) {
		if ((fileStrings != null) && (fileStrings.length > 0)) {
			int numberOfFilesNotFound = 0;
			StringBuffer notFound = new StringBuffer();

			for (String path : fileStrings) {
				File file = new File(path);
				TraceEditorInput input = new TraceEditorInput(file);
				if (file.exists() && !file.isDirectory()) {
					openFile(input);
				} else {
					if (++numberOfFilesNotFound > 1) {
						notFound.append('\n');
					}
					notFound.append(file.getAbsolutePath());
				}
			}
			if (numberOfFilesNotFound > 0) {
				MessageDialog.openError(window.getShell(),
						"Error", "The file(s) " + notFound.toString()
								+ " could not be found");
			}
		}
	}

	private void openFile(final TraceEditorInput input) {
		try {
			window.getActivePage().openEditor(input, TraceEditor.ID);
		} catch (PartInitException e) {
			MessageDialog.openError(window.getShell(),
					"Error", e.getMessage());
		}
	}
}
