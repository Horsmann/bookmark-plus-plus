package org.eclipselabs.recommenders.bookmark.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class ToggleFlatAndTreeAction
	extends Action
{

	private ViewManager manager;

	public ToggleFlatAndTreeAction(ViewManager manager)
	{
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_TOGGLE_FLAT_HIERARCHY));
		this.setToolTipText("Toggles between tree and a flattened representation");
		this.setText("Toggle flat/tree");

		this.manager = manager;
	}

	@Override
	public void run()
	{
		if (manager.isViewFlattened()) {
			manager.deactivateFlattenedModus();
		}
		else {
			BMNode flattenFromNode = null;
			BookmarkView view = manager.getActiveBookmarkView();
			
			saveExpandedState();
			
			TreeModel model = view.getModel();
			flattenFromNode = model.getModelHead();
			manager.activateFlattenedModus(flattenFromNode);
		}

		refreshView();

	}

	private void saveExpandedState()
	{
		
	}

	private void refreshView()
	{
		TreeViewer viewer = manager.getActiveBookmarkView().getView();
		viewer.refresh(true);
	}
}
