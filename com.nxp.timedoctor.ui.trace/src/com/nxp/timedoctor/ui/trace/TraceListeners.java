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
package com.nxp.timedoctor.ui.trace;

import java.util.ArrayList;

/**
 * Class to hold all the listeners related to the traceCanvas
 * 
 */
public class TraceListeners {
	private ArrayList<Object> listenerArrayList;

	/**
	 * The TraceListeners constructor
	 */
	public TraceListeners() {
		listenerArrayList = new ArrayList<Object>();
	}

	/**
	 * Adds the listener to the list of listeners
	 * @param listener
	 */
	public void addListener(final Object listener) {
		listenerArrayList.add(listener);
	}

	/**
	 * Returns the listener object
	 * 
	 * @param listenerClass
	 * 					The class of the listener to be obtained
	 * @return
	 * 			The listener object, if present in the list, <code>null</code> otherwise
	 */
	public Object getListener(final Class listenerClass) {
		Object o;
		for (int i = 0; i < listenerArrayList.size(); i++) {
			o = listenerArrayList.get(i);
			if (listenerClass.isInstance(o)) {
				return o;
			}
		}
		return null;
	}
}
