package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreeNodesToExistingBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreeNodesToNewBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreepathsToExistingBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.CreateNewBookmarkAddAsNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.ReOrderNodes;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ExpandedStorage;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class DefaultTreeDropListener
	implements DropTargetListener
{

	private TreeDragListener dragListener = null;
	private final ViewManager manager;

	public DefaultTreeDropListener(ViewManager manager,
			TreeDragListener localViewsDragListener)
	{
		this.manager = manager;
		this.dragListener = localViewsDragListener;
	}

	@Override
	public void drop(DropTargetEvent event)
	{

		updateExpandedStorage();

		try {
			if (dragListener.isDragInProgress()) {
				processDropEventWithDragFromWithinTheView(event);
			}
			else {
				processDropEventWithDragInitiatedFromOutsideTheView(event);
			}

			manager.saveModelState();

		}
		catch (JavaModelException e) {
			e.printStackTrace();
		}

		updateView();

	}

	private void updateExpandedStorage()
	{
		if (!manager.isViewFlattened()) {
			ExpandedStorage storage = manager.getExpandedStorage();

			storage.reinitialize();
			storage.addCurrentlyExpandedNodes();
		}
	}

	private void updateView()
	{
		BookmarkView viewer = manager.getActiveBookmarkView();
		ExpandedStorage storage = manager.getExpandedStorage();

		viewer.updateControls();

		ViewManager manager = viewer.getManager();

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
		else {
			storage.reinitialize();
			storage.addCurrentlyExpandedNodes();
		}

	}

	@Override
	public void dropAccept(DropTargetEvent event)
	{

		if (!isValidDrop(event)) {
			event.detail = DND.DROP_NONE;
		}
	}

	@Override
	public void dragEnter(DropTargetEvent event)
	{
		if (event.operations == DND.DROP_COPY) {
			event.detail = DND.DROP_COPY;
			return;
		}

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

	private void processDropEventWithDragFromWithinTheView(DropTargetEvent event)
		throws JavaModelException
	{
		if (performReorderNodeOperation(event)) {
			return;
		}

		if (isBookmarkNodeAmongSelections()) {
			return;
		}

		BMNode bookmark = getBookmarkOfDropTarget(event);

		if (didDropOccurInEmptyArea(bookmark)) {
			addToNewBookmark(event);
			return;
		}

		addToExistingBookmark(event, bookmark);

	}

	private boolean performReorderNodeOperation(DropTargetEvent event)
	{
		BookmarkView activeBookmarkView = manager.getActiveBookmarkView();
		List<IStructuredSelection> treeSelections = TreeUtil
				.getTreeSelections(activeBookmarkView.getView());

		LinkedList<BMNode> selectedNodesList = new LinkedList<BMNode>();

		for (int i = 0; i < treeSelections.size(); i++) {
			BMNode node = (BMNode) treeSelections.get(i);
			node = TreeUtil.getReference(node);
			selectedNodesList.add(node);
		}
		BMNode[] selectedNodes = selectedNodesList.toArray(new BMNode[0]);

		BMNode target = (BMNode) DropUtil.getTarget(event);
		target = TreeUtil.getReference(target);

		ReOrderNodes reorder = new ReOrderNodes(manager, event, selectedNodes,
				target);

		if (!reorder.isValidDragforReordering()) {
			return false;
		}

		return reorder.execute();
	}

	private boolean isBookmarkNodeAmongSelections()
	{
		TreeViewer viewer = manager.getActiveBookmarkView().getView();
		List<IStructuredSelection> treeSelections = TreeUtil
				.getTreeSelections(viewer);

		for (int i = 0; i < treeSelections.size(); i++) {
			BMNode node = (BMNode) treeSelections.get(i);
			if (node.isBookmarkNode()) {
				return true;
			}
		}

		return false;
	}

	private void addToNewBookmark(DropTargetEvent event)
	{
		TreeViewer view = manager.getActiveBookmarkView().getView();
		TreeModel model = manager.getModel();

		boolean keepSource = (event.operations == DND.DROP_COPY);
		new AddTreeNodesToNewBookmark(view, model, keepSource).execute();
	}

	private BMNode getBookmarkOfDropTarget(DropTargetEvent event)
	{
		BMNode dropTarget = (BMNode) DropUtil.getTarget(event);

		BMNode bookmarkOfDropTarget = TreeUtil.getBookmarkNode(dropTarget);
		return bookmarkOfDropTarget;
	}

	private void addToExistingBookmark(DropTargetEvent event, BMNode bookmark)
	{
		BookmarkView viewer = manager.getActiveBookmarkView();
		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(viewer.getView());

		for (int i = 0; i < selections.size(); i++) {

			BMNode node = (BMNode) selections.get(i);

			boolean keepSource = (event.operations == DND.DROP_COPY);
			new AddTreeNodesToExistingBookmark(viewer, bookmark, node,
					keepSource).execute();

		}
	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			DropTargetEvent event)
		throws JavaModelException
	{
		TreePath[] treePath = getTreePath(event);
		BMNode target = (BMNode) DropUtil.getTarget(event);

		BMNode bookmark = TreeUtil.getBookmarkNode(target);
		ExpandedStorage storage = manager.getExpandedStorage();

		if (bookmark != null) {
			new AddTreepathsToExistingBookmark(manager, bookmark, treePath)
					.execute();
		}
		else {
			new CreateNewBookmarkAddAsNode(manager, treePath).execute();
		}

		storage.addCurrentlyExpandedNodes();
	}

	private boolean isValidDrop(DropTargetEvent event)
	{
		BookmarkView viewer = manager.getActiveBookmarkView();
		if (dragListener.isDragInProgress()) {
			BMNode target = (BMNode) DropUtil.getTarget(event);
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

}