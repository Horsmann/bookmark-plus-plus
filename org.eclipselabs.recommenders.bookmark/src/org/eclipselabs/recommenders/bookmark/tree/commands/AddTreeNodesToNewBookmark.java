package org.eclipselabs.recommenders.bookmark.tree.commands;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class AddTreeNodesToNewBookmark
	implements TreeCommand
{

	private TreeViewer viewer = null;
	private TreeModel model = null;
	private final boolean keepSource;

	public AddTreeNodesToNewBookmark(TreeViewer viewer, TreeModel model,
			boolean keepSource)
	{

		this.viewer = viewer;
		this.model = model;
		this.keepSource = keepSource;
	}

	@Override
	public boolean execute()
	{

		BMNode bookmark = TreeUtil.makeBookmarkNode();
		model.getModelRoot().addChild(bookmark);

		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(viewer);

		LinkedList<BMNode> nodesToUnlink = new LinkedList<BMNode>();

		for (int i = 0; i < selections.size(); i++) {
			BMNode node = (BMNode) selections.get(i);

			BMNode nodeCopy = TreeUtil.copyTreeBelowNode(node, false);

			while (nodeCopy.getParent() != null)
				nodeCopy = nodeCopy.getParent();

			if (TreeUtil.isDuplicate(bookmark, node))
				continue;

			BMNode merged = null;
			if ((merged = TreeUtil.attemptMerge(bookmark, nodeCopy)) != null) {
				nodesToUnlink.add(node);
				TreeUtil.showNodeExpanded(viewer, merged);
				continue;
			}

			bookmark.addChild(nodeCopy);

			TreeUtil.showNodeExpanded(viewer, nodeCopy);
			nodesToUnlink.add(node);

		}

		TreeUtil.showNodeExpanded(viewer, bookmark);

		performUnlink(nodesToUnlink);
		
		return true;
	}

	private void performUnlink(LinkedList<BMNode> nodesToUnlink)
	{
		if (keepSource == false) {
			for (BMNode node : nodesToUnlink) {
				TreeUtil.unlink(node);
			}
		}
	}
}
