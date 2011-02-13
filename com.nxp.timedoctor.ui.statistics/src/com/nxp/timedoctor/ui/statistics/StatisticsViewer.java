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
package com.nxp.timedoctor.ui.statistics;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextPrintOptions;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * A helper abstract class that statistics viewers can extend. This class creates a {@link StyledText}
 * widget, that is useful for printing and copying to clipboard. The {@link StyledText} widget always stays
 * hidden.
 * 
 * Clients must implement {@link #getControl(Composite)} and {@link #populateStyledText()}
 */
public abstract class StatisticsViewer {
	private StyledTextPrintOptions styledTextPrintOptions;
	private Composite parentComposite;

	protected StyledText styledText;
	private boolean styledTextUpdateRequired = true;

	public StatisticsViewer(final Composite topComposite) {
		parentComposite = new Composite(topComposite, SWT.NONE);
		
		StackLayout stackLayout = new StackLayout();
		parentComposite.setLayout(stackLayout);
		
		Control control = getControl(parentComposite);
		stackLayout.topControl = control;
		
		createStyledText(parentComposite);
	}

	/**
	 * Clients must implement this method and return the main control in the viewer
	 * 
	 * @param composite
	 * 		The parent composite
	 * @return
	 * 		The main {@link Control} in the view 
	 */
	protected abstract Control getControl(Composite composite);

	private void createStyledText(final Composite parent) {
		styledText = new StyledText(parent, SWT.MULTI | SWT.READ_ONLY);
		styledText.setFont(JFaceResources.getTextFont());
		styledText.setVisible(false);
		
		initStyledTextPrintOptions();
	}

	private void initStyledTextPrintOptions() {
		styledTextPrintOptions = new StyledTextPrintOptions();
	
		styledTextPrintOptions.footer = "\t\t" + StyledTextPrintOptions.PAGE_TAG;
		styledTextPrintOptions.jobName = "Task Statistics";
		styledTextPrintOptions.printLineBackground = true;
		styledTextPrintOptions.printTextFontStyle = true;
		styledTextPrintOptions.printTextBackground = true;
		styledTextPrintOptions.printTextForeground = true;
	}
	
	protected void updateRequired() {
		styledTextUpdateRequired = true;
	}

	protected abstract void populateStyledText();

	public void copy() {
		if (styledTextUpdateRequired) {
			populateStyledText();
			styledTextUpdateRequired = false;
		}
		
		styledText.selectAll();
		styledText.copy();
	}

	public void print() {
		PrinterData data = getPrinterData();
		
		if (data == null)
			return;
		
		if (styledTextUpdateRequired) {
			populateStyledText();
			styledTextUpdateRequired = false;
		}
		
		final Printer printer = new Printer(data);
		
		final Runnable runnable = styledText.print(printer, styledTextPrintOptions);
		Thread printingThread = new Thread("Statistics printer thread") {
			@Override
			public void run() {
				runnable.run();
				printer.dispose();
			}
		};		
		printingThread.start();
	}

	private PrinterData getPrinterData() {
		final Shell platformShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		PrintDialog dialog = new PrintDialog(platformShell , SWT.NONE);
		PrinterData data = dialog.open();
		
		if (data != null && data.printToFile) {
			final String s = getFileName(platformShell);
			if (s == null) {
				data = null;
			} else {
				data.fileName = s;
			}
		}
		
		return data;
	}

	private String getFileName(final Shell platformShell) {
		FileDialog dialog = new FileDialog(platformShell, SWT.SAVE);
		dialog.setFilterExtensions(new String[] {"*.prn", "*.*"});
		
		return dialog.open();
	}

	public void setLayoutData(final Object layoutData) {
		parentComposite.setLayoutData(layoutData);
	}
}
