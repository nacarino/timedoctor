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

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.nxp.timedoctor.core.model.statistics.Statistic;

public class LineStatTreeViewer {
	private final String ITEM_HEADING 	= "statistic";
	private final String TOTAL_HEADING 	= "total";
	private final String LOAD_HEADING 	= "load";
	private final String MIN_HEADING 	= "min/exec";
	private final String AVG_HEADING 	= "avg/exec";
	private final String MAX_HEADING 	= "max/exec";

	private TreeViewer viewer;
	
	private final String[] columnNames = new String[] { 
			ITEM_HEADING, 
			TOTAL_HEADING,
			LOAD_HEADING,
			MIN_HEADING,
			AVG_HEADING,
			MAX_HEADING
	};
	
	public LineStatTreeViewer(final Composite parent) {
		createTreeViewer(createTree(parent));		
	}
	
	private Tree createTree(final Composite parent) {
		Tree tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		createColumns(tree);
		return tree;
	}
	
	private void createTreeViewer(final Tree tree) {
		viewer = new TreeViewer(tree);
		viewer.setContentProvider(new StatContentProvider());
		viewer.setLabelProvider(new LineStatLabelProvider());
		
		viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
	}

	private void createColumns(final Tree tree) {
		for (int i = 0; i < columnNames.length; i++) {
			TreeColumn column = new TreeColumn(tree, SWT.LEFT, i);
			column.setText(columnNames[i]);

			// TODO compute optimal width per column (possible?)
			column.setWidth((i==0)? 150: 80);
		}		
	}

	public void setLayoutData(final Object layoutData) {
		viewer.getTree().setLayoutData(layoutData);
	}
	
	public void setInput(final Statistic input) {
		viewer.setInput(input);
	}
	
	public void refresh() {
		viewer.refresh(true);
	}
}