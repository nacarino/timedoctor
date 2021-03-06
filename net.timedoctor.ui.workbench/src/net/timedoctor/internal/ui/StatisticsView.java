/*******************************************************************************
 * Copyright (c) 2007-2013 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NXP Semiconductors B.V. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.internal.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

import net.timedoctor.core.model.SampleLine;
import net.timedoctor.ui.statistics.IStatisticsViewPage;

public abstract class StatisticsView extends PageBookView implements ISelectionListener {
	
	/**
	 * The constructor.
	 */
	public StatisticsView() {
		super();
	}
	
	@Override
	public void init(IViewSite site) throws PartInitException {
		site.getPage().addSelectionListener(this);
		super.init(site);		
	}

	@Override
	public void dispose() {
		super.dispose();
		
		getSite().getPage().removeSelectionListener(this);
	}

	public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
		if (part == this || !(selection instanceof IStructuredSelection) || selection.isEmpty()) {
			return;
		}
		
		final IPage page = getCurrentPage();
		final Object selectedElement = ((IStructuredSelection) selection).getFirstElement();
		
		if (!(page instanceof IStatisticsViewPage) || !(selectedElement instanceof SampleLine))
			return;
		
		IStatisticsViewPage statisticsPage = (IStatisticsViewPage) page;
		statisticsPage.selectLine((SampleLine) selectedElement);
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage("Statistics unavailable");
        return page;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		IStatisticsViewPage page = getPage((TraceEditor)part);
		
		if (page != null) {
			if (page instanceof IPageBookViewPage) {
				initPage((IPageBookViewPage) page);
			}
			
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		
		//Use the default page
		return null;
	}
	
	protected abstract IStatisticsViewPage getPage(TraceEditor editor);

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		IStatisticsViewPage page = (IStatisticsViewPage) pageRecord.page;
		page.dispose();
		pageRecord.dispose();
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
        if (page != null) {
			return page.getActiveEditor();
		}

        return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {		
		return (part instanceof TraceEditor);
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {		
		super.partActivated(part);
	}
}