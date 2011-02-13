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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import net.timedoctor.core.model.statistics.Statistic;

public class LineStatTreeViewer extends StatisticsViewer {
	private final static String ITEM_HEADING 	= "Statistic";
	private final static String TOTAL_HEADING 	= "Total";
	private final static String LOAD_HEADING 	= "Load";
	private final static String MIN_HEADING 	= "Minimum/Execution";
	private final static String AVG_HEADING 	= "Average/Execution";
	private final static String MAX_HEADING 	= "Maximum/Execution";

	private TreeViewer viewer;
	
	private final static String[] columnNames = new String[] { 
			ITEM_HEADING, 
			TOTAL_HEADING,
			LOAD_HEADING,
			MIN_HEADING,
			AVG_HEADING,
			MAX_HEADING
	};
	
	public LineStatTreeViewer(final Composite topComposite) {
		super(topComposite);
	}
	
	@Override
	protected Control getControl(Composite parentComposite) {
		Tree tree = createTree(parentComposite);
		createTreeViewer(tree);
		updateColumnSize();		
		return tree;
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
		}
	}

	public void setInput(final Statistic input) {
		viewer.setInput(input);
		viewer.getTree().setEnabled(input != null);
		updateRequired();
		refresh();
	}

	private void refresh() {
		viewer.refresh(true);
		updateColumnSize();
	}
	
	private void updateColumnSize() {
		for (TreeColumn column : viewer.getTree().getColumns()) {
			column.pack();
		}
	}

	@Override
	protected void populateStyledText() {
		styledText.setText(""); //Clear the text
		
		final Tree tree = viewer.getTree();
		
		StyleRange boldStyle = new StyleRange();
		boldStyle.fontStyle = SWT.BOLD;
		
		StyleRange normalStyle = new StyleRange();
		normalStyle.fontStyle = SWT.NORMAL;
		
		//Copy column text
		TreeColumn[] columns = tree.getColumns();
		
		List<TreeItem> treeItemsList = getTreeItemsAsList(tree);
		ArrayList<ColumnFormatter> columnFormatterArray = new ArrayList<ColumnFormatter>(columns.length);
		
		for (int col = 0; col < columns.length; col++) {
			ColumnFormatter columnFormatter = new ColumnFormatter();
			columnFormatter.addString(tree.getColumn(col).getText());
			
			for (TreeItem item: treeItemsList) {
				columnFormatter.addString(item.getText(col));
			}
			
			columnFormatterArray.add(columnFormatter);
		}
		
		for (int row = 0; row < treeItemsList.size() + 1; row++) {
			for (int col = 0; col < columns.length; col++) {
				final int currentOffset = styledText.getText().length();
				
				ColumnFormatter columnFormatter = columnFormatterArray.get(col);
				final String formattedString = columnFormatter.getFormattedString(row);
				
				styledText.append(formattedString);
				styledText.append("\t");
				
				if (row == 0 || col == 0) {
					boldStyle.start = currentOffset;
					boldStyle.length = formattedString.length() + 1;
					styledText.setStyleRange(boldStyle);
				} else {
					normalStyle.start = currentOffset;
					normalStyle.length = formattedString.length() + 1;
					styledText.setStyleRange(normalStyle);
				}
			}
			styledText.append("\n");
		}
	}

	private List<TreeItem> getTreeItemsAsList(final Tree tree) {
		List<TreeItem> list = new ArrayList<TreeItem>(tree.getItemCount() * 2);
		appendAllTreeItems(tree.getItems(), list);
		return list;
	}

	private void appendAllTreeItems(TreeItem[] items, List<TreeItem> list) {
		for (TreeItem subItem : items) {
			list.add(subItem);
			appendAllTreeItems(subItem.getItems(), list);
		}
	}
}