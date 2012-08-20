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
package net.timedoctor.ui.trace;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The Trace plug-in Activtor class
 *
 */
public class TracePluginActivator extends AbstractUIPlugin {
	private IPreferenceStore preferenceStore = null;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "net.timedoctor.ui.trace";
	
	// The shared instance
	private static TracePluginActivator plugin;
	
	/**
	 * String constant used for enable/disable auto hide in preference page.
	 */
	public static final String AUTO_HIDE_PREFERENCE = "AUTO_HIDE_PREFERENCE";
	/**
	 * String constant used in preference page to hide empty lines.
	 */
	public static final String HIDE_EMPTY_LINE_PREFERENCE = "HIDE_EMPTY_LINE_PREFERENCE";
	/**
	 * String constant used for in preference page for displaying semaphores as queues.
	 */
	public static final String SEMAPHORE_QUEUE_PREFERENCE = "SEMAPHORE_QUEUE_PREFERENCE";
	/**
	 * String constant used in preference page for displaying proportional queues.
	 */
	public static final String PROPORTIONAL_QUEUES_PREFERENCE = "PROPORTIONAL_QUEUES_PREFERENCE";
	/**
	 * String constant used in preference page for displaying proportional counters.
	 */
	public static final String PROPORTIONAL_COUNTERS_PREFERENCE = "PROPORTIONAL_COUNTERS_PREFERENCE";
	
	/**
	 * String constant used in preference page for Sub pixel load.
	 */
	public static final String SUB_PIXEL_LOAD = "SUB_PIXEL_LOAD";
	
	public TracePluginActivator() {
		plugin = this;
	}
	
	/**
	 * The instance 
	 * 
	 * @return The shared {@link TracePluginActivator} instance
	 */
	public static TracePluginActivator getDefault() {
		return plugin;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#getPreferenceStore()
	 */
	@Override
	public IPreferenceStore getPreferenceStore() {
		 // Create the preference store lazily.
        if (preferenceStore == null) {
            preferenceStore = new ScopedPreferenceStore(new InstanceScope(),getBundle().getSymbolicName());
            initializeDefaultPreferenceStore();
        }
        return preferenceStore;
	}

	private void initializeDefaultPreferenceStore() {
		preferenceStore.setDefault(TracePluginActivator.AUTO_HIDE_PREFERENCE,             true);
		preferenceStore.setDefault(TracePluginActivator.HIDE_EMPTY_LINE_PREFERENCE,       false);
		preferenceStore.setDefault(TracePluginActivator.SEMAPHORE_QUEUE_PREFERENCE,       false);
		preferenceStore.setDefault(TracePluginActivator.PROPORTIONAL_QUEUES_PREFERENCE,   false);
		preferenceStore.setDefault(TracePluginActivator.PROPORTIONAL_COUNTERS_PREFERENCE, false);
	}
}
