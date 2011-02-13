/*******************************************************************************
 * Copyright (c) 2009 NXP Semiconductors B.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NXP Semiconductors B.V. - initial API and implementation
 *******************************************************************************/
package com.nxp.timedoctor.internal.ui.properties;

import java.text.DecimalFormat;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.nxp.timedoctor.core.model.SampleLine;

/**
 * PropertySource Class
 * 
 * @see org.eclipse.ui.views.properties.IPropertySource
 */
public class SampleLinePropertySource implements IPropertySource {
	private static final String CPU = "CPU";
	private static final String LINE = "Line";

	private static final String CPU_MEM_CLOCKS_PER_SEC = "cpu.memClocksPerSec";
	private static final String CPU_CLOCKS_PER_SEC    = "cpu.clocksPerSec";
	private static final String CPU_NAME              = "cpu.name";
	private static final String CPU_ID                = "cpu.id";
	private static final String LINE_MAX_SAMPLE_VALUE = "line.maxSampleValue";
	private static final String LINE_SAMPLE_COUNT     = "line.sampleCount";
	private static final String LINE_END_TIME         = "line.endTime";
	private static final String LINE_START_TIME       = "line.startTime";
	private static final String LINE_TYPE             = "line.type";
	private static final String LINE_NAME             = "line.name";
	private static final String LINE_ID               = "line.id";
	
	private static IPropertyDescriptor[] descriptors = new IPropertyDescriptor[11];
	
	static {
		PropertyDescriptor descriptor;
		
		//Line id
		descriptor = new PropertyDescriptor(LINE_ID, "ID");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(LINE);
		descriptors[0] = descriptor;
		
		//Line name
		descriptor = new PropertyDescriptor(LINE_NAME, "Name");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(LINE);
		descriptors[1] = descriptor;
		
		//Line type
		descriptor = new PropertyDescriptor(LINE_TYPE, "Type");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(LINE);
		descriptors[2] = descriptor;
		
		//Line start time
		descriptor = new PropertyDescriptor(LINE_START_TIME, "Start Time");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(LINE);
		descriptors[3] = descriptor;
		
		//Line end time
		descriptor = new PropertyDescriptor(LINE_END_TIME, "End Time");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(LINE);
		descriptors[4] = descriptor;
		
		//Line sample count
		descriptor = new PropertyDescriptor(LINE_SAMPLE_COUNT, "Sample Count");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(LINE);
		descriptors[5] = descriptor;
		
		//Line name
		descriptor = new PropertyDescriptor(LINE_MAX_SAMPLE_VALUE, "Maximum Sample Value");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(LINE);
		descriptors[6] = descriptor;
		
		//CPU
		//CPU id
		descriptor = new PropertyDescriptor(CPU_ID, "ID");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(CPU);
		descriptors[7] = descriptor;
		
		//CPU name
		descriptor = new PropertyDescriptor(CPU_NAME, "Name");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(CPU);
		descriptors[8] = descriptor;
		
		//CPU clocks per second
		descriptor = new PropertyDescriptor(CPU_CLOCKS_PER_SEC, "CPU Cycles per second");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(CPU);
		descriptors[9] = descriptor;
		
		//CPU memory clocks per second
		descriptor = new PropertyDescriptor(CPU_MEM_CLOCKS_PER_SEC, "Memory Cycles per second");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setCategory(CPU);
		descriptors[10] = descriptor;
	}
	
	private SampleLine sampleLine = null;

	/**
	 * constructor
	 * 
	 * @param sampleLine
	 */
	public SampleLinePropertySource(SampleLine sampleLine) {
		this.sampleLine = sampleLine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java
	 * .lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		// SampleLine
		if (id.equals(LINE_ID))
			return sampleLine.getID();
		if (id.equals(LINE_NAME))
			return sampleLine.getName();
		if (id.equals(LINE_TYPE))
			return sampleLine.getType();
		if (id.equals(LINE_START_TIME))
			return sampleLine.getStartTime();
		if (id.equals(LINE_END_TIME))
			return sampleLine.getEndTime();
		if (id.equals(LINE_SAMPLE_COUNT))
			return sampleLine.getCount();
		if (id.equals(LINE_MAX_SAMPLE_VALUE))
			return sampleLine.getMaxSampleValue();

		// SampleCPU
		final DecimalFormat df = new DecimalFormat();

		if (id.equals(CPU_ID))
			return sampleLine.getCPU().getID();
		if (id.equals(CPU_NAME))
			return sampleLine.getCPU().getName();
		if (id.equals(CPU_CLOCKS_PER_SEC))
			return df.format(sampleLine.getCPU().getClocksPerSec());
		if (id.equals(CPU_MEM_CLOCKS_PER_SEC))
			return df.format(sampleLine.getCPU().getMemClocksPerSec());

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang
	 * .Object)
	 */
	public boolean isPropertySet(Object id) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java
	 * .lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java
	 * .lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
	}
}
