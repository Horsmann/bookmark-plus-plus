package org.eclipselabs.recommenders.bookmark.tree.commands;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class ReOrderNodes
	implements TreeCommand
{
	private final ViewManager manager;
	private final DropTargetEvent event;

	public ReOrderNodes(ViewManager manager, DropTargetEvent event)
	{
		this.manager = manager;
		this.event = event;
	}

	public boolean isValidDragforReordering()
	{

		BMNode target = (BMNode) getTarget(event);
		if (target == null) {
			return false;
		}

		BookmarkView activeBookmarkView = manager.getActiveBookmarkView();
		List<IStructuredSelection> treeSelections = TreeUtil
				.getTreeSelections(activeBookmarkView.getView());

		for (int i = 0; i < treeSelections.size(); i++) {
			BMNode node = (BMNode) treeSelections.get(i);
			if (isInvalidNodeForReorderOperation(node, target)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean execute()
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
		manager.getExpandedStorage().expandStoredNodesForActiveView();
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
