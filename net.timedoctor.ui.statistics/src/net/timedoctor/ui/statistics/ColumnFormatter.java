/*******************************************************************************
 * Copyright (c) 2007-2012 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NXP Semiconductors B.V. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.ui.statistics;

import java.util.ArrayList;

public class ColumnFormatter {
	private ArrayList<String> stringArray = new ArrayList<String>(10);
	private int maxLength = 0;
	private StringBuilder spaceString = null;
	
	
	public void addString(final String string) {
		stringArray.add(string);
		maxLength = Math.max(string.length(), maxLength);
	}
	
	public String getFormattedString(int index) {
		if (spaceString == null) {
			spaceString = new StringBuilder(maxLength);
			for (int i = 0; i < maxLength; i++)
				spaceString = spaceString.append(" ");
		}
		
		StringBuilder builder = new StringBuilder(maxLength);
		builder = builder.append(stringArray.get(index));
		builder = builder.append(spaceString, 0, maxLength - builder.length());
		
		return builder.toString();
	}
}
