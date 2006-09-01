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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class PropertyAction extends Action implements IWorkbenchAction {
	
	public final static String ID = "com.nxp.timedoctor.ui.trace";
	
	private final IWorkbenchWindow window;

	public PropertyAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Property View");
		setToolTipText("Open the property view.");
		// TODO add image
	}

	public void run() {
		IWorkbenchPage page = window.getActivePage();
		try {
			page.showView(IPageLayout.ID_PROP_SHEET);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void dispose() {
	}
}
