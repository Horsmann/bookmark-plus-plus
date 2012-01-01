package org.eclipselabs.recommenders.bookmark.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ExpandedStorage;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class CreateNewBookmarkAction
	extends Action
{

	private final ViewManager manager;

	public CreateNewBookmarkAction(ViewManager manager)
	{
		this.manager = manager;

		setProperties();
	}

	private void setProperties()
	{
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_BOOKMARK));
		this.setToolTipText("Creates a new bookmark");
		this.setText("New Bookmark");
	}

	@Override
	public void run()
	{
		BookmarkView viewer = manager.getActiveBookmarkView();

		BMNode bookmark = TreeUtil.makeBookmarkNode();
		TreeModel model = manager.getModel();
		model.getModelRoot().addChild(bookmark);

		boolean isToggled = manager.isViewToggled();
		if (isToggled) {
			setNewlyCreatedNodeAsCurrentCategory(bookmark);
		}

		boolean isFlattened = manager.isViewFlattened();
		if (isFlattened) {
			rebuildFlatModel();
		}

		ExpandedStorage storage = manager.getExpandedStorage();
		storage.addNodeToExpandedStorage(bookmark);

		viewer.updateControls();

		manager.saveModelState();
	}

	private void rebuildFlatModel()
	{
		TreeModel model = manager.getModel();
		BMNode currentHead = model.getModelHead();
		manager.activateFlattenedModus(currentHead);
	}

	private void setNewlyCreatedNodeAsCurrentCategory(BMNode bookmark)
	{
		BookmarkView viewer = manager.getActiveBookmarkView();
		TreeModel model = manager.getModel();
		ExpandedStorage storage = manager.getExpandedStorage();

		model.setHeadNode(bookmark);
		TreeViewer view = viewer.getView();
		view.setInput(null);
		view.setInput(model.getModelHead());
		
		storage.expandStoredNodesForActiveView();

		if (manager.isViewFlattened()) {
			manager.activateFlattenedModus(bookmark);
		}

	}

}
