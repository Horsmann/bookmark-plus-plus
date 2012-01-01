package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class DropUtil
{

	public static Object getTarget(DropTargetEvent event)
	{
		return ((event.item == null) ? null : event.item.getData());
	}

	public static boolean isValidDrop(TreeViewer viewer, BMNode target)
	{
		List<IStructuredSelection> selectedList = TreeUtil
				.getTreeSelections(viewer);

		for (int i = 0; i < selectedList.size(); i++) {
			BMNode node = (BMNode) selectedList.get(i);

			if (TreeUtil.causesRecursion(node, target)) {
				return false;
			}

			if (node == target) {
				return false;
			}

		}
		return true;
	}

}
