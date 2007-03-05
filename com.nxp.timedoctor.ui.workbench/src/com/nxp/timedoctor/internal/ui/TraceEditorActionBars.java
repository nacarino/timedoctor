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

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.EditorActionBarContributor;

import com.nxp.timedoctor.core.model.SampleLine;
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
 * This class performs retargetable actions for menu items.
 */
public class TraceEditorActionBars extends EditorActionBarContributor implements Observer {
	private final static String PLUGIN_COMMAND_ID = "com.nxp.timedoctor.ui.commands";

	private ZoomInAction zoomInAction;
	private ActionHandler zoomInCommandHandler;
	
	private ZoomOutAction zoomOutAction;
	private ActionHandler zoomOutCommandHandler;
	
	private ZoomBackAction zoomBackAction;
	private ActionHandler zoomBackCommandHandler;
	
	private ZoomFitAction zoomFitAction;
	private ActionHandler zoomFitCommandHandler;
	
	private TraceAction nextAction;
	private ActionHandler nextCommandHandler;
	
	private PreviousAction previousAction;
	private ActionHandler previousCommandHandler;
	
	private TraceAction goToTimeAction;
	private ActionHandler goToTimeCommandHandler;

	private ZoomModel zoomModel;
	
	/**
	 * Constructor for TraceEditorActionBars.
	 */
	public TraceEditorActionBars() {
		// Should be retargetable editor actions,
		// must be updated to Eclipse 3.2 new command handler way of working
		zoomInAction = new ZoomInAction("Zoom In");		
		zoomInAction.setActionDefinitionId(PLUGIN_COMMAND_ID + ".ZoomIn");
		zoomInCommandHandler = new ActionHandler(zoomInAction);
		
		zoomOutAction = new ZoomOutAction("Zoom Out");		
		zoomOutAction.setActionDefinitionId(PLUGIN_COMMAND_ID + ".ZoomOut");
		zoomOutCommandHandler = new ActionHandler(zoomOutAction);
		
		zoomBackAction = new ZoomBackAction("Zoom Back");
		zoomBackAction.setActionDefinitionId(PLUGIN_COMMAND_ID + ".ZoomBack");
		zoomBackCommandHandler = new ActionHandler(zoomBackAction);
		
		zoomFitAction = new ZoomFitAction("Zoom Fit");
		zoomFitAction.setActionDefinitionId(PLUGIN_COMMAND_ID + ".ZoomFit");
		zoomFitCommandHandler = new ActionHandler(zoomFitAction);
		
		nextAction = new NextAction("Next Event");		
		nextAction.setActionDefinitionId(PLUGIN_COMMAND_ID + ".Next");
		nextCommandHandler = new ActionHandler(nextAction);
		
		previousAction = new PreviousAction("Previous Event");
		previousAction.setActionDefinitionId(PLUGIN_COMMAND_ID + ".Previous");
		previousCommandHandler = new ActionHandler(previousAction);
		
		// Editor actions
		goToTimeAction = new GoToTimeAction("Goto Time");		
		goToTimeAction.setActionDefinitionId(PLUGIN_COMMAND_ID + ".GoToTime");
		goToTimeCommandHandler = new ActionHandler(goToTimeAction);
	}

	@Override
	public void contributeToMenu(final IMenuManager menuManager) {
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

	@Override
	public void dispose() {		
	}

	@Override
	public void init(final IActionBars bars, final IWorkbenchPage page) {
		super.init(bars, page);
	}

	@Override
	public void setActiveEditor(final IEditorPart editor) {
		super.setActiveEditor(editor);
		
		TraceEditor traceEditor = (TraceEditor) editor;
		
		zoomModel = traceEditor.getZoomModel();
		TraceModel traceModel = traceEditor.getTraceModel();
		
		zoomInAction.updateModel(traceModel, zoomModel);
		zoomOutAction.updateModel(traceModel, zoomModel);
		zoomBackAction.updateModel(traceModel, zoomModel);
		zoomFitAction.updateModel(traceModel, zoomModel);		
		nextAction.updateModel(traceModel, zoomModel);
		previousAction.updateModel(traceModel, zoomModel);
		goToTimeAction.updateModel(traceModel, zoomModel);
		
		final IHandlerService service = (IHandlerService) editor
				.getEditorSite().getService(IHandlerService.class);

		service.activateHandler(zoomInAction.getActionDefinitionId(), zoomInCommandHandler);
		service.activateHandler(zoomOutAction.getActionDefinitionId(),zoomOutCommandHandler);
		service.activateHandler(zoomBackAction.getActionDefinitionId(), zoomBackCommandHandler);
		service.activateHandler(zoomFitAction.getActionDefinitionId(), zoomFitCommandHandler);
		service.activateHandler(nextAction.getActionDefinitionId(), nextCommandHandler);
		service.activateHandler(previousAction.getActionDefinitionId(), previousCommandHandler);
		service.activateHandler(goToTimeAction.getActionDefinitionId(), goToTimeCommandHandler);
		
		nextAction.setEnabled(false);
		previousAction.setEnabled(false); //Will get enabled in updateActionState(), if necessary
		
		zoomModel.addObserver(this); //Has no effect if the same observer is added twice
		updateActionState();
	}

	/**
	 * Updates the action buttons when a baseline position is changed.
	 * 
	 * @param o
	 *            the <code>Observable</code> calling the update
	 * @param data
	 *            has no effect
	 */
	public void update(Observable o, Object data) {		
		updateActionState();
	}

	private void updateActionState() {
		final SampleLine selectedLine = zoomModel.getSelectedLine();
		
		if (selectedLine != null) {
			final double selectTime = zoomModel.getSelectTime();
			
			double startTime = selectedLine.getStartTime();
			double endTime;
			
			// we are ignoring the last sample which is of type END.
			if (selectedLine.getCount() < 2) {
				endTime = -1;
			} else {
				endTime = selectedLine.getSample(selectedLine.getCount() - 2).time;
			}
			
			if (selectTime < endTime) {
				nextAction.setEnabled(true);
			} else {
				nextAction.setEnabled(false);
			}

			if (selectTime > startTime) {
				previousAction.setEnabled(true);
			} else {
				previousAction.setEnabled(false);
			}
		}
	}
}
