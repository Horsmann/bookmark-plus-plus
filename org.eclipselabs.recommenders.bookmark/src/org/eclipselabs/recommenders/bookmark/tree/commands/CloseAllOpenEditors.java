package org.eclipselabs.recommenders.bookmark.tree.commands;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class CloseAllOpenEditors implements TreeCommand
{

	@Override
	public void execute()
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		for (IWorkbenchWindow window : windows) {
			for (IWorkbenchPage page : window.getPages())
				page.closeAllEditors(true);
		}		
	}

}
