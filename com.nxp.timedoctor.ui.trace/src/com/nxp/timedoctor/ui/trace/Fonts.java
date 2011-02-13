/*******************************************************************************
 * Copyright (c) 2007 NXP Semiconductors B.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NXP Semiconductors B.V. - initial API and implementation
 *******************************************************************************/
package com.nxp.timedoctor.ui.trace;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * Contains {@link FontRegistry} for the common fonts used in TimeDoctor.
 * All methods of this class are declared static and this class cannot be
 * either extended or instantiated.
 *
 */
public final class Fonts {	
	public static final String TRACE_LABEL_FONT     = "TRACE_LABEL_FONT";
	public static final String TRACE_ICON_FONT      = "TRACE_ICON_FONT";
	public static final String HEADER_LOGO_FONT     = "HEADER_LOGO_FONT";
	public static final String SECTION_HEADER_FONT  = "SECTION_HEADER_FONT";
	public static final String RULER_FONT           = "RULER_FONT";
	
	private static final String DEFAULT_FONT_NAME = "Tahoma";
	private static final String RULER_FONT_NAME   = "Arial";
	 
	private static final int SIZE_6 = 6;
	private static final int SIZE_8 = 8;
	
	private static FontRegistry fontRegistry = null;
	
	/**
	 * Returns the {@link FontRegistry}
	 * 
	 * @return The {@link FontRegistry} object
	 */
	public static FontRegistry getFontRegistry() {
		if (fontRegistry == null) {
			fontRegistry = new FontRegistry();
			initializeFontRegistry(fontRegistry);
		}
		
		return fontRegistry;
	}
	
	private static void initializeFontRegistry(FontRegistry fontRegistry) {
		//Trace Icon font
		FontData f = new FontData(DEFAULT_FONT_NAME, SIZE_6, SWT.NORMAL);
		fontRegistry.put(TRACE_ICON_FONT,  new FontData[]{f});
		
		//Trace Label font
		f = new FontData(DEFAULT_FONT_NAME, SIZE_8, SWT.NORMAL);
		fontRegistry.put(TRACE_LABEL_FONT, new FontData[]{f});
		
		//Logo font
		fontRegistry.put(HEADER_LOGO_FONT, new FontData[]{f});
		
		//Section header font
		fontRegistry.put(SECTION_HEADER_FONT, new FontData[]{f});
		
		//Ruler font
		f = new FontData(RULER_FONT_NAME, SIZE_8, SWT.NORMAL);
		fontRegistry.put(RULER_FONT, new FontData[]{f});
	}
	
	//Prevent instantiation
	private Fonts() {		
	}
}
