package org.eclipselabs.recommenders.bookmark.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.commands.CloseAllOpenEditors;

public class CloseAllOpenEditorsAction extends Action implements SelfEnabling{

	public CloseAllOpenEditorsAction() {
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_CLOSE_ALL_OPEN_EDITORS));
		this.setToolTipText("Closes all currently opened editor windows");
		this.setText("Close Editors");
	}

	@Override
	public void run() {
		new CloseAllOpenEditors().execute();
	}

	@Override
	public void updateEnabledStatus() {
		// TODO Auto-generated method stub
		
	}

}
