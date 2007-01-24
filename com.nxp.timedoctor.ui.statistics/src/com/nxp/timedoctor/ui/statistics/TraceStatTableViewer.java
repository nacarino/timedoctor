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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.nxp.timedoctor.core.model.statistics.Statistic;

public class TraceStatTableViewer {
	private final String TASK_HEADING 		= "Task";
	private final String EXECUTIONS_HEADING = "Nr. Executions";
	private final String EXEC_RATE_HEADING 	= "Nr. Executions/Second";
	private final String LOAD_HEADING 		= "Load (%)";
	private final String INT_RATE_HEADING 	= "Interrupts/Second";

	private TableViewer viewer;
	
	private final String[] columnNames = new String[] { 
			TASK_HEADING,
			EXECUTIONS_HEADING,
			EXEC_RATE_HEADING,
			LOAD_HEADING,
			INT_RATE_HEADING
	};
	
	public TraceStatTableViewer(final Composite parent) {
		createTableViewer(createTable(parent));		
	}
	
	private Table createTable(final Composite parent) {
		Table tree = new Table(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		createColumns(tree);
		return tree;
	}
	
	private void createTableViewer(final Table tree) {
		viewer = new TableViewer(tree);
		viewer.setContentProvider(new StatContentProvider());
		viewer.setLabelProvider(new TraceStatLabelProvider());
	}

	private void createColumns(final Table tree) {
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(tree, SWT.LEFT, i);
			column.setText(columnNames[i]);

			// TODO compute optimal width per column (possible?)
			column.setWidth((i==0)? 150: 80);
		}		
	}

	public void setLayoutData(final Object layoutData) {
		viewer.getTable().setLayoutData(layoutData);
	}
	
	public void setInput(final Statistic input) {
		viewer.setInput(input);
	}
	
	public void refresh() {
		viewer.refresh(true);
	}
}