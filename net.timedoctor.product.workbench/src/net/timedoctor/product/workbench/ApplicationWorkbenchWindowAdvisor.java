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
package net.timedoctor.product.workbench;

import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import net.timedoctor.internal.ui.FileDropListener;
import net.timedoctor.internal.ui.actions.FileOpener;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
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
	public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(400, 300));
        configurer.setTitle("TimeDoctor Performance Visualizer");
        
        // Add support for dropping files onto the editor area.
        configurer.addEditorAreaTransfer(FileTransfer.getInstance());
        configurer.configureEditorAreaDropListener(new FileDropListener(configurer.getWindow()));
        
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
    }

	@Override
	public void postWindowOpen() {
		if (arguments != null && arguments.length > 0) {
			FileOpener opener = new FileOpener(getWindowConfigurer().getWindow());
			opener.openFiles(arguments);
		}
	}
}
