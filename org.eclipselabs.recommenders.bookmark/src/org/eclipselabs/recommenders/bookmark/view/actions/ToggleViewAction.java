package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class ToggleViewAction
	extends Action
	implements SelfEnabling
{

	private ViewManager manager;
	private BookmarkView actionTriggeringView;

	public ToggleViewAction(ViewManager manager, BookmarkView view)
	{
		this.manager = manager;
		this.actionTriggeringView = view;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_TOGGLE_LEVEL));
		this.setToolTipText("Toggles between the default view and the category the selected item is in");
		this.setText("Toggle between levels");
		setEnabledStatus();
	}

	@Override
	public void run()
	{

		TreeModel model = actionTriggeringView.getModel();

		if (manager.isViewToggled()) {
			setUpDefault(model);
		}
		else {
			setUpCategory(model);
		}
	}

	private void setUpCategory(TreeModel model)
	{
		if (manager.isViewFlattened()) {
			if (noSelectionInView()) {
				return;
			}

			setModelsHeadToCurrentlySelectedNode(model);

			setUpViewFlattened(model);
		}
		else {

			updateExpandedNodesStorage(model);

			if (noSelectionInView()) {
				return;
			}

			setModelsHeadToCurrentlySelectedNode(model);

			manager.reinitializeExpandedStorage();
			manager.addCurrentlyExpandedNodesToStorage();

			setUpViewUnflattened(model.getModelHead());
		}
	}

	private void setModelsHeadToCurrentlySelectedNode(TreeModel model)
	{
		BMNode node = getFirstSelectionInView();
		BMNode bookmark = null;
		if (node.hasReference()) {
			BMNode reference = node.getReference();
			bookmark = TreeUtil.getBookmarkNode(reference);
		}
		else {
			bookmark = TreeUtil.getBookmarkNode(node);
		}
		model.setHeadNode(bookmark);
	}

	private void setUpDefault(TreeModel model)
	{
		model.resetHeadToRoot();

		if (manager.isViewFlattened()) {

			setUpViewFlattened(model);
		}
		else {

			updateExpandedNodesStorage(model);

			setUpViewUnflattened(model.getModelHead());
		}
	}

	private void updateExpandedNodesStorage(TreeModel model)
	{
		Object[] currentlyVisibleNodes = TreeUtil.getTreeBelowNode(model
				.getModelHead());
		for (Object o : currentlyVisibleNodes) {
			manager.deleteExpandedNodeFromStorage(o);
		}
		manager.addCurrentlyExpandedNodesToStorage();
	}

	private BMNode getFirstSelectionInView()
	{
		List<IStructuredSelection> selection = TreeUtil
				.getTreeSelections(actionTriggeringView.getView());
		return (BMNode) selection.get(0);
	}

	private boolean noSelectionInView()
	{
		List<IStructuredSelection> selection = TreeUtil
				.getTreeSelections(actionTriggeringView.getView());

		return (selection.size() <= 0);
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
		// model.setHeadNode(bmNode);

		BookmarkView activatedView = manager.activateNextView();
		manager.activateFlattenedView(model.getModelHead());
		activatedView.updateControls();
	}

	@Override
	public void updateEnabledStatus()
	{

		setEnabledStatus();

	}

	public void setEnabledStatus()
	{
		if (actionTriggeringView.requiresSelectionForToggle()) {
			if (TreeUtil.getTreeSelections(actionTriggeringView.getView())
					.size() > 0) {
				this.setEnabled(true);
			}
			else {
				this.setEnabled(false);
			}
		}
		else {
			this.setEnabled(true);
		}
	}

}
