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
package com.nxp.timedoctor.ui.statistics.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.nxp.timedoctor.ui.ITimeDoctorUIConstants;
import com.nxp.timedoctor.ui.statistics.IStatisticsViewPage;


public class CopyAction extends Action {
	private IStatisticsViewPage statisticsPage;
	public CopyAction(final IStatisticsViewPage statisticsPage) {
		this.statisticsPage = statisticsPage;
		
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(ITimeDoctorUIConstants.TD_UI_PLUGIN, 
									ITimeDoctorUIConstants.LOCAL_TOOLBAR_ENABLED_IMG_PATH + "copy.gif"));
		setToolTipText("Copy to clipboard");
	}

	@Override
	public void run() {
		statisticsPage.copyToClipboard();
	}
}
