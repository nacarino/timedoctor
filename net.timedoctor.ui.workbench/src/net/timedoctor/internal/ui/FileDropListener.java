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
package net.timedoctor.internal.ui;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.ui.IWorkbenchWindow;

import net.timedoctor.internal.ui.actions.FileOpener;

public class FileDropListener extends DropTargetAdapter {

	private FileOpener fileOpener;

	public FileDropListener(final IWorkbenchWindow window) {
		fileOpener = new FileOpener(window);
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
			fileOpener.openFiles(fileStrings);
		}
	}
}
