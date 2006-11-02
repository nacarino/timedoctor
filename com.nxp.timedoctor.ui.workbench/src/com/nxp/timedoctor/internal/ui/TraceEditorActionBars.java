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
package com.nxp.timedoctor.internal.ui;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.EditorActionBarContributor;

import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.ui.trace.actions.GoToTimeAction;
import com.nxp.timedoctor.ui.trace.actions.NextAction;
import com.nxp.timedoctor.ui.trace.actions.PreviousAction;
import com.nxp.timedoctor.ui.trace.actions.TraceAction;
import com.nxp.timedoctor.ui.trace.actions.ZoomBackAction;
import com.nxp.timedoctor.ui.trace.actions.ZoomFitAction;
import com.nxp.timedoctor.ui.trace.actions.ZoomInAction;
import com.nxp.timedoctor.ui.trace.actions.ZoomOutAction;

/**
 * This class performes retargetable actions for menu items.
 */
public class TraceEditorActionBars extends EditorActionBarContributor {
	private final static String PLUGIN_COMMAND_ID = "com.nxp.timedoctor.ui.commands";

	private ZoomInAction zoomInHandler;
	private RetargetAction zoomInAction;
	private ActionHandler zoomInCommandHandler;
	
	private ZoomOutAction zoomOutHandler;
	private RetargetAction zoomOutAction;
	private ActionHandler zoomOutCommandHandler;
	
	private RetargetAction zoomBackAction;
	private ZoomBackAction zoomBackHandler;
	private ActionHandler zoomBackCommandHandler;
	
	private RetargetAction zoomFitAction;
	private ZoomFitAction zoomFitHandler;
	private ActionHandler zoomFitCommandHandler;
	
	private RetargetAction nextAction;
	private TraceAction nextHandler;
	private ActionHandler nextCommandHandler;
	
	private RetargetAction previousAction;
	private PreviousAction previousHandler;
	private ActionHandler previousCommandHandler;
	
	private TraceAction goToTimeAction;
	private ActionHandler goToTimeCommandHandler;

	/**
	 * Constructor for TraceEditorActionBars.
	 */
	public TraceEditorActionBars() {
		// Retargetable editor actions
		zoomInAction = new RetargetAction(ZoomInAction.ID, "Zoom In");
		zoomInHandler = new ZoomInAction("Zoom In");		
		zoomInHandler.setActionDefinitionId(PLUGIN_COMMAND_ID + ".ZoomIn");
		zoomInCommandHandler = new ActionHandler(zoomInHandler);
		
		zoomOutAction = new RetargetAction(ZoomOutAction.ID, "Zoom Out");
		zoomOutHandler = new ZoomOutAction("Zoom Out");		
		zoomOutHandler.setActionDefinitionId(PLUGIN_COMMAND_ID + ".ZoomOut");
		zoomOutCommandHandler = new ActionHandler(zoomOutHandler);
		
		zoomBackAction = new RetargetAction(ZoomBackAction.ID, "Zoom Back");
		zoomBackHandler = new ZoomBackAction("Zoom Back");
		zoomBackHandler.setActionDefinitionId(PLUGIN_COMMAND_ID + ".ZoomBack");
		zoomBackCommandHandler = new ActionHandler(zoomBackHandler);
		
		zoomFitAction = new RetargetAction(ZoomFitAction.ID, "Zoom Fit");
		zoomFitHandler = new ZoomFitAction("Zoom Fit");
		zoomFitHandler.setActionDefinitionId(PLUGIN_COMMAND_ID + ".ZoomFit");
		zoomFitCommandHandler = new ActionHandler(zoomFitHandler);
		
		nextAction = new RetargetAction(NextAction.ID, "Next Event");
		nextHandler = new NextAction("Next Event");		
		nextHandler.setActionDefinitionId(PLUGIN_COMMAND_ID + ".Next");
		nextCommandHandler = new ActionHandler(nextHandler);
		
		previousAction = new RetargetAction(PreviousAction.ID, "Previous Event");
		previousHandler = new PreviousAction("Previous Event");
		previousHandler.setActionDefinitionId(PLUGIN_COMMAND_ID + ".Previous");
		previousCommandHandler = new ActionHandler(previousHandler);
		
		// Editor actions
		goToTimeAction = new GoToTimeAction("Goto Time");		
		goToTimeAction.setActionDefinitionId(PLUGIN_COMMAND_ID + ".GoToTime");
		goToTimeCommandHandler = new ActionHandler(goToTimeAction);
	}

	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);
		
		MenuManager traceMenu = new MenuManager("&Trace", "Trace");
		menuManager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS,
				traceMenu);
		MenuManager zoomMenu = new MenuManager("&Zoom", "Zoom");
		traceMenu.add(zoomMenu);
		zoomMenu.add(zoomInAction);
		zoomMenu.add(zoomOutAction);
		zoomMenu.add(zoomBackAction);
		zoomMenu.add(zoomFitAction);
		traceMenu.add(nextAction);
		traceMenu.add(previousAction);
		traceMenu.add(goToTimeAction);
	}

	public void dispose() {		
		getPage().removePartListener(zoomInAction);
		getPage().removePartListener(zoomOutAction);
		getPage().removePartListener(zoomBackAction);
		getPage().removePartListener(zoomFitAction);		
		getPage().removePartListener(nextAction);
		getPage().removePartListener(previousAction);
	}

	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
		
		page.addPartListener(zoomInAction);
		page.addPartListener(zoomBackAction);
		page.addPartListener(zoomOutAction);
		page.addPartListener(zoomFitAction);		
		page.addPartListener(nextAction);
		page.addPartListener(previousAction);
		
		IWorkbenchPart activePart = page.getActivePart();
		if (activePart != null) {
			zoomInAction.partActivated(activePart);
			zoomOutAction.partActivated(activePart);
			zoomFitAction.partActivated(activePart);
			zoomBackAction.partActivated(activePart);
			nextAction.partActivated(activePart);
			previousAction.partActivated(activePart);
		}
	}

	public void setActiveEditor(IEditorPart editor) {
		super.setActiveEditor(editor);
		
		TraceEditor traceEditor = (TraceEditor) editor;
		
		ZoomModel zoomModel = traceEditor.getZoomModel();
		TraceModel traceModel = traceEditor.getTraceModel();
		
		zoomInHandler.updateModel(traceModel, zoomModel);
		zoomOutHandler.updateModel(traceModel, zoomModel);
		zoomBackHandler.updateModel(traceModel, zoomModel);
		zoomFitHandler.updateModel(traceModel, zoomModel);		
		nextHandler.updateModel(traceModel, zoomModel);
		previousHandler.updateModel(traceModel, zoomModel);
		goToTimeAction.updateModel(traceModel, zoomModel);
		
		final IHandlerService service = (IHandlerService) editor
				.getEditorSite().getService(IHandlerService.class);
		
		service.activateHandler(zoomInHandler.getActionDefinitionId(), zoomInCommandHandler);
		service.activateHandler(zoomOutHandler.getActionDefinitionId(),zoomOutCommandHandler);
		service.activateHandler(zoomBackHandler.getActionDefinitionId(), zoomBackCommandHandler);
		service.activateHandler(zoomFitHandler.getActionDefinitionId(), zoomFitCommandHandler);
		service.activateHandler(nextHandler.getActionDefinitionId(), nextCommandHandler);
		service.activateHandler(previousHandler.getActionDefinitionId(), previousCommandHandler);
		service.activateHandler(goToTimeAction.getActionDefinitionId(), goToTimeCommandHandler);
	}
}
