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
 * Interface for classes to register to expand/collapse events of a custom
 * expand bar.
 */
public interface IExpandClient {
	
	/**
	 * Expands the client.
	 *
	 */
	void expand();

	/**
	 * Collapses the client.
	 *
	 */
	void collapse();
}
