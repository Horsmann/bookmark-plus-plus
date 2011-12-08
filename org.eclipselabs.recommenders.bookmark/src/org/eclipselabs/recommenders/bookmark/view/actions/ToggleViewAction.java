package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class ToggleViewAction extends Action implements SelfEnabling {

	private ViewManager manager;
	private BookmarkView actionTriggeringView;
	private TreeModel model;

	public ToggleViewAction(ViewManager manager, BookmarkView view,
			TreeModel model) {
		this.manager = manager;
		this.actionTriggeringView = view;
		this.model = model;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_TOGGLE_LEVEL));
		this.setToolTipText("Toggles between the default view and the category the selected item is in");
		this.setText("Toggle between levels");
		setEnabledStatus();
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

			manager.reinitializeExpandedStorage();
			manager.addCurrentlyExpandedNodesToStorage();

			BookmarkView activatedView = manager.activateNextView();

			// get newly set view
			activatedView.getView().setInput(null);
			activatedView.getView().setInput(bookmark);
			manager.setStoredExpandedNodesForActiveView();

			activatedView.updateControls();

		} else {
			// Get and remove all nodes that are currently visible from the
			// storage
			Object[] currentlyVisibleNodes = TreeUtil.getAllChildsOfNode(model
					.getModelHead());
			for (Object o : currentlyVisibleNodes) {
				manager.deleteExpandedNodeFromStorage(o);
			}

			// Add those to the storage which are truly expanded since expanded
			// state may have change
			// for any visible node
			manager.addCurrentlyExpandedNodesToStorage();

			model.resetHeadToRoot();

			BookmarkView activatedView = manager.activateNextView();

			activatedView.getView().setInput(null);
			activatedView.getView().setInput(model.getModelHead());

			manager.setStoredExpandedNodesForActiveView();

			activatedView.updateControls();
		}

	}

	@Override
	public void updateEnabledStatus() {

		setEnabledStatus();

	}

	public void setEnabledStatus() {
		if (actionTriggeringView.requiresSelectionForToggle()) {
			if (TreeUtil.getTreeSelections(actionTriggeringView.getView())
					.size() > 0) {
				this.setEnabled(true);
			} else {
				this.setEnabled(false);
			}
		} else {
			this.setEnabled(true);
		}
	}

}
