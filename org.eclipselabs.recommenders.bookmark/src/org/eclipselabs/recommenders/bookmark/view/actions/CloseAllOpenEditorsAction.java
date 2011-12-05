package org.eclipselabs.recommenders.bookmark.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.recommenders.bookmark.Activator;

public class CloseAllOpenEditorsAction extends Action implements SelfEnabling{

	public CloseAllOpenEditorsAction() {
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_CLOSE_ALL_OPEN_EDITORS));
		this.setToolTipText("Closes all currently opened editor windows");
		this.setText("Close Editors");
	}

	@Override
	public void run() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		for (IWorkbenchWindow window : windows) {
			for (IWorkbenchPage page : window.getPages())
				page.closeAllEditors(true);
		}
	}

	@Override
	public void updateEnabledStatus() {
		// TODO Auto-generated method stub
		
	}

}
