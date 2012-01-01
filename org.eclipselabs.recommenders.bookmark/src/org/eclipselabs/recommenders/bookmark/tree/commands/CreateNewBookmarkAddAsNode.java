package org.eclipselabs.recommenders.bookmark.tree.commands;

import org.eclipse.jface.viewers.TreePath;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class CreateNewBookmarkAddAsNode
	implements TreeCommand
{

	private TreeCommand addToExistingBookmarkCommand = null;
	private BMNode bookmark = null;
	private final ViewManager manager;

	public CreateNewBookmarkAddAsNode(ViewManager manager, TreePath[] treePath)
	{

		this.manager = manager;
		this.bookmark = TreeUtil.makeBookmarkNode();

		addToExistingBookmarkCommand = new AddTreepathsToExistingBookmark(
				manager, this.bookmark, treePath);

	}

	@Override
	public boolean execute()
	{

		manager.getModel().getModelRoot().addChild(bookmark);

		addToExistingBookmarkCommand.execute();
		return true;
	}

}
