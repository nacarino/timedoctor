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

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	public Object start(IApplicationContext context) throws Exception {
		final Display display = PlatformUI.createDisplay();
		try {
			String[] arguments = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
			
			int code = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor(arguments));
			
			// exit the application with an appropriate return code
			return code == PlatformUI.RETURN_RESTART ? EXIT_RESTART : EXIT_OK;
		} finally {
			if (display != null)
				display.dispose();
		}
	}

	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();

		if (workbench == null)
			return;
		
		final Display display = workbench.getDisplay();
		
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
