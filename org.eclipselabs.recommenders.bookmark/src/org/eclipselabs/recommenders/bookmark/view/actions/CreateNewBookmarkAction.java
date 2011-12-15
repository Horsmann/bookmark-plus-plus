package org.eclipselabs.recommenders.bookmark.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class CreateNewBookmarkAction extends Action {

	private BookmarkView viewer;

	public CreateNewBookmarkAction(BookmarkView viewer) {
		this.viewer = viewer;

		setProperties();
	}

	private void setProperties() {
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_BOOKMARK));
		this.setToolTipText("Creates a new bookmark");
		this.setText("New Bookmark");
	}

	@Override
	public void run() {
		BMNode bookmark = TreeUtil.makeBookmarkNode();
		viewer.getModel().getModelRoot().addChild(bookmark);
		viewer.updateControls();

		saveNewTreeModelState();
	}

	private void saveNewTreeModelState() {
		TreeSerializerFacade.serializeToDefaultLocation(viewer.getView(),
				viewer.getModel());
	}

}
