package org.eclipselabs.recommenders.bookmark.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

public class CreateNewBookmarkAction extends Action implements SelfEnabling {
	
	private TreeViewer viewer;
	private TreeModel model;
	
	public CreateNewBookmarkAction(TreeViewer viewer, TreeModel model){
		this.viewer = viewer;
		this.model = model;
		
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_BOOKMARK));
		this.setToolTipText("Creates a new bookmark");
		this.setText("New Bookmark");
	}
	
	@Override
	public void run() {
		TreeNode bookmark = new TreeNode("New Bookmark", true);
		model.getModelRoot().addChild(bookmark);
		viewer.refresh();
	}

	@Override
	public void updateEnabledStatus() {
		// TODO Auto-generated method stub
		
	}

}
