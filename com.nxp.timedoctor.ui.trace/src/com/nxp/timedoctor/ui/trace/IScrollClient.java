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
package com.nxp.timedoctor.ui.trace;

/**
 * Interface used by scrollListeners 
 */
public interface IScrollClient {
	
	/**
	 * Set the scrollbar position
	 * 
	 * @param selection
	 * 		the scrollbar position in pixels
	 */
	void setScroll(int selection);
}
