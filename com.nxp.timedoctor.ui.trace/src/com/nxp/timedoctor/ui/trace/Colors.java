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

import org.eclipse.swt.graphics.RGB;

/**
 * Contains RGB color definitions to be used in sections and trace lines. 
 */
public class Colors {
	/**
	 * RGB object to create light yellow for grid lines
	 */
	public static final RGB LIGHT_YELLOW = new RGB(0xFF, 0xFF, 0x99);
	
	/**
	 * RGB object to create dark blue for tasks.
	 */
	public static final RGB DARK_BLUE = new RGB(0x0, 0x0, 0x8B);
	
    /** 
     * RGB object to create light blue for tasks.
     */
	public static final RGB LIGHT_BLUE = new RGB(0xE7, 0xC6, 0xF2);
	
	/**
	 * RGB object to create dark green for isrs.
	 */
	public static final RGB DARK_GREEN = new RGB(0x0, 0x64, 0x0);
	
	/**
	 * RGB object to create dark color for counters.
	 */
	public static final RGB DARK_CYAN = new RGB(0x0, 0x8B, 0x8B);

	/**
	 * RGB object to create light color for counters.
	 */
	public static final RGB LIGHT_CYAN = new RGB(0xE0, 0xFF, 0xFF);

	/**
	 * RGB object to create dark color for events.
	 */
	public static final RGB DARK_MAGENTA = new RGB(0x8B, 0x0, 0x8B);

	/**
	 * RGB object to create light color for events.
	 */
	public static final RGB LIGHT_PINK = new RGB(0xFF, 0xB6, 0xC1);

	/**
	 * RGB object to create dark color for queues.
	 */
	public static final RGB DARK_RED = new RGB(0x8B, 0x0, 0x0);

	/**
	 * RGB object to create light color for queues.
	 */
	public static final RGB MISTY_ROSE = new RGB(0xFF, 0xE4, 0xE1);

	/**
	 * RGB object to create dark color for semaphores.
	 */
	public static final RGB DARK_VIOLET = new RGB(0x94, 0x0, 0xD3);

	/**
	 * RGB object to create light color for semaphores.
	 */
	public static final RGB THISTLE = new RGB(0xD8, 0xBF, 0xD8);

	/**
	 * RGB object to create dark color for agents.
	 */
	public static final RGB SEA_GREEN = new RGB(0x2E, 0x8B, 0x57);

	/**
	 * RGB object to create light color for agents.
	 */
	public static final RGB MINT_CREAM = new RGB(0xF5, 0xFF, 0xFA);

	/**
	 * RGB object to create dark color for notes.
	 */
	public static final RGB DARK_GOLDENROD = new RGB(0xB8, 0x86, 0x0B);

	/**
	 * RGB object to create light color for notes.
	 */
	public static final RGB PALE_GOLDENROD = new RGB(0xEE, 0xE8, 0xAA);

}
