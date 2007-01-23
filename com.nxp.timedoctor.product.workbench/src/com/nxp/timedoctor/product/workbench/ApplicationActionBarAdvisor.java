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
package com.nxp.timedoctor.product.workbench;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.nxp.timedoctor.internal.ui.actions.OpenAction;
import com.nxp.timedoctor.internal.ui.actions.PropertyAction;
import com.nxp.timedoctor.internal.ui.actions.StatisticsAction;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    private IWorkbenchAction openAction;
    private IWorkbenchAction closeAction;
    private IWorkbenchAction exitAction;
    private IWorkbenchAction statisticsAction;
    private IWorkbenchAction propertyAction;
    private IWorkbenchAction preferenceAction;
    private IWorkbenchAction aboutAction;
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

        statisticsAction = new StatisticsAction(window);
        register(statisticsAction);
        
        propertyAction = new PropertyAction(window);
        register(propertyAction);

        preferenceAction = ActionFactory.PREFERENCES.create(window);
        register(preferenceAction);

        helpAction = ActionFactory.HELP_CONTENTS.create(window);
        register(helpAction);

        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
    }

    @Override
	protected void fillMenuBar(final IMenuManager menuBar) {
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
        MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
        
        menuBar.add(fileMenu);
        // Add a group marker indicating where action set menus will appear.
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);
        
        // File
        fileMenu.add(openAction);
        fileMenu.add(closeAction);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);
        
        // Window
        windowMenu.add(statisticsAction);
        windowMenu.add(propertyAction);
        windowMenu.add(new Separator());
        windowMenu.add(preferenceAction);
        
        // Help
        helpMenu.add(helpAction);
        helpMenu.add(new Separator());
        helpMenu.add(aboutAction);
    }    
}
