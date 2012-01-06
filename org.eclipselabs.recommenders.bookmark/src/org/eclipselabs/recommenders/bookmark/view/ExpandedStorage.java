package org.eclipselabs.recommenders.bookmark.view;

import java.util.HashMap;

import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class ExpandedStorage
{
	private HashMap<Object, String> expandedNodes = new HashMap<Object, String>();
	private final ViewManager manager;

	public ExpandedStorage(ViewManager manager)
	{
		this.manager = manager;
	}

	public Object[] getExpandedNodes()
	{
		return expandedNodes.keySet().toArray();
	}

	public void addNodeToExpandedStorage(BMNode node)
	{
		expandedNodes.put(node, "");
	}

	public void addCurrentlyExpandedNodes()
	{
		BookmarkView activeView = manager.getActiveBookmarkView();
		TreeModel model = manager.getModel();

		Object[] expanded = activeView.getView().getExpandedElements();
		for (Object o : expanded) {
			expandedNodes.put(o, "");
		}
		if (!model.isHeadEqualRoot()) {
			expandedNodes.put(model.getModelHead(), "");
		}
	}

	public void expandStoredNodesForActiveView()
	{

		BookmarkView activeView = manager.getActiveBookmarkView();

		Object[] expanded = expandedNodes.keySet().toArray();
		activeView.getView().setExpandedElements(expanded);
	}

	public void removeCurrentlyVisibleNodes()
	{

		TreeModel model = manager.getModel();
		BMNode head = model.getModelHead();

		Object[] currentlyVisibleNodes = TreeUtil.getTreeBelowNode(head);
		for (Object o : currentlyVisibleNodes) {
			expandedNodes.remove(o);
		}
	}
	
	public void removeNode(Object object) {
		expandedNodes.remove(object);
	}

	public void reinitialize()
	{
		expandedNodes = new HashMap<Object, String>();
	}
}
