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

import java.util.ArrayList;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.nxp.timedoctor.core.model.statistics.Statistic;


public class TraceStatTableViewer extends StatisticsViewer {
	private final static String TASK_HEADING 		= "Task";
	private final static String EXECUTIONS_HEADING  = "Nr. Executions";
	private final static String EXEC_RATE_HEADING 	= "Nr. Executions/Second";
	private final static String LOAD_HEADING 		= "Load (%)";
	private final static String INT_RATE_HEADING 	= "Interrupts/Second";

	private final static String[] columnNames = new String[] { 
			TASK_HEADING,
			EXECUTIONS_HEADING,
			EXEC_RATE_HEADING,
			LOAD_HEADING,
			INT_RATE_HEADING
	};

	private TableViewer viewer;
	
	public TraceStatTableViewer(final Composite topComposite) {
		super(topComposite);
	}
	
	@Override
	public Control getControl(Composite parentComposite) {
		final Table table = createTable(parentComposite);
		createTableViewer(table);
		updateColumnSize();
		
		return table;
	}
	
	private Table createTable(final Composite parent) {
		Table table = new Table(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		createColumns(table);
		return table;
	}
	
	private void createTableViewer(final Table table) {
		viewer = new TableViewer(table);
		viewer.setContentProvider(new StatContentProvider());
		viewer.setLabelProvider(new TraceStatLabelProvider());
	}

	private void createColumns(final Table table) {
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(table, SWT.LEFT, i);
			column.setText(columnNames[i]);
		}		
	}
	
	public void setInput(final Statistic input) {
		viewer.setInput(input);
	}
	
	public void refresh() {
		viewer.refresh(true);
		updateColumnSize();
		updateRequired();
	}
	
	private void updateColumnSize() {
		for (TableColumn column : viewer.getTable().getColumns()) {
			column.pack();
		}
	}

	@Override
	protected void populateStyledText() {
		styledText.setText("");
		
		final Table table = viewer.getTable();
		
		StyleRange boldStyle = new StyleRange();
		boldStyle.fontStyle = SWT.BOLD;
		
		StyleRange normalStyle = new StyleRange();
		normalStyle.fontStyle = SWT.NORMAL;
		
		// Copy column texts
		TableColumn[] columns = table.getColumns();
		TableItem[] items = table.getItems();
		ArrayList<ColumnFormatter> columnFormatterArray = new ArrayList<ColumnFormatter>(columns.length);
		
		for (int col = 0; col < columns.length; col++) {
			ColumnFormatter columnFormatter = new ColumnFormatter();
			columnFormatter.addString(table.getColumn(col).getText());
			
			for (TableItem item: items) {
				columnFormatter.addString(item.getText(col));
			}
			
			columnFormatterArray.add(columnFormatter);
		}
		
		//Make all elements of the header bold		
		for (int col = 0; col < columns.length; col++) {
			ColumnFormatter columnFormatter = columnFormatterArray.get(col);
			final String formattedString = columnFormatter.getFormattedString(0);
			
			styledText.append(formattedString + "\t");
		}
		
		final int headerLength = styledText.getText().length();
		
		boldStyle.start = 0;
		boldStyle.length = headerLength;
		
		styledText.setStyleRange(boldStyle);
		styledText.append("\n");
		
		for (int row = 1; row < items.length + 1; row++) {//Rest of the rows			
			for (int col = 0; col < columns.length; col++) {
				ColumnFormatter columnFormatter = columnFormatterArray.get(col);
				final String formattedString = columnFormatter.getFormattedString(row);				
				styledText.append(formattedString + "\t");
			}
			styledText.append("\n");
		}
		
		normalStyle.start = headerLength;
		normalStyle.length = styledText.getText().length() - headerLength;
		styledText.setStyleRange(normalStyle);
	}
}