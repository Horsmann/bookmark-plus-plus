package org.eclipselabs.recommenders.bookmark.tree.commands;

import org.eclipse.jface.viewers.TreePath;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class CreateNewBookmarkAddAsNode implements TreeCommand {

	private TreeCommand addToExistingBookmarkCommand = null;
	private TreeModel model = null;
	private BMNode bookmark = null;
	
	public CreateNewBookmarkAddAsNode(BookmarkView viewer, TreePath[] treePath) {

		this.model = viewer.getModel();
		this.bookmark = TreeUtil.makeBookmarkNode();
		
		addToExistingBookmarkCommand = new AddTreepathsToExistingBookmark(viewer,
				this.bookmark, treePath);

	}

	@Override
	public void execute() {

		model.getModelRoot().addChild(bookmark);

		addToExistingBookmarkCommand.execute();

	}

}
