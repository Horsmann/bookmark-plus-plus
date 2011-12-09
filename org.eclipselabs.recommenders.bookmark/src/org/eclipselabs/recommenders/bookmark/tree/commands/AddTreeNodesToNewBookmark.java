package org.eclipselabs.recommenders.bookmark.tree.commands;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class AddTreeNodesToNewBookmark implements TreeCommand {

	private TreeViewer viewer = null;
	private TreeModel model = null;

	public AddTreeNodesToNewBookmark(TreeViewer viewer, TreeModel model) {

		this.viewer = viewer;
		this.model = model;
	}

	@Override
	public void execute() {

		TreeNode bookmark = TreeUtil.makeBookmarkNode();
		model.getModelRoot().addChild(bookmark);

		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(viewer);

		LinkedList<TreeNode> nodesToUnlink = new LinkedList<TreeNode>();

		for (int i = 0; i < selections.size(); i++) {
			TreeNode node = (TreeNode) selections.get(i);

			TreeNode nodeCopy = TreeUtil.copyTreePath(node);

			while (nodeCopy.getParent() != null)
				nodeCopy = nodeCopy.getParent();

			if (TreeUtil.isDuplicate(bookmark, node))
				continue;

			TreeNode merged = null;
			if ((merged = TreeUtil.attemptMerge(bookmark, nodeCopy)) != null) {
				nodesToUnlink.add(node);
				TreeUtil.showNodeExpanded(viewer, merged);
				continue;
			}

			bookmark.addChild(nodeCopy);

			TreeUtil.showNodeExpanded(viewer, nodeCopy);
			nodesToUnlink.add(node);
			// TreeUtil.unlink(node);

		}

		TreeUtil.showNodeExpanded(viewer, bookmark);

		for (TreeNode node : nodesToUnlink) {
			TreeUtil.unlink(node);
		}
	}
}
