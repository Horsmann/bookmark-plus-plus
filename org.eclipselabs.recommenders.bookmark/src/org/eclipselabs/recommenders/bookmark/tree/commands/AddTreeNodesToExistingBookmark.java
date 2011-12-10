package org.eclipselabs.recommenders.bookmark.tree.commands;

import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class AddTreeNodesToExistingBookmark implements TreeCommand {

	private BookmarkView viewer;
	private TreeNode bookmark;
	private TreeNode node;

	public AddTreeNodesToExistingBookmark(BookmarkView viewer, TreeNode bookmark,
			TreeNode node) {
		this.viewer = viewer;
		this.bookmark = bookmark;
		this.node = node;
	}

	@Override
	public void execute() {
		
		TreeNode nodeCopy = TreeUtil.copyTreeBelowBookmark(node);

		if (TreeUtil.isDuplicate(bookmark, nodeCopy))
			return;

		TreeNode merged = null;
		if ((merged = TreeUtil.attemptMerge(bookmark, nodeCopy)) != null) {
			TreeUtil.unlink(node);
			TreeUtil.showNodeExpanded(viewer.getView(), merged);
			return;
		}

		node.getParent().removeChild(node);
		TreeNode head = TreeUtil.climbUpUntilLevelBelowBookmark(nodeCopy);
		bookmark.addChild(head);
		
		TreeUtil.showNodeExpanded(viewer.getView(), head);

	}

}
