package com.nxp.timedoctor.product.workbench.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.update.ui.UpdateManagerUI;

import com.nxp.timedoctor.ui.ITimeDoctorUIConstants;

public class FindAndUpdateAction extends Action implements IWorkbenchAction {
	private static final String ID = "com.nxp.timedoctor.ui.update";
	private IWorkbenchWindow window;
	
	public FindAndUpdateAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Find and Install...");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(ITimeDoctorUIConstants.TD_UI_PLUGIN, 
				ITimeDoctorUIConstants.TOOLBAR_ENABLED_IMG_PATH + "usearch_obj.gif"));
	}
	
	public void dispose() {
	}

	@Override
	public void run() {
		BusyIndicator.showWhile(window.getShell().getDisplay(), new Runnable() {
            public void run() {
                UpdateManagerUI.openInstaller(window.getShell());
            }
        });
	}
}
