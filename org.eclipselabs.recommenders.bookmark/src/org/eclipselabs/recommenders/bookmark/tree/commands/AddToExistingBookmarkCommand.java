package org.eclipselabs.recommenders.bookmark.tree.commands;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class AddToExistingBookmarkCommand implements TreeCommand {

	private TreeViewer viewer = null;
	private TreeNode bookmark = null;
	private TreePath[] treePath = null;

	public AddToExistingBookmarkCommand(TreeViewer viewer, TreeNode bookmark,
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
					TreeUtil.showNodeExpanded(viewer, added);
				}
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}
}
