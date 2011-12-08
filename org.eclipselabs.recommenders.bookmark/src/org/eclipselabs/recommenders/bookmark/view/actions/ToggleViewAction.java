package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.views.BookmarkView;
import org.eclipselabs.recommenders.bookmark.views.ViewManager;

public class ToggleViewAction extends Action implements SelfEnabling {

	private ViewManager manager;
	private BookmarkView actionTriggeringView;
	private TreeModel model;

	HashMap<Object, String> expandedNodes = null;

	public ToggleViewAction(ViewManager manager, BookmarkView view,
			TreeModel model) {
		this.manager = manager;
		this.actionTriggeringView = view;
		this.model = model;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_TOGGLE_LEVEL));
		this.setToolTipText("Toggles between the default view and the category the selected item is in");
		this.setText("Toggle between levels");
	}

	@Override
	public void run() {
		List<IStructuredSelection> selection = TreeUtil
				.getTreeSelections(actionTriggeringView.getView());

		if (model.isHeadEqualRoot()) {
			if (selection.size() <= 0)
				return;

			Object object = selection.get(0);

			TreeNode node = (TreeNode) object;
			TreeNode bookmark = TreeUtil.getBookmarkNode(node);
			model.setHeadNode(bookmark);

			TreeViewer view = manager.getActiveViewer();

			Object[] expanded = view.getExpandedElements();
			expandedNodes = new HashMap<Object, String>();
			AddExpandedNodesToHashMap(expanded);

			manager.activateNextView();

			// get newly set view
			view = manager.getActiveViewer();
			view.setInput(null);
			view.setInput(bookmark);
			view.setExpandedElements(expanded);
		} else {
			TreeViewer view = manager.getActiveViewer();
			Object[] expanded = view.getExpandedElements();
			AddExpandedNodesToHashMap(expanded);
			model.resetHeadToRoot();

			manager.activateNextView();

			view.setInput(null);
			view.setInput(model.getModelHead());
			Object[] allExpanded = getExpandedNodes();
			view.setExpandedElements(allExpanded);
		}

	}

	private void AddExpandedNodesToHashMap(Object[] expanded) {

		if (expandedNodes == null)
			expandedNodes = new HashMap<Object, String>();

		for (Object o : expanded)
			if (expandedNodes.get(o) == null)
				expandedNodes.put(o, "");
	}

	private Object[] getExpandedNodes() {
		return expandedNodes.keySet().toArray();
	}

	@Override
	public void updateEnabledStatus() {

	}

}
