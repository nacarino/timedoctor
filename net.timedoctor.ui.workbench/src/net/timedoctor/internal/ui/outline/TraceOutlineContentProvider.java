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
package net.timedoctor.internal.ui.outline;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.Section;
import net.timedoctor.core.model.SectionList;
import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.SampleLine.LineType;

/**
 * The {@link ITreeContentProvider} for the TraceOutlinePage tree viewer
 *
 */
public class TraceOutlineContentProvider implements ITreeContentProvider {
	private static final Object[] NO_CHILDREN = new Object[0];
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if ( parentElement instanceof TraceModel ) {
			
			TraceModel traceModel = (TraceModel)parentElement;
			
			//Code repetition, also in MainViewer. Can be refactored into TraceModel
			SectionList list = traceModel.getSections();
			ArrayList<Section> sectionArray = new ArrayList<Section>();
			Section section;
			
			for ( LineType type : LineType.values() ) {
				if ( ((section = list.getSection(type)) != null) && (type != LineType.PORTS)) {
					sectionArray.add(section);
				}
			}
			
			return sectionArray.toArray();
			
		} else if ( parentElement instanceof Section ) {
			Section section = (Section)parentElement;
			return section.getLines().toArray();
		} else {
			return NO_CHILDREN;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof SampleLine)
			return ((SampleLine)element).getSection();
		else
			return null;
			
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length != 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if ( inputElement instanceof TraceModel) {
			return getChildren(inputElement);
		} else {
			return NO_CHILDREN;
		}	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
