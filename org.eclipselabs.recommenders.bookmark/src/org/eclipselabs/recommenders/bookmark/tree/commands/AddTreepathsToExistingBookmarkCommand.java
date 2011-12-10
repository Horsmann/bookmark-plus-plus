package org.eclipselabs.recommenders.bookmark.tree.commands;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.TreePath;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class AddTreepathsToExistingBookmarkCommand implements TreeCommand {

	private BookmarkView viewer = null;
	private TreeNode bookmark = null;
	private TreePath[] treePath = null;

	public AddTreepathsToExistingBookmarkCommand(BookmarkView viewer, TreeNode bookmark,
			TreePath[] treePath) {

		this.viewer = viewer;
		this.bookmark = bookmark;
		this.treePath = treePath;
	}

	@Override
	public void execute() {
		try {

			for (int i = 0; i < treePath.length; i++) {
				TreeNode added = TreeUtil.addNodesToExistingBookmark(bookmark,
						treePath[i]);
				if (added != null) {
					TreeUtil.showNodeExpanded(viewer.getView(), added);
				}
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}
}
