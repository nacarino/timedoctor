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
package net.timedoctor.ui.statistics;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import net.timedoctor.core.model.statistics.Statistic;


public class StatContentProvider implements ITreeContentProvider {
	public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
	}
	
	public void dispose() {
	}
	
	 /**
	   * Gets the root element(s) of the tree
	   * 
	   * @param arg0
	   *            the input data
	   * @return Object[]
	   */
	public Object[] getElements(final Object parent) {
		return getChildren(parent);
	}

	public Object[] getChildren(final Object parentElement) {
		return ((Statistic) parentElement).getChildren().toArray();
	}

	public Object getParent(final Object element) {
		return ((Statistic) element).getParent();
	}

	public boolean hasChildren(final Object element) {
		return (((Statistic) element).getChildren().size() > 0);
	}
}
