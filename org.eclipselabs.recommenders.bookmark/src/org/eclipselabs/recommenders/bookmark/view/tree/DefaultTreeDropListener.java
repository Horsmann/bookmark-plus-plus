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
		if (performReorderNodeOperation(event)) {
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
		BMNode target = (BMNode) getTarget(event);
		if (target == null) {
			return false;
		}

		BookmarkView activeBookmarkView = manager.getActiveBookmarkView();
		List<IStructuredSelection> treeSelections = TreeUtil
				.getTreeSelections(activeBookmarkView.getView());

		LinkedList<BMNode> silblings = getSilblings(target);

		LinkedList<BMNode> selectedSilblings = new LinkedList<BMNode>();
		LinkedList<BMNode> unselectedSilblings = new LinkedList<BMNode>(
				silblings);

		for (int i = 0; i < treeSelections.size(); i++) {
			BMNode node = (BMNode) treeSelections.get(i);
			if (isInvalidNodeForReorderOperation(node, target)) {
				return false;
			}

			selectedSilblings.add(node);
			unselectedSilblings.remove(node);
		}

		WidgetElementLocation wel = new WidgetElementLocation(event,
				activeBookmarkView);

		reorderNodes(wel, unselectedSilblings, selectedSilblings);

		return true;
	}

	private void reorderNodes(WidgetElementLocation wel,
			LinkedList<BMNode> unselectedSilblings,
			LinkedList<BMNode> selectedSilblings)
	{
		BMNode target = wel.element;
		LinkedList<BMNode> newOrder = new LinkedList<BMNode>();

		int index = moveUnselectedNodesToNewOrderUntilTargetNode(
				unselectedSilblings, newOrder, target);

		if (wel.isInUpperHalf) {
			processDropInUpperHalf(selectedSilblings, unselectedSilblings,
					newOrder, index);
		}
		else {
			processDropInLowerHalf(selectedSilblings, unselectedSilblings,
					newOrder, index);
		}

		setNewOrder(target, newOrder);

		manager.getActiveBookmarkView().updateControls();
		manager.setStoredExpandedNodesForActiveView();
	}

	private void setNewOrder(BMNode target, LinkedList<BMNode> newOrder)
	{
		BMNode parent = target.getParent();
		parent.removeAllChildren();

		for (BMNode node : newOrder) {
			parent.addChild(node);
		}

	}

	private void processDropInLowerHalf(LinkedList<BMNode> selectedSilblings,
			LinkedList<BMNode> unselectedSilblings,
			LinkedList<BMNode> newOrder, int modelIndex)
	{
		newOrder.add(unselectedSilblings.get(modelIndex));

		for (BMNode node : selectedSilblings) {
			newOrder.add(node);
		}

		for (int i = (modelIndex + 1); i < unselectedSilblings.size(); i++) {
			newOrder.add(unselectedSilblings.get(i));
		}

	}

	private void processDropInUpperHalf(LinkedList<BMNode> selectedSilblings,
			LinkedList<BMNode> unselectedSilblings,
			LinkedList<BMNode> newOrder, int modelIndex)
	{
		for (BMNode node : selectedSilblings) {
			newOrder.add(node);
		}
		for (int i = modelIndex; i < unselectedSilblings.size(); i++) {
			newOrder.add(unselectedSilblings.get(i));
		}
	}

	private int moveUnselectedNodesToNewOrderUntilTargetNode(
			LinkedList<BMNode> unselectedSilblings,
			LinkedList<BMNode> newOrder, BMNode target)
	{
		int modelIndex = 0;
		for (BMNode node : unselectedSilblings) {
			if (node != target) {
				newOrder.add(node);
			}
			else {
				break;
			}
			modelIndex++;
		}

		return modelIndex;
	}

	private boolean isInvalidNodeForReorderOperation(BMNode node, BMNode target)
	{
		if (node.getParent() != target.getParent()) {
			return true;
		}
		if (node == target) {
			return true;
		}
		return false;
	}

	private LinkedList<BMNode> getSilblings(BMNode target)
	{
		LinkedList<BMNode> silblings = new LinkedList<BMNode>();

		BMNode[] modelCategoryNodes = target.getParent().getChildren();
		for (BMNode node : modelCategoryNodes) {
			silblings.add(node);
		}
		return silblings;
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
			element = (BMNode) getTarget(event);

			TreeViewer viewer = bookmarView.getView();

			// get the rectangle are of the target control
			Point globalPoint = new Point(event.x, event.y);
			Point controlRelativePoint = viewer.getTree()
					.toControl(globalPoint);
			TreeItem item = viewer.getTree().getItem(controlRelativePoint);
			Rectangle bounds = item.getBounds();

			// divide target rectangle in upper an lower half
			Rectangle upperHalf = new Rectangle(bounds.x, bounds.y,
					bounds.width, bounds.height / 2);
			Rectangle lowerHalf = new Rectangle(bounds.x, bounds.y
					+ (bounds.height / 2), bounds.width, bounds.height / 2);

			// decide in which part drop occurred
			isInUpperHalf = upperHalf.contains(controlRelativePoint);
			isInLowerHalf = lowerHalf.contains(controlRelativePoint);
		}

	}
}