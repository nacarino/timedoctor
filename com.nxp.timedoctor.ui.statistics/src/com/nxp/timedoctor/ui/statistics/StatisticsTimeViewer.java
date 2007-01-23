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
package com.nxp.timedoctor.ui.statistics;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.nxp.timedoctor.core.model.statistics.StatisticsTimeModel;

public class StatisticsTimeViewer extends Composite implements Observer {
	private Text startInput;
	private Text endInput;
	
	private StatisticsTimeModel timeModel = null;

	public StatisticsTimeViewer(final Composite parent) {
		super(parent, SWT.NONE);
		
		createContents(parent);		
		init();
	}
	
	private void createContents(final Composite parent) {
		GridLayout parentLayout = new GridLayout(4, false);
		setLayout(parentLayout);
		
		startInput = createTimeSelector(parent, "Start time (sec):");
		endInput = createTimeSelector(parent, "End time (sec):");

		startInput.addModifyListener(new ModifyListener() {			
			public void modifyText(final ModifyEvent e) {
				if (timeModel != null) {
					double time = Double.parseDouble(startInput.getText());
					timeModel.setStartTime(time);
				}				
			}
		});
		
		endInput.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if (timeModel != null) {
					double time = Double.parseDouble(endInput.getText());
					timeModel.setEndTime(time);
				}				
			}
		});
	}
	
	private Text createTimeSelector(final Composite parent, final String labelText) {
		Label timeLabel = new Label(this, SWT.LEFT);
		timeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		timeLabel.setText(labelText);
		
		Text timeInput = new Text(this, SWT.SINGLE);
		GridData timeInputGridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		timeInputGridData.widthHint = 100;
		timeInput.setLayoutData(timeInputGridData);
		
		timeInput.addVerifyListener(new VerifyListener() {
			public void verifyText(final VerifyEvent e) {
				// Only accept positive numbers (e.g. 123.4E-3)
				// (obviously this is not foolproof)
				e.doit = e.text.matches("[0-9.E\\-]*");
			}
		});
		
		return timeInput;
	}

	private void init() {
		startInput.setText("0.0");
		endInput.setText("0.0");
	}
	
	public void setTimeModel(final StatisticsTimeModel timeModel) {
		this.timeModel = timeModel;
		if (timeModel != null) {
			timeModel.addObserver(this);
		}
		else {
			init();
		}
	}

	public void update(final Observable o, final Object arg) {
		setSelection(startInput, timeModel.getStartTime());
		setSelection(endInput, timeModel.getEndTime());
	}	
	
	private void setSelection(final Text timeInput, final double selectTime)	{
		String timeStr = Double.toString(selectTime);
		timeInput.setText(timeStr);
	}
}
