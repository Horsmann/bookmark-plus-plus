package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreeNodesToExistingBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreeNodesToNewBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreepathsToExistingBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.tree.commands.CreateNewBookmarkAddAsNodeCommand;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class DefaultTreeDropListener
	implements DropTargetListener
{

	private final BookmarkView viewer;

	private TreeDragListener dragListener = null;
	private TreeKeyListener keyListener = null;

	public DefaultTreeDropListener(BookmarkView viewer,
			TreeDragListener localViewsDragListener, TreeKeyListener listener)
	{
		this.viewer = viewer;
		this.dragListener = localViewsDragListener;
		this.keyListener = listener;
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

			saveNewTreeModelState();

		}
		catch (JavaModelException e) {
			e.printStackTrace();
		}

		updateView();

	}

	private void updateView()
	{
		viewer.updateControls();

		ViewManager manager = viewer.getManager();

		/*
		 * The flattened view works with a partial copy of the main model. To
		 * make the change visible, we rebuild the flat-copied model from
		 * scratch
		 */
		if (manager.isViewFlattened()) {
			TreeModel model = viewer.getModel();
			BMNode head = model.getModelHead();
			manager.activateFlattenedView(head);
		}

	}

	@Override
	public void dropAccept(DropTargetEvent event)
	{

		if (!isValidDrop(event))
			event.detail = DND.DROP_NONE;
	}

	@Override
	public void dragEnter(DropTargetEvent event)
	{
		event.detail = DND.DROP_LINK | DND.DROP_COPY;
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

	private void saveNewTreeModelState()
	{
		TreeSerializerFacade.serializeToDefaultLocation(viewer.getView(),
				viewer.getModel());
	}

	private void processDropEventWithDragFromWithinTheView(DropTargetEvent event)
		throws JavaModelException
	{

		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(viewer.getView());

		BMNode dropTarget = (BMNode) getTarget(event);

		dropTarget = getReference(dropTarget);

		BMNode bookmarkOfDropTarget = TreeUtil.getBookmarkNode(dropTarget);

		if (didDropOccurInEmptyArea(bookmarkOfDropTarget)) {

			new AddTreeNodesToNewBookmark(viewer.getView(), viewer.getModel())
					.execute();
			return;
		}

		for (int i = 0; i < selections.size(); i++) {

			BMNode node = (BMNode) selections.get(i);
			
			node = getReference(node);

			new AddTreeNodesToExistingBookmark(viewer, bookmarkOfDropTarget,
					node, keyListener.isAltPressed()).execute();

		}

	}

	private BMNode getReference(BMNode node)
	{
		if (node.hasReference()) {
			return node.getReference();
		}
		return node;
	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			DropTargetEvent event)
		throws JavaModelException
	{
		TreePath[] treePath = getTreePath(event);
		BMNode target = (BMNode) getTarget(event);

		if (target.hasReference()) {
			target = target.getReference();
		}

		BMNode bookmark = TreeUtil.getBookmarkNode(target);

		if (bookmark != null) {
			new AddTreepathsToExistingBookmarkCommand(viewer, bookmark,
					treePath).execute();
		}
		else {
			new CreateNewBookmarkAddAsNodeCommand(viewer, treePath).execute();

		}
		viewer.getManager().addCurrentlyExpandedNodesToStorage();
	}

	private boolean isValidDrop(DropTargetEvent event)
	{

		if (dragListener.isDragInProgress()) {
			BMNode target = (BMNode) getTarget(event);
			return DropUtil.isValidDrop(viewer.getView(), target);
		}
		return true;
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

	private boolean didDropOccurInEmptyArea(BMNode node)
	{
		return node == null;
	}

	private Object getTarget(DropTargetEvent event)
	{
		return ((event.item == null) ? null : event.item.getData());
	}
}