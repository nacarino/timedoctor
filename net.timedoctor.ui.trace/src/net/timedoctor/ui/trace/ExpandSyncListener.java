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
package net.timedoctor.ui.trace;

import java.util.ArrayList;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * Assumes the contents are expanded upon creation. 
 */
public class ExpandSyncListener implements MouseListener, SelectionListener {
	
	private boolean isExpanded = true;
	
	private ArrayList < IExpandClient > clients;

	/**
	 * Constructs an ExpandSyncListener, initializing the empty list of clients.
	 */
	public ExpandSyncListener() {
		clients = new ArrayList < IExpandClient > ();
	}

	public void widgetDefaultSelected(final SelectionEvent e) {
	}

	public void widgetSelected(final SelectionEvent e) {
		expand();
	}	
	
	public void mouseDoubleClick(final MouseEvent e) {
		expand();
	}

	public void mouseDown(final MouseEvent e) {
	}

	public void mouseUp(final MouseEvent e) {
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
	 * Handles expand/collapse of all clients.
	 * 
	 */
	private void expand() {
		if (isExpanded) {
			for (int i = 0; i < clients.size(); i++) {
				clients.get(i).collapse();
			}
		} else {
			for (int i = 0; i < clients.size(); i++) {
				clients.get(i).expand();
			}
		}
		isExpanded = !isExpanded;
	}
}
