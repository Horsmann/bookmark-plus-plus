package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreepathsToExistingBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;

public class CategoryTreeDropListener implements DropTargetListener {

	private TreeViewer viewer = null;
	private TreeModel model = null;

	public CategoryTreeDropListener(TreeViewer viewer, TreeModel model) {
		this.viewer = viewer;
		this.model = model;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		event.detail = DND.DROP_LINK;
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

		TreePath[] treePath = getTreePath(event);
		TreeNode bookmark = model.getModelHead();

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
		TreeSerializerFacade.serializeToDefaultLocation(viewer, model);
	}
}
