/*******************************************************************************
 * Copyright (c) 2006-2013 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.ui.trace;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.RGB;

/**
 * Contains {@link ColorRegistry} for some of the common colors used in
 * TimeDoctor. All methods of this class are declared static and this class
 * cannot be either extended or instantiated. 
 */
public final class Colors {
	public  static final String LIGHT_YELLOW     = "LIGHT_YELLOW";
	private static final RGB    LIGHT_YELLOW_RGB = new RGB(0xFF, 0xFF, 0x99);
	
	public  static final String DARK_BLUE     = "DARK_BLUE";
	private static final RGB    DARK_BLUE_RGB = new RGB(0x0, 0x0, 0x8B);
	
	public  static final String LIGHT_BLUE     = "LIGHT_BLUE";
	private static final RGB    LIGHT_BLUE_RGB = new RGB(0xE7, 0xC6, 0xF2);
	
	public  static final String DARK_GREEN     = "DARK_GREEN";
	private static final RGB    DARK_GREEN_RGB = new RGB(0x0, 0x64, 0x0);
	
	public  static final String DARK_CYAN     = "DARK_CYAN";
	private static final RGB    DARK_CYAN_RGB = new RGB(0x0, 0x8B, 0x8B);

	public  static final String LIGHT_CYAN     = "LIGHT_CYAN";
	private static final RGB    LIGHT_CYAN_RGB = new RGB(0xE0, 0xFF, 0xFF);

	public  static final String DARK_MAGENTA     = "DARK_MAGENTA";
	private static final RGB    DARK_MAGENTA_RGB = new RGB(0x8B, 0x0, 0x8B);

	public  static final String LIGHT_PINK     = "LIGHT_PINK";
	private static final RGB    LIGHT_PINK_RGB = new RGB(0xFF, 0xB6, 0xC1);

	public  static final String DARK_RED     = "DARK_RED";
	private static final RGB    DARK_RED_RGB = new RGB(0x8B, 0x0, 0x0);

	public  static final String MISTY_ROSE     = "MISTY_ROSE";
	private static final RGB    MISTY_ROSE_RGB = new RGB(0xFF, 0xE4, 0xE1);

	public  static final String DARK_VIOLET     = "DARK_VIOLET";
	private static final RGB    DARK_VIOLET_RGB = new RGB(0x94, 0x0, 0xD3);

	public  static final String THISTLE     = "THISTLE";
	private static final RGB    THISTLE_RGB = new RGB(0xD8, 0xBF, 0xD8);

	public  static final String SEA_GREEN     = "SEA_GREEN";
	private static final RGB    SEA_GREEN_RGB = new RGB(0x2E, 0x8B, 0x57);

	public  static final String MINT_CREAM     = "MINT_CREAM";
	private static final RGB    MINT_CREAM_RGB = new RGB(0xF5, 0xFF, 0xFA);

	public  static final String DARK_GOLDENROD     = "DARK_GOLDENROD";
	private static final RGB    DARK_GOLDENROD_RGB = new RGB(0xB8, 0x86, 0x0B);

	public  static final String PALE_GOLDENROD     = "PALE_GOLDENROD";
	private static final RGB    PALE_GOLDENROD_RGB = new RGB(0xEE, 0xE8, 0xAA);
	
	public static final String TASKCOLOR1     = "TASKCOLOR1";
	private static final RGB   TASKCOLOR1_RGB =  new RGB(0XEF, 0X00, 0X00);
	
	public static final String TASKCOLOR2     = "TASKCOLOR2";
	private static final RGB   TASKCOLOR2_RGB =  new RGB(0x00, 0x00, 0xEF);
	
	public static final String TASKCOLOR3     = "TASKCOLOR3";
	private static final RGB   TASKCOLOR3_RGB =  new RGB(0xFF, 0xFF, 0x00);
	
	public static final String TASKCOLOR4     = "TASKCOLOR4";
	private static final RGB   TASKCOLOR4_RGB =  new RGB(0xFF, 0x00, 0xFF);
	
	public static final String TASKCOLOR5     = "TASKCOLOR5";
	private static final RGB   TASKCOLOR5_RGB =  new RGB(0x94, 0x00, 0xD3);
	
	public static final String TASKCOLOR6     = "TASKCOLOR6";
	private static final RGB   TASKCOLOR6_RGB =  new RGB(0XFF, 0XB0, 0X8A);
	
	public static final String TASKCOLOR7     = "TASKCOLOR7";
	private static final RGB   TASKCOLOR7_RGB =  new RGB(0x80, 0xFF, 0xFF);
	
	public static final String TASKCOLOR8     = "TASKCOLOR8";
	private static final RGB   TASKCOLOR8_RGB =  new RGB(0x00, 0xFF, 0x80);
	
	
	private static ColorRegistry colorRegistry = null;
	
	/**
	 * Returns the {@link ColorRegistry}
	 * @return The {@link ColorRegistry} for use in TimeDoctor
	 */
	public static ColorRegistry getColorRegistry() {
		if (colorRegistry == null) {
			colorRegistry = new ColorRegistry();
			initializeColorRegistry(colorRegistry);
		}

		return colorRegistry;
	}
	
	private static void initializeColorRegistry(ColorRegistry colorRegistry) {
		colorRegistry.put(LIGHT_YELLOW, LIGHT_YELLOW_RGB);
		colorRegistry.put(DARK_BLUE, DARK_BLUE_RGB);
		colorRegistry.put(LIGHT_BLUE, LIGHT_BLUE_RGB);
		colorRegistry.put(DARK_GREEN, DARK_GREEN_RGB);
		colorRegistry.put(DARK_CYAN, DARK_CYAN_RGB);
		colorRegistry.put(LIGHT_CYAN, LIGHT_CYAN_RGB);
		colorRegistry.put(DARK_MAGENTA, DARK_MAGENTA_RGB);
		colorRegistry.put(LIGHT_PINK, LIGHT_PINK_RGB);
		colorRegistry.put(DARK_RED, DARK_RED_RGB);
		colorRegistry.put(MISTY_ROSE, MISTY_ROSE_RGB);
		colorRegistry.put(DARK_VIOLET, DARK_VIOLET_RGB);
		colorRegistry.put(THISTLE, THISTLE_RGB);
		colorRegistry.put(SEA_GREEN, SEA_GREEN_RGB);
		colorRegistry.put(MINT_CREAM, MINT_CREAM_RGB);
		colorRegistry.put(DARK_GOLDENROD, DARK_GOLDENROD_RGB);
		colorRegistry.put(PALE_GOLDENROD, PALE_GOLDENROD_RGB);
		
		//Task colors
		colorRegistry.put(TASKCOLOR1, TASKCOLOR1_RGB);
		colorRegistry.put(TASKCOLOR2, TASKCOLOR2_RGB);
		colorRegistry.put(TASKCOLOR3, TASKCOLOR3_RGB);
		colorRegistry.put(TASKCOLOR4, TASKCOLOR4_RGB);
		colorRegistry.put(TASKCOLOR5, TASKCOLOR5_RGB);
		colorRegistry.put(TASKCOLOR6, TASKCOLOR6_RGB);
		colorRegistry.put(TASKCOLOR7, TASKCOLOR7_RGB);
		colorRegistry.put(TASKCOLOR8, TASKCOLOR8_RGB);		
	}
	
	//Prevent instantiation
	private Colors() {		
	}
}
