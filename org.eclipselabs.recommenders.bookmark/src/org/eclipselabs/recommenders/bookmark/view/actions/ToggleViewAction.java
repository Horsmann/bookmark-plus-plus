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

		// List<IStructuredSelection> selection = TreeUtil
		// .getTreeSelections(actionTriggeringView.getView());

		TreeModel model = null;
//		if (manager.isViewFlattened()) {
//			model = actionTriggeringView.getFlatModel();
//		}
//		else {
			model = actionTriggeringView.getModel();
//		}

		if (manager.isViewToggled()) {
			System.out.println("Category changes to Default");
			if (manager.isViewFlattened()) {
				System.out.println("Default will be flattened");
				model.resetHeadToRoot();

				BookmarkView activatedView = manager.activateNextView();
				manager.activateFlattenedView(model.getModelHead());
				activatedView.updateControls();
			}
			else {
				System.out.println("Default will be normal");
				Object[] currentlyVisibleNodes = TreeUtil
						.getTreeBelowNode(model.getModelHead());
				for (Object o : currentlyVisibleNodes) {
					manager.deleteExpandedNodeFromStorage(o);
				}
				manager.addCurrentlyExpandedNodesToStorage();

				model.resetHeadToRoot();

				BookmarkView activatedView = manager.activateNextView();

				activatedView.getView().setInput(null);
				activatedView.getView().setInput(model.getModelHead());

				manager.setStoredExpandedNodesForActiveView();
				activatedView.updateControls();
			}
		}
		else {
			System.out.println("Default changes to Category");
			if (manager.isViewFlattened()) {
				System.out.println("Category will be flattened");
				List<IStructuredSelection> selection = TreeUtil
						.getTreeSelections(actionTriggeringView.getView());

				if (selection.size() <= 0) {
					return;
				}
				Object object = selection.get(0);
				BMNode node = (BMNode) object;
				BMNode referencedNode = node.getReference();
				BMNode bookmark = TreeUtil.getBookmarkNode(referencedNode);

				model.setHeadNode(bookmark);
				BookmarkView activatedView = manager.activateNextView();
				manager.activateFlattenedView(model.getModelHead());
				activatedView.updateControls();
			}
			else {
				System.out.println("Category will be normal");

				List<IStructuredSelection> selection = TreeUtil
						.getTreeSelections(actionTriggeringView.getView());

				if (selection.size() <= 0) {
					return;
				}

				Object object = selection.get(0);

				BMNode node = (BMNode) object;
				BMNode bookmark = TreeUtil.getBookmarkNode(node);
				model.setHeadNode(bookmark);

				manager.reinitializeExpandedStorage();
				manager.addCurrentlyExpandedNodesToStorage();

				BookmarkView activatedView = manager.activateNextView();
				activatedView.getView().setInput(null);
				activatedView.getView().setInput(bookmark);
				manager.setStoredExpandedNodesForActiveView();
				activatedView.updateControls();
			}
		}

		// TreeModel model = actionTriggeringView.getModel();
		// if (model.isHeadEqualRoot()) {
		// if (selection.size() <= 0)
		// return;
		//
		// Object object = selection.get(0);
		//
		// BMNode node = (BMNode) object;
		// BMNode bookmark = null;
		// if (node.hasReference()) {
		// bookmark = TreeUtil.getBookmarkNode(node.getReference());
		// }
		// else {
		// bookmark = TreeUtil.getBookmarkNode(node);
		// }
		// model.setHeadNode(bookmark);
		//
		// manager.reinitializeExpandedStorage();
		// manager.addCurrentlyExpandedNodesToStorage();
		//
		// BookmarkView activatedView = manager.activateNextView();
		//
		// if (manager.isViewFlattened()) {
		// manager.activateFlattenedView(bookmark);
		// }
		// else {
		// // get newly set view
		// activatedView.getView().setInput(null);
		// activatedView.getView().setInput(bookmark);
		// manager.setStoredExpandedNodesForActiveView();
		// }
		//
		// activatedView.updateControls();
		//
		// }
		// else {
		// // Get and remove all nodes that are currently visible from the
		// // storage
		//
		// BookmarkView activatedView = null;
		// if (manager.isViewFlattened()) {
		// model.resetHeadToRoot();
		//
		// activatedView = manager.activateNextView();
		// manager.activateFlattenedView(model.getModelHead());
		// }
		// else {
		// Object[] currentlyVisibleNodes = TreeUtil
		// .getTreeBelowNode(model.getModelHead());
		// for (Object o : currentlyVisibleNodes) {
		// manager.deleteExpandedNodeFromStorage(o);
		// }
		// manager.addCurrentlyExpandedNodesToStorage();
		//
		// model.resetHeadToRoot();
		//
		// activatedView = manager.activateNextView();
		//
		// activatedView.getView().setInput(null);
		// activatedView.getView().setInput(model.getModelHead());
		//
		// manager.setStoredExpandedNodesForActiveView();
		// }
		// Object[] currentlyVisibleNodes = TreeUtil.getTreeBelowNode(model
		// .getModelHead());
		// for (Object o : currentlyVisibleNodes) {
		// manager.deleteExpandedNodeFromStorage(o);
		// }

		// Add those to the storage which are truly expanded since expanded
		// state may have change
		// for any visible node
		// manager.addCurrentlyExpandedNodesToStorage();
		//
		// model.resetHeadToRoot();
		//
		// BookmarkView activatedView = manager.activateNextView();
		//
		// activatedView.getView().setInput(null);
		// activatedView.getView().setInput(model.getModelHead());
		//
		// manager.setStoredExpandedNodesForActiveView();

		// activatedView.updateControls();
		// }

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
