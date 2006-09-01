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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

/**
 * Assumes the contents are expanded upon creation. 
 */
public class ExpandSyncListener implements SelectionListener {

	/**
	 * Holds the ExpandClients associated with this listener.
	 */
	// Checkstyle not compatible with J2SE5 type parameterization
	private ArrayList<IExpandClient> clients;

	/**
	 * Constructs an ExpandSyncListener, initializing the empty list of clients.
	 */
	// Checkstyle not compatible with J2SE5 type parameterization
	public ExpandSyncListener() {
		clients = new ArrayList<IExpandClient>();
	}

	/**
	 * Adds a client to the ExpandSyncListener.
	 * 
	 * @param client
	 *            the client to add
	 */
	public final void addClient(final IExpandClient client) {
		clients.add(client);
	}

	/**
	 * Empty method -- listener does nothing on this type of event.
	 * 
	 * @param e
	 *            SelectionEvent
	 */
	public final void widgetDefaultSelected(final SelectionEvent e) {
	}

	/**
	 * Method to handle selection of this listener's clients. Syncronizes
	 * expand/collapse events across all clients.
	 * 
	 * @param e
	 *            SelectionEvent from one of this listener's clients
	 */
	public final void widgetSelected(final SelectionEvent e) {
		Button expandButton = (Button) e.widget;

		if (expandButton.getAlignment() == SWT.UP) {
			collapse();
			expandButton.setAlignment(SWT.DOWN);
		} else {
			expand();
			expandButton.setAlignment(SWT.UP);
		}
	}

	/**
	 * Handles expanding all clients.
	 * 
	 */
	private void expand() {
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).expand();
		}
	}

	/**
	 * Handles collapsing all clients.
	 * 
	 */
	private void collapse() {
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).collapse();
		}
	}
}
