/*******************************************************************************
 * Copyright (c) 2006-2012 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.ui.trace;

/**
 * Interface used by SashListeners 
 */
public interface ISashClient {
	
	/**
	 * Set the sash to its default offset
	 */
	void setDefaultSashOffset();

	/**
	 * Sets the sash offset to the given value.
	 * 
	 * @param offset
	 *            the offset in pixels from the left of the parent's client area
	 * @return true if the setting was accepted, false otherwise            
	 */
	boolean setSashOffset(final int offset);
}
