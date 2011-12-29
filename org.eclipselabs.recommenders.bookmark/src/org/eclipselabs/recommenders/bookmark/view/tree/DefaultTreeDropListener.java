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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreeNodesToExistingBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreeNodesToNewBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreepathsToExistingBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.CreateNewBookmarkAddAsNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
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
		
		manager.reinitializeExpandedStorage();
		manager.addCurrentlyExpandedNodesToStorage();

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

	private void updateView()
	{
		BookmarkView viewer = manager.getActiveBookmarkView();

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
			manager.reinitializeExpandedStorage();
			manager.addCurrentlyExpandedNodesToStorage();
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
		if (test(event))
			return;

		BMNode bookmark = getBookmarkOfDropTarget(event);

		if (didDropOccurInEmptyArea(bookmark)) {
			addToNewBookmark(event);
			return;
		}

		addToExistingBookmark(event, bookmark);

	}

	private boolean test(DropTargetEvent event)
	{
		BookmarkView activeBookmarkView = manager.getActiveBookmarkView();
		List<IStructuredSelection> treeSelections = TreeUtil
				.getTreeSelections(activeBookmarkView.getView());

		LinkedList<BMNode> modelCategories = getModelCategories();

		LinkedList<BMNode> selectedNodes = new LinkedList<BMNode>();
		BMNode target = (BMNode) getTarget(event);

		for (int i = 0; i < treeSelections.size(); i++) {
			BMNode node = (BMNode) treeSelections.get(i);
			if (invalidNodeForOperation(node, target)) {
				return false;
			}

			selectedNodes.add(node);
			// remove a selected category since this node is going to be moved
			modelCategories.remove(node);
		}

		WidgetElementLocation wel = new WidgetElementLocation(event,
				activeBookmarkView);

		reorderCategoryNodesInModel(wel, modelCategories, selectedNodes);

		return true;
	}

	private void reorderCategoryNodesInModel(WidgetElementLocation wel,
			LinkedList<BMNode> modelCategories, LinkedList<BMNode> selectedNodes)
	{
		BMNode target = wel.element;
		LinkedList<BMNode> newCategoryOrder = new LinkedList<BMNode>();

		int modelIndex=0;
		for (BMNode node : modelCategories) {
			if (node != target) {
				newCategoryOrder.add(node);
			}
			else {
				break;
			}
			modelIndex++;
		}
		
		if (wel.isInUpperHalf) {
			for (BMNode node : selectedNodes) {
				newCategoryOrder.add(node);
			}
			for (int i=modelIndex; i < modelCategories.size(); i++){
				newCategoryOrder.add(modelCategories.get(i));
			}
		}
		else { 
			
			newCategoryOrder.add(modelCategories.get(modelIndex));
			
			for (BMNode node : selectedNodes) {
				newCategoryOrder.add(node);
			}
			
			for (int i=(modelIndex+1); i < modelCategories.size(); i++){
				newCategoryOrder.add(modelCategories.get(i));
			}
			
		}

		BMNode root = manager.getModel().getModelRoot();
		root.removeAllChildren();
		
		for (BMNode node: newCategoryOrder) {
			root.addChild(node);
		}
		
		manager.getActiveBookmarkView().updateControls();
		manager.setStoredExpandedNodesForActiveView();
	}

	private boolean invalidNodeForOperation(BMNode node, BMNode target)
	{
		if (!node.isBookmarkNode()) {
			return true;// DUMMY return
		}
		if (node == target) {
			return true;
		}
		return false;
	}

	private LinkedList<BMNode> getModelCategories()
	{
		LinkedList<BMNode> modelCategories = new LinkedList<BMNode>();

		BMNode[] modelCategoryNodes = manager.getModel().getModelRoot()
				.getChildren();
		for (BMNode node : modelCategoryNodes) {
			modelCategories.add(node);
		}
		return modelCategories;
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
		BMNode dropTarget = (BMNode) getTarget(event);

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
		BMNode target = (BMNode) getTarget(event);

		BMNode bookmark = TreeUtil.getBookmarkNode(target);
		BookmarkView viewer = manager.getActiveBookmarkView();

		if (bookmark != null) {
			new AddTreepathsToExistingBookmark(manager, bookmark, treePath)
					.execute();
		}
		else {
			new CreateNewBookmarkAddAsNode(manager, treePath).execute();

		}
		viewer.getManager().addCurrentlyExpandedNodesToStorage();
	}

	private boolean isValidDrop(DropTargetEvent event)
	{
		BookmarkView viewer = manager.getActiveBookmarkView();
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

	class WidgetElementLocation
	{

		boolean isInUpperHalf;
		boolean isInLowerHalf;
		BMNode element;

		public WidgetElementLocation(DropTargetEvent event,
				BookmarkView bookmarView)
		{
			Point p = new Point(event.x, event.y);

			element = (BMNode) getTarget(event);

			TreeViewer viewer = bookmarView.getView();

			Point control = viewer.getTree().toControl(p);

			TreeItem item = viewer.getTree().getItem(control);

			Rectangle bounds = item.getBounds();

			Rectangle upperHalf = new Rectangle(bounds.x, bounds.y,
					bounds.width, bounds.height / 2);

			Rectangle lowerHalf = new Rectangle(bounds.x, bounds.y
					+ (bounds.height / 2), bounds.width, bounds.height / 2);

			isInUpperHalf = upperHalf.contains(control);
			isInLowerHalf = lowerHalf.contains(control);
		}

	}
}