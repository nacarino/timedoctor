/*******************************************************************************
 * Copyright (c) 2006-2013 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.internal.ui.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import net.timedoctor.ui.ITimeDoctorUIConstants;

public class OpenAction extends Action implements IWorkbenchAction {
	
	public final static String ID = "net.timedoctor.ui.trace";
	
	private FileOpener fileOpener;
	private final IWorkbenchWindow window;

	public OpenAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Open Trace...");
		setToolTipText("Open a TimeDoctor trace file.");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(ITimeDoctorUIConstants.TD_UI_PLUGIN, 
				ITimeDoctorUIConstants.TOOLBAR_ENABLED_IMG_PATH + "open.gif"));
		fileOpener = new FileOpener(window);
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
		
		if (fileNames != null && fileNames.length > 0) {
			final String filePath = fileDialog.getFilterPath();
			
			for (int i=0; i < fileNames.length; i++) {
				fileNames[i] = filePath + File.separator + fileNames[i];
			}
			
			fileOpener.openFiles(fileNames);
		}
	}

	public void dispose() {
	}
}
