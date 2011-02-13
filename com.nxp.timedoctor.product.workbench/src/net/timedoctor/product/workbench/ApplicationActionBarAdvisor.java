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
package net.timedoctor.product.workbench;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import net.timedoctor.internal.ui.actions.OpenAction;
import net.timedoctor.product.workbench.actions.FindAndUpdateAction;
import net.timedoctor.product.workbench.actions.ManageConfigurationAction;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    private IWorkbenchAction openAction;
    private IWorkbenchAction closeAction;
    private IWorkbenchAction exitAction;
    private IWorkbenchAction copyAction;
    private IWorkbenchAction preferenceAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction updateAction;
    private IWorkbenchAction manageConfigurationAction;
    private IWorkbenchAction helpAction;
    
    public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer) {
        super(configurer);
    }

    @Override
	protected void makeActions(final IWorkbenchWindow window) {
    	openAction = new OpenAction(window);
    	register(openAction);
    	
    	closeAction = ActionFactory.CLOSE.create(window);
    	register(closeAction);
    	
        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);
 
        copyAction = ActionFactory.COPY.create(window);
        register(copyAction);
        
        preferenceAction = ActionFactory.PREFERENCES.create(window);
        register(preferenceAction);

        helpAction = ActionFactory.HELP_CONTENTS.create(window);
        register(helpAction);
        
        updateAction = new FindAndUpdateAction(window);
        register(updateAction);
        
        manageConfigurationAction = new ManageConfigurationAction(window);
        register(manageConfigurationAction);
        
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
    }

    @Override
	protected void fillMenuBar(final IMenuManager menuBar) {
    	IWorkbenchWindow window = getActionBarConfigurer().getWindowConfigurer().getWindow();
    	
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        MenuManager editMenu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT);
        MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
        MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        // Add a group marker indicating where action set menus will appear.
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);
        
        // File
        fileMenu.add(openAction);
        fileMenu.add(closeAction);
        IContributionItem recentFileList = ContributionItemFactory.REOPEN_EDITORS.create(window);
        fileMenu.add(recentFileList);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);
        
        //Edit
        editMenu.add(copyAction);
        
        // Window
        MenuManager viewMenu = new MenuManager("Show View");
        IContributionItem showViewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
        viewMenu.add(showViewList);
        windowMenu.add(viewMenu);
        windowMenu.add(new Separator());
        windowMenu.add(preferenceAction);
        
        // Help
        helpMenu.add(helpAction);
        helpMenu.add(new Separator());
        
        //Update manager
        MenuManager updateMenuManager = new MenuManager("Software Updates");
        updateMenuManager.add(updateAction);
        updateMenuManager.add(manageConfigurationAction);
        
        helpMenu.add(updateMenuManager);
        helpMenu.add(new Separator());
        helpMenu.add(aboutAction);
    }    
}
