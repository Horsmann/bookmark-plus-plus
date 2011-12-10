package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;

public class ViewPartListener implements IPartListener2 {

	private BookmarkView viewer;

	public ViewPartListener(BookmarkView viewer) {
		this.viewer = viewer;
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		saveNewTreeModelState();
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {

	}
	
	private void saveNewTreeModelState() {
		TreeSerializerFacade.serializeToDefaultLocation(viewer.getView(), viewer.getModel());		
	}

}
