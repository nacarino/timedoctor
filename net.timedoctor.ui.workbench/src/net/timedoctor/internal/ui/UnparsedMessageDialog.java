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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class UnparsedMessageDialog extends MessageDialog {

	private static final String COPY_LABEL = "Copy";
	private String unParsedLines;
	private StyledText text;

	private UnparsedMessageDialog(String unParsedLines, Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.unParsedLines = unParsedLines;
	}
	
	public static void displayMessage(final Shell parent, final String message) {
		if (parent == null)
			throw new IllegalArgumentException("parent cannot be null");
		if (message == null)
			throw new IllegalArgumentException("message cannot be null");
		
		UnparsedMessageDialog dialog = new UnparsedMessageDialog(message, 
				parent,
				"Unparsed lines", 
				null, 
				"These lines in the Trace file were not parsed",
				WARNING, 
				new String[] {IDialogConstants.OK_LABEL, COPY_LABEL}, 
				IDialogConstants.OK_ID);
		dialog.open();
		return;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.MessageDialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			super.buttonPressed(buttonId);
		} else {
			copyToClipBoard();
		}
	}

	private void copyToClipBoard() {
		text.selectAll();
		text.copy();
		text.setSelection(-1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createCustomArea(final Composite parent) {
		text = new StyledText(parent, SWT.READ_ONLY | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL );
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalAlignment = SWT.FILL;
		data.widthHint = messageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		data.heightHint = 80;
		text.setLayoutData(data);
		
		text.setText(unParsedLines);
		return text;
	}
}
