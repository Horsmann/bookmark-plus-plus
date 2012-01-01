package org.eclipselabs.recommenders.bookmark.tree.commands;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.TreePath;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class AddTreepathsToExistingBookmark
	implements TreeCommand
{

	private BMNode bookmark = null;
	private TreePath[] treePath = null;
	private final ViewManager manager;

	public AddTreepathsToExistingBookmark(ViewManager manager, BMNode bookmark,
			TreePath[] treePath)
	{

		this.manager = manager;
		this.bookmark = bookmark;
		this.treePath = treePath;
	}

	@Override
	public boolean execute()
	{
		try {

			for (int i = 0; i < treePath.length; i++) {
				BMNode added = TreeUtil.addNodesToExistingBookmark(bookmark,
						treePath[i]);
				if (added != null) {
					BookmarkView viewer = manager.getActiveBookmarkView();
					TreeUtil.showNodeExpanded(viewer.getView(), added);
				}
			}

		}
		catch (JavaModelException e) {
			e.printStackTrace();
		}

		return true;
	}
}
