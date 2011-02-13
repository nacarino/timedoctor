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

package net.timedoctor.ui.trace.actions;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

/**
 * This class performs displacement of the baseLine according to the time
 * inputted by the user.
 */
public class GoToTimeAction extends TraceAction {
	public static final String ID = "net.timedoctor.ui.actions.GoToTime";

	private IInputValidator inputValidator;
	
	/**
	 * Constructor for GoToTime action
	 * @param label
	 *  		 Name of the action 
	 */
	public GoToTimeAction(final String label) {
		super(label);
		
		inputValidator = new IInputValidator() {
			public String isValid(final String newText) {				
				try {
					Double d = Double.parseDouble(newText);
					if (d < 0) {
						return "Please enter a number >= 0";						
					}
					return null;					
				}
				catch (NumberFormatException e) {
					return "Please enter a decimal number";
				}
			}
		};		
	}

	@Override
	public void run() {
		InputDialog inputDialog = new InputDialog(null, "Go To Time",
				"Enter the time in seconds", "0", inputValidator);
		if (inputDialog.open() == Window.OK) {
			gotoTime(Double.parseDouble(inputDialog.getValue()));
		}
	}
}
