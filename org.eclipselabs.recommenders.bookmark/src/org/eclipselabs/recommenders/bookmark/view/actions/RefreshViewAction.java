package org.eclipselabs.recommenders.bookmark.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class RefreshViewAction
	extends Action
{

	private BookmarkView viewer;

	public RefreshViewAction(BookmarkView viewer)
	{
		this.viewer = viewer;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_REFRESH_VIEW));
		this.setToolTipText("Refreshes the view and updates the labeling (available/closed/not available)");
		this.setText("Refresh");
	}

	@Override
	public void run()
	{

		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		boolean removeDeadReferences = preferenceStore
				.getBoolean(org.eclipselabs.recommenders.bookmark.preferences.PreferenceConstants.REMOVE_DEAD_BOOKMARK_REFERENCES_REFRESH);

		if (removeDeadReferences) {
			TreeModel model = viewer.getModel();
			final BMNode root = model.getModelRoot();

			TreeUtil.deleteNodesReferencingToDeadResourcesUnderNode(root, model);
		}

		viewer.getView().refresh(true);
	}

}
