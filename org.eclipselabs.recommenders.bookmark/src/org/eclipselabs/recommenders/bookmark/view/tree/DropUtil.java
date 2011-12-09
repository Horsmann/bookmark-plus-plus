package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class DropUtil {

	public static boolean isValidDrop(TreeViewer viewer, DropTargetEvent event) {
		List<IStructuredSelection> selectedList = TreeUtil
				.getTreeSelections(viewer);
		TreeNode target = (TreeNode) getTarget(event);

		for (int i = 0; i < selectedList.size(); i++) {
			TreeNode node = (TreeNode) selectedList.get(i);

			if (TreeUtil.causesRecursion(node, target))
				return false;

			if (node == target)
				return false;

			if (node.isBookmarkNode())
				return false;

		}
		return true;
	}

	private static Object getTarget(DropTargetEvent event) {
		return ((event.item == null) ? null : event.item.getData());
	}
}
