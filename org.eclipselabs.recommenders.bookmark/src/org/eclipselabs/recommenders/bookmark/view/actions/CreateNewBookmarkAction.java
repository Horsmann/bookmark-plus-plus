package org.eclipselabs.recommenders.bookmark.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class CreateNewBookmarkAction extends Action  {
	
	private BookmarkView viewer;
	private TreeModel model;
	
	public CreateNewBookmarkAction(BookmarkView viewer, TreeModel model){
		this.viewer = viewer;
		this.model = model;
		
		setProperties();
	}
	
	private void setProperties(){
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_BOOKMARK));
		this.setToolTipText("Creates a new bookmark");
		this.setText("New Bookmark");
	}
	
	@Override
	public void run() {
		TreeNode bookmark = TreeUtil.makeBookmarkNode();
		model.getModelRoot().addChild(bookmark);
		viewer.updateControls();
	}

}
