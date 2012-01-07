package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class ShowInFlatAction
	extends Action
	implements SelfEnabling
{
	private final ViewManager manager;
	private final String hidden = "Hide in flat-modus";
	private final String show = "Show in flat-modus";

	public ShowInFlatAction(ViewManager manager)
	{
		this.manager = manager;
		this.setToolTipText("Makes a node visible in the flattened view");
	}

	@Override
	public void run()
	{
		BookmarkView viewer = manager.getActiveBookmarkView();

		List<IStructuredSelection> list = TreeUtil.getTreeSelections(viewer
				.getView());
		changeFlatStatusOfNodes(list);

		update();
	}

	private void update()
	{
		updateEnabledStatus();

		if (manager.isViewFlattened()) {
			BMNode head = manager.getModel().getModelHead();
			manager.activateFlattenedModus(head);
		}

	}

	private void changeFlatStatusOfNodes(List<IStructuredSelection> list)
	{
		for (int i = 0; i < list.size(); i++) {
			BMNode node = (BMNode) list.get(i);

			node = TreeUtil.getReference(node);

			if (node.isBookmarkNode()) {
				continue;
			}

			if (node.showInFlatModus()) {
				node.setShowInFlatModus(false);
			}
			else {
				node.setShowInFlatModus(true);
			}

		}
	}

	@Override
	public void updateEnabledStatus()
	{
		BookmarkView viewer = manager.getActiveBookmarkView();

		List<IStructuredSelection> list = TreeUtil.getTreeSelections(viewer
				.getView());
		if (isSingleNodeSelected(list)) {
			updateTextForSingleSelection(list);
		}
		else if (areSeveralNodesSelected(list)) {
			updateTextForSeveralSelections(list);
		}

	}

	private void updateTextForSeveralSelections(List<IStructuredSelection> list)
	{
		if (allSelectionsAreCategoryNodes(list)) {
			this.setEnabled(false);
			this.setText("N/A");
		}

		this.setEnabled(true);
		this.setText("Show/Hide in flat");
	}

	private boolean allSelectionsAreCategoryNodes(
			List<IStructuredSelection> list)
	{
		boolean allCategories = true;

		for (int i = 0; i < list.size(); i++) {
			BMNode node = (BMNode) list.get(i);

			if (!node.isBookmarkNode()) {
				allCategories = false;
			}

		}

		return allCategories;
	}

	private boolean areSeveralNodesSelected(List<IStructuredSelection> list)
	{
		return list.size() > 1;
	}

	private void updateTextForSingleSelection(List<IStructuredSelection> list)
	{
		BMNode node = (BMNode) list.get(0);

		if (node.isBookmarkNode()) {
			this.setEnabled(false);
			return;
		}

		this.setEnabled(true);
		if (node.showInFlatModus()) {
			this.setText(hidden);
		}
		else {
			this.setText(show);
		}

	}

	private boolean isSingleNodeSelected(List<IStructuredSelection> list)
	{
		return list.size() == 1 && list.get(0) instanceof BMNode;
	}

}
