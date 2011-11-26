package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.view.save_restore.SaveBookmarksToLocalDefaultFile;

public class PartListener implements IPartListener {

	private TreeViewer viewer;
	private TreeModel model;

	public PartListener(TreeViewer viewer, TreeModel model) {
		this.viewer = viewer;
		this.model = model;
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		// TODO Auto-generated method stub

	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		// TODO Auto-generated method stub

	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		new SaveBookmarksToLocalDefaultFile(viewer, model).saveCurrentState();
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		new SaveBookmarksToLocalDefaultFile(viewer, model).saveCurrentState();
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		// TODO Auto-generated method stub

	}

}
