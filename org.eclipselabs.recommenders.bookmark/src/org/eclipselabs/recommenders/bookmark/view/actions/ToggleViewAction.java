package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class ToggleViewAction
	extends Action
{

	private ViewManager manager;
	private BookmarkView bookmarkView;

	public ToggleViewAction(ViewManager manager)
	{
		this.manager = manager;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_TOGGLE_VIEW));
		this.setToolTipText("Toggles between the default view and the category the selected item is in");
		this.setText("Toggle between levels");
	}

	@Override
	public void run()
	{
		bookmarkView = manager.getActiveBookmarkView();
		TreeModel model = bookmarkView.getModel();

		if (manager.isViewToggled()) {
			setUpDefault(model);
		}
		else {
			setUpCategory(model);
		}

		refreshView();

	}

	private void setUpCategory(TreeModel model)
	{
		if (manager.isViewFlattened()) {

			setNewModelHead(model);

			setUpViewFlattened(model);
		}
		else {

			setNewModelHead(model);

			manager.reinitializeExpandedStorage();
			manager.addCurrentlyExpandedNodesToStorage();

			setUpViewUnflattened(model.getModelHead());
		}
	}

	private void setNewModelHead(TreeModel model)
	{
		BMNode node = getSelectedOrDefaultNode(model);
		BMNode bookmark = getNodesBookmark(node);

		model.setHeadNode(bookmark);
	}

	private BMNode getNodesBookmark(BMNode node)
	{
		BMNode bookmark = TreeUtil.getBookmarkNode(node);
		return bookmark;
	}

	private void setUpDefault(TreeModel model)
	{

		if (manager.isViewFlattened()) {
			model.resetHeadToRoot();
			setUpViewFlattened(model);
		}
		else {

			manager.removeCurrentlyVisibleNodesFromStorage();
			manager.addCurrentlyExpandedNodesToStorage();
			model.resetHeadToRoot();
			setUpViewUnflattened(model.getModelHead());
		}
	}

	private BMNode getSelectedOrDefaultNode(TreeModel model)
	{
		List<IStructuredSelection> selection = TreeUtil
				.getTreeSelections(bookmarkView.getView());

		if (selection.isEmpty()) {
			BMNode node = model.getModelRoot().getChildren()[0];
			return node;
		}

		BMNode node = (BMNode) selection.get(0);
		return node;
	}

	private void setUpViewUnflattened(BMNode input)
	{
		BookmarkView activatedView = manager.activateNextView();

		activatedView.getView().setInput(null);
		activatedView.getView().setInput(input);

		manager.setStoredExpandedNodesForActiveView();
		activatedView.updateControls();
	}

	private void setUpViewFlattened(TreeModel model)
	{
		BookmarkView activatedView = manager.activateNextView();
		manager.activateFlattenedModus(model.getModelHead());
		activatedView.updateControls();
	}

	private void refreshView()
	{
		TreeViewer viewer = manager.getActiveBookmarkView().getView();
		viewer.refresh(true);
	}
}
