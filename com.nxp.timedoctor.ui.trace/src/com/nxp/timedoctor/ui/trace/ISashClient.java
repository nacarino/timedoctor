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
 * Interface for sash clients.
 */
public interface ISashClient {
	
	/**
	 * Gets the client's minimum sash offset.
	 * @return minimum sash offset
	 */
	int getMinSashOffset();

	/**
	 * Sets the client's sash offset.
	 * @param offset the new offset value
	 */
	void setSashOffset(int offset);
	
	void update();
}
