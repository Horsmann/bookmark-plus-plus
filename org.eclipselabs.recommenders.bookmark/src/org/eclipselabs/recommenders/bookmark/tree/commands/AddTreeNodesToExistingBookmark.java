package org.eclipselabs.recommenders.bookmark.tree.commands;

import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class AddTreeNodesToExistingBookmark implements TreeCommand {

	private BookmarkView viewer;
	private BMNode bookmark;
	private BMNode node;
	private boolean keepSourceNode;

	public AddTreeNodesToExistingBookmark(BookmarkView viewer,
			BMNode bookmark, BMNode node, boolean keepSourceNode) {
		this.viewer = viewer;
		this.bookmark = bookmark;
		this.node = node;
		this.keepSourceNode = keepSourceNode;
	}

	@Override
	public void execute() {

		BMNode nodeCopy = TreeUtil.copyTreePathBelowBookmark(node);

		if (TreeUtil.isDuplicate(bookmark, nodeCopy))
			return;

		BMNode merged = null;
		if ((merged = TreeUtil.attemptMerge(bookmark, nodeCopy)) != null) {

			if (!keepSourceNode) {
				TreeUtil.unlink(node);
			}
			
			TreeUtil.showNodeExpanded(viewer.getView(), merged);
			return;
		}

		if (!keepSourceNode) {
			node.getParent().removeChild(node);
		}
		
		BMNode head = TreeUtil.climbUpUntilLevelBelowBookmark(nodeCopy);
		bookmark.addChild(head);

		TreeUtil.showNodeExpanded(viewer.getView(), head);

	}

}
