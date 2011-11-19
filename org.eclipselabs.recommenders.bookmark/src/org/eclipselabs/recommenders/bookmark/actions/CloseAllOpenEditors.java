package org.eclipselabs.recommenders.bookmark.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.recommenders.bookmark.Activator;

public class CloseAllOpenEditors extends Action {

	public CloseAllOpenEditors() {
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_CLOSE_ALL_OPEN_EDITORS));
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

}
