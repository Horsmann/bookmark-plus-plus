package org.eclipselabs.recommenders.bookmark.tree.commands;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class AddTreeNodesToExistingBookmark implements TreeCommand {

	private TreeViewer viewer;
	private TreeNode bookmark;
	private TreeNode node;

	public AddTreeNodesToExistingBookmark(TreeViewer viewer, TreeNode bookmark,
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
			TreeUtil.showNodeExpanded(viewer, merged);
			return;
		}

		node.getParent().removeChild(node);
		TreeNode head = TreeUtil.climbUpUntilLevelBelowBookmark(nodeCopy);
		bookmark.addChild(head);
		
		TreeUtil.showNodeExpanded(viewer, head);

	}

}
