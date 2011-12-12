package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreepathsToExistingBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class CategoryTreeDropListener implements DropTargetListener {

	private BookmarkView viewer = null;
	private TreeKeyListener keyListener = null;

	public CategoryTreeDropListener(BookmarkView viewer,
			TreeKeyListener keyListener) {
		this.viewer = viewer;
		this.keyListener = keyListener;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		event.detail = DND.DROP_LINK | DND.DROP_COPY;
	}

	@Override
	public void dragLeave(DropTargetEvent event) {

	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {

	}

	@Override
	public void dragOver(DropTargetEvent event) {

	}

	@Override
	public void drop(DropTargetEvent event) {
		try {
			processDropEventWithDragInitiatedFromOutsideTheView(event);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			DropTargetEvent event) throws JavaModelException {

		System.err.println(keyListener.isAltPressed());

		TreePath[] treePath = getTreePath(event);
		TreeNode bookmark = viewer.getModel().getModelHead();

		new AddTreepathsToExistingBookmarkCommand(viewer, bookmark, treePath)
				.execute();
		saveNewTreeModelState();
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
	}

	private TreePath[] getTreePath(DropTargetEvent event) {
		TreeSelection treeSelection = null;
		TreePath[] treePath = null;
		if (event.data instanceof TreeSelection) {
			treeSelection = (TreeSelection) event.data;
			treePath = treeSelection.getPaths();
		}
		return treePath;
	}

	private void saveNewTreeModelState() {
		TreeSerializerFacade.serializeToDefaultLocation(viewer.getView(),
				viewer.getModel());
	}
}
