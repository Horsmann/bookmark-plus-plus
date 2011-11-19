package org.eclipselabs.recommenders.bookmark.tree.listener;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class TreeDoubleclickListener implements IDoubleClickListener {

	private Action action = null;

	public TreeDoubleclickListener(final Action action) {
		this.action = action;
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {

		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		if (((TreeNode) selection.getFirstElement()).isBookmarkNode())
			return;

		action.run();
	}
}
