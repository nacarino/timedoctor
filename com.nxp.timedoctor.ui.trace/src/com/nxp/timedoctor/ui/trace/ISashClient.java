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
 * Interface used by SashListeners 
 */
public interface ISashClient {
	
	/**
	 * Returns the minimum sash offset from the left of the parent's client
	 * area.
	 * 
	 * @return the minimum sash offset in pixels
	 */
	int getMinSashOffset();

	/**
	 * Sets the sash offset to the given value.
	 * 
	 * @param offset
	 *            the offset in pixels from the left of the parent's client area
	 */
	void setSashOffset(final int offset);
}
