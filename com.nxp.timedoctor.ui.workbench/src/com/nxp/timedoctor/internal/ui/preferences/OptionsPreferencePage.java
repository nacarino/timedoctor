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
package com.nxp.timedoctor.internal.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.nxp.timedoctor.ui.trace.TracePluginActivator;

/**
 * Class which will create the "Display options" preference page.
 */
public class OptionsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor hideEmptyLines;
	
	private IPropertyChangeListener listener;
	
	/**
	 * Constructor which will set the preference store and its default values.
	 *
	 */
	public OptionsPreferencePage() {
		super("TimeDoctor Options", FieldEditorPreferencePage.GRID);
		super.setPreferenceStore(TracePluginActivator.getDefault().getPreferenceStore());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		Label label = new Label(getFieldEditorParent(), 0);
		label.setText("Options to modify the Trace display preferences");
				
		BooleanFieldEditor autoHide = new BooleanFieldEditor(TracePluginActivator.AUTO_HIDE_PREFERENCE, 
				"Auto-hide empty lines", getFieldEditorParent());
		addField(autoHide);

		hideEmptyLines = new BooleanFieldEditor(TracePluginActivator.HIDE_EMPTY_LINE_PREFERENCE, 
				"Hide empty lines", getFieldEditorParent());
		addField(hideEmptyLines);
		
		if (getPreferenceStore().getBoolean(TracePluginActivator.AUTO_HIDE_PREFERENCE)) {
			hideEmptyLines.setEnabled(false, getFieldEditorParent());	
		}

		BooleanFieldEditor semaphoreQueue = new BooleanFieldEditor(TracePluginActivator.SEMAPHORE_QUEUE_PREFERENCE,
				"Display Semaphores as Queues", getFieldEditorParent());
		addField(semaphoreQueue);
		
		BooleanFieldEditor proportionalQueues = new BooleanFieldEditor(TracePluginActivator.PROPORTIONAL_QUEUES_PREFERENCE,
				"Display Queues proportionally", getFieldEditorParent());
		addField(proportionalQueues);

		BooleanFieldEditor proportionalCounters = new BooleanFieldEditor(TracePluginActivator.PROPORTIONAL_COUNTERS_PREFERENCE,
				"Display Counters proportionally", getFieldEditorParent());
		addField(proportionalCounters);
		
		BooleanFieldEditor subPixelLoad = new BooleanFieldEditor(TracePluginActivator.SUB_PIXEL_LOAD,
				"Display task height proportionally to its CPU load", getFieldEditorParent());
		addField(subPixelLoad);
		
		listener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(TracePluginActivator.AUTO_HIDE_PREFERENCE)) {
					boolean hidePreference = getPreferenceStore().getBoolean(TracePluginActivator.AUTO_HIDE_PREFERENCE);
					hideEmptyLines.setEnabled(!hidePreference, getFieldEditorParent());
				}
			}
		};
		
		getPreferenceStore().addPropertyChangeListener(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#dispose()
	 */
	@Override
	public void dispose() {
		getPreferenceStore().removePropertyChangeListener(listener);		
	}

	public void init(IWorkbench workbench) {
		//Do nothing
	}
}


