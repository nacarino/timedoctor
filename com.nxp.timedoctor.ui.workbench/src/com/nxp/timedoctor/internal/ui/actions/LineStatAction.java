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
package com.nxp.timedoctor.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.nxp.timedoctor.internal.ui.LineStatView;

public class LineStatAction extends Action implements IWorkbenchAction {
	
	public final static String ID = "com.nxp.timedoctor.ui.trace";
	
	private final IWorkbenchWindow window;

	public LineStatAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Selected Row");
		setToolTipText("Open the statistics view for the selected trace line.");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("com.nxp.timedoctor.ui", "icons/line_statistic_view.gif"));
	}

	@Override
	public void run() {
		IWorkbenchPage page = window.getActivePage();
		try {
			page.showView(LineStatView.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void dispose() {
	}
}
