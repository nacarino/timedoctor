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
package net.timedoctor.internal.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * TimeDoctor top-level node dummy page 
 */
public class TimeDoctorPreferences extends PreferencePage implements
		IWorkbenchPreferencePage {

	public TimeDoctorPreferences() {
	}

	public TimeDoctorPreferences(String title) {
		super(title);
	}

	public TimeDoctorPreferences(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(Composite parent) {
	    Composite comp = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        comp.setLayout(layout);
        comp.setFont(parent.getFont());
        
        Label label = new Label(comp, SWT.LEFT);
        label.setText("TimeDoctor visualization preferences");
        
		return comp;
	}

	public void init(IWorkbench workbench) {
		//Do nothing
	}
}
