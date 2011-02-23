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
package net.timedoctor.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import net.timedoctor.internal.ui.TraceEditor;

public class CopyAction extends Action implements IWorkbenchAction {
	private TraceEditor editor;

	public CopyAction(final TraceEditor editor) {
		this.editor = editor;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		final Display display = editor.getEditorSite().getWorkbenchWindow().getShell().getDisplay();
		BusyIndicator.showWhile(display, new Runnable() {
			public void run() {
				final Image image = editor.getScreenShot();
				AWTClipboardUtil.getInstance().copyToClipBoard(image.getImageData());
				image.dispose();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionFactory$IWorkbenchAction#dispose()
	 */
	public void dispose() {
	}
}
