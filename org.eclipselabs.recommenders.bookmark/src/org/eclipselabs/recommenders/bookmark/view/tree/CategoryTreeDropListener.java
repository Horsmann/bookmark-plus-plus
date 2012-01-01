package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreepathsToExistingBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.ReOrderNodes;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class CategoryTreeDropListener
	implements DropTargetListener
{

	private final ViewManager manager;
	private final TreeDragListener dragListener;

	public CategoryTreeDropListener(ViewManager manager,
			TreeDragListener dragListener)
	{
		this.manager = manager;
		this.dragListener = dragListener;
	}

	@Override
	public void dragEnter(DropTargetEvent event)
	{
		event.detail = DND.DROP_LINK;
	}

	@Override
	public void dragLeave(DropTargetEvent event)
	{

	}

	@Override
	public void dragOperationChanged(DropTargetEvent event)
	{

	}

	@Override
	public void dragOver(DropTargetEvent event)
	{

	}

	@Override
	public void drop(DropTargetEvent event)
	{
		try {
			if (dragListener.isDragInProgress()) {
				processDropEventWithDragFromWithinTheView(event);
			}
			else {
				processDropEventWithDragInitiatedFromOutsideTheView(event);
			}

			manager.saveModelState();
			updateView();
		}
		catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void processDropEventWithDragFromWithinTheView(DropTargetEvent event)
	{
		if (performReorderNodeOperation(event)) {
			return;
		}
	}

	private boolean performReorderNodeOperation(DropTargetEvent event)
	{
		ReOrderNodes reorder = new ReOrderNodes(manager, event);

		if (!reorder.isValidDragforReordering()) {
			return false;
		}

		return reorder.execute();
	}

	private void updateView()
	{
		BookmarkView viewer = manager.getActiveBookmarkView();
		viewer.updateControls();

		/*
		 * The flattened view works with a partial copy of the main model. To
		 * make the change visible, we rebuild the flat-copied model from
		 * scratch
		 */
		if (manager.isViewFlattened()) {
			TreeModel model = manager.getModel();
			BMNode head = model.getModelHead();
			manager.activateFlattenedModus(head);
		}

	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			DropTargetEvent event)
		throws JavaModelException
	{

		TreePath[] treePath = getTreePath(event);
		BMNode bookmark = manager.getModel().getModelHead();

		new AddTreepathsToExistingBookmark(manager, bookmark, treePath)
				.execute();
	}

	@Override
	public void dropAccept(DropTargetEvent event)
	{
	}

	private TreePath[] getTreePath(DropTargetEvent event)
	{
		TreeSelection treeSelection = null;
		TreePath[] treePath = null;
		if (event.data instanceof TreeSelection) {
			treeSelection = (TreeSelection) event.data;
			treePath = treeSelection.getPaths();
		}
		return treePath;
	}

}
