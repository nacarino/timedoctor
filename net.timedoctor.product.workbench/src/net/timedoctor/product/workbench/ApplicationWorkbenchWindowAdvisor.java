/*******************************************************************************
 * Copyright (c) 2006-2012 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.product.workbench;

import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import net.timedoctor.internal.ui.FileDropListener;
import net.timedoctor.internal.ui.actions.FileOpener;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	private Control page;
	private Control statusLine;
	private Control coolBar;

	private ApplicationActionBarAdvisor actionBars;
	private String[] arguments;
	
	public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer, String[] arguments) {
        super(configurer);
        this.arguments = arguments;
    }

    @Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
    	actionBars = new ApplicationActionBarAdvisor(configurer);
    	return actionBars;
    }
    
	@Override
	public void createWindowContents(final Shell shell) {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		Menu menu = configurer.createMenuBar();
		shell.setMenuBar(menu);
		FormLayout layout = new FormLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		shell.setLayout(layout);
		
		coolBar = configurer.createCoolBarControl(shell);
		page = configurer.createPageComposite(shell);
		statusLine = configurer.createStatusLineControl(shell);
		layoutNormal();
	}

	private void layoutNormal() {
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		coolBar.setLayoutData(data);
		
		data = new FormData();
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		statusLine.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(coolBar);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(statusLine);
		page.setLayoutData(data);
		
		layout();
	}

	private void layout() {
		getWindowConfigurer().getWindow().getShell().layout(true);
		if (page != null) {
			((Composite) page).layout(true);
		}
	}

	public void setShowCoolBar(final boolean visible) {
		if (visible) {
			if (coolBar.isVisible()) {
				return;
			}
			
			FormData data = (FormData) page.getLayoutData();
			data.top = new FormAttachment(coolBar, 0);
			page.setLayoutData(data);
			coolBar.setVisible(true);
		} else {
			if (!coolBar.isVisible()) {
				return;
			}
			
			FormData data = (FormData) page.getLayoutData();
			data.top = new FormAttachment(0, 0);
			page.setLayoutData(data);
			coolBar.setVisible(false);
		}
		layout();
	}

	public boolean getShowCoolBar() {
		return ((coolBar != null) && coolBar.isVisible());
	}

	public void setShowStatusLine(final boolean visible) {
		if (visible) {
			if (statusLine.isVisible()) {
				return;
			}

			FormData data = (FormData) page.getLayoutData();
			data.bottom = new FormAttachment(statusLine, 0);
			page.setLayoutData(data);
			statusLine.setVisible(true);
		} else {
			if (!statusLine.isVisible()) {
				return;
			}

			FormData data = (FormData) page.getLayoutData();
			data.bottom = new FormAttachment(100, 0);
			page.setLayoutData(data);
			statusLine.setVisible(false);
		}
		layout();
	}

	public boolean getShowStatusLine() {
		return ((statusLine != null) && statusLine.isVisible());
	}
    
    @Override
	public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(400, 300));
        configurer.setTitle("TimeDoctor Performance Visualizer");
        
        // Add support for dropping files onto the editor area.
        configurer.addEditorAreaTransfer(FileTransfer.getInstance());
        configurer.configureEditorAreaDropListener(new FileDropListener(configurer.getWindow()));        
    }

	@Override
	public void postWindowOpen() {
		if (arguments != null && arguments.length > 0) {
			FileOpener opener = new FileOpener(getWindowConfigurer().getWindow());
			opener.openFiles(arguments);
		}
	}
}
