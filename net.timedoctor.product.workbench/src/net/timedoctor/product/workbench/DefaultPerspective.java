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
package net.timedoctor.product.workbench;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import net.timedoctor.ui.ITimeDoctorUIConstants;

public class DefaultPerspective implements IPerspectiveFactory {

	public void createInitialLayout(final IPageLayout layout) {
		layout.addShowViewShortcut(ITimeDoctorUIConstants.TASK_STATISTICS_VIEW);
		layout.addShowViewShortcut(ITimeDoctorUIConstants.ALL_TASK_STATISTICS_VIEW);
	}
}
