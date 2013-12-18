/*******************************************************************************
 * Copyright (c) 2007-2013 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NXP Semiconductors B.V. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.internal.ui.actions;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import net.timedoctor.internal.ui.TraceEditor;
import net.timedoctor.internal.ui.TraceEditorInput;

public class FileOpener {
	private IWorkbenchWindow window;

	public FileOpener(IWorkbenchWindow window) {
		this.window = window;
	}

	public void openFiles(final String[] fileStrings) {
		if ((fileStrings != null) && (fileStrings.length > 0)) {
			int numberOfFilesNotFound = 0;
			StringBuffer notFound = new StringBuffer();
	
			for (String path : fileStrings) {
				File file = new File(path);
				TraceEditorInput input = new TraceEditorInput(file);
				if (input.canRead()) {
					openFile(input);
				} else {
					if (++numberOfFilesNotFound > 1) {
						notFound.append('\n');
					}
					notFound.append(file.getAbsolutePath());
				}
			}
			
			if (numberOfFilesNotFound > 0) {
				MessageDialog.openError(window.getShell(), "Error", "The file(s)\n" + notFound.toString() + "\ncould not be found");
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
}
