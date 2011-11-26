package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.view.save_restore.SaveModelToLocalDefaultFile;

public class BookmarkViewPartListener implements IPartListener {

	private TreeViewer viewer;
	private TreeModel model;

	public BookmarkViewPartListener(TreeViewer viewer, TreeModel model) {
		this.viewer = viewer;
		this.model = model;
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		// TODO Auto-generated method stub

	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		new SaveModelToLocalDefaultFile(viewer, model).saveChanges();
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		new SaveModelToLocalDefaultFile(viewer, model).saveChanges();

	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		// TODO Auto-generated method stub

	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		// TODO Auto-generated method stub

	}
}
